package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Manages native rendering of PDF pages from assets using Android's PdfRenderer.
 */
class PdfRenderSession(
    private val parcelFileDescriptor: ParcelFileDescriptor,
    private val pdfRenderer: PdfRenderer,
    val totalPages: Int,
    val fileName: String
) {
    private val bitmapCache = LruCache<Int, Bitmap>(10)

    suspend fun renderPage(pageIndex: Int, scaleFactor: Float = 2.0f): Bitmap? = withContext(Dispatchers.IO) {
        if (pageIndex < 0 || pageIndex >= totalPages) return@withContext null

        synchronized(bitmapCache) {
            val cached = bitmapCache.get(pageIndex)
            if (cached != null && !cached.isRecycled) {
                return@withContext cached
            }
        }

        try {
            val page = synchronized(pdfRenderer) {
                pdfRenderer.openPage(pageIndex)
            }

            val targetWidth = (page.width * scaleFactor).toInt().coerceIn(400, 2400)
            val targetHeight = (page.height * scaleFactor).toInt().coerceIn(600, 3600)

            val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)

            synchronized(pdfRenderer) {
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
            }

            synchronized(bitmapCache) {
                bitmapCache.put(pageIndex, bitmap)
            }

            bitmap
        } catch (e: Exception) {
            Log.e("PdfRenderSession", "Error rendering page $pageIndex", e)
            null
        }
    }

    fun close() {
        try {
            synchronized(bitmapCache) {
                bitmapCache.evictAll()
            }
            pdfRenderer.close()
            parcelFileDescriptor.close()
        } catch (e: Exception) {
            Log.e("PdfRenderSession", "Error closing session", e)
        }
    }
}

object PdfRendererManager {
    private const val TAG = "PdfRendererManager"

    /**
     * Attempts to open an asset PDF file and prepare a render session.
     */
    suspend fun openAssetPdf(context: Context, assetPath: String): PdfRenderSession? = withContext(Dispatchers.IO) {
        try {
            val safeName = assetPath.replace("/", "_").replace("\\", "_").replace(" ", "_")
            val cacheFile = File(context.cacheDir, "pdf_cache_$safeName")

            // Copy from assets to cache file if needed
            context.assets.open(assetPath).use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }

            val pfd = ParcelFileDescriptor.open(cacheFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)
            val pageCount = renderer.pageCount

            Log.i(TAG, "Successfully loaded asset PDF: $assetPath with $pageCount pages")
            PdfRenderSession(
                parcelFileDescriptor = pfd,
                pdfRenderer = renderer,
                totalPages = pageCount,
                fileName = File(assetPath).name
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open asset PDF: $assetPath", e)
            null
        }
    }

    /**
     * Lists all PDF files located in assets, including the 'libros', 'PDF LIBROS', and root assets folders.
     */
    fun discoverAssetPdfs(context: Context): List<String> {
        val list = mutableListOf<String>()
        try {
            // Check in 'libros'
            val libros = context.assets.list("libros") ?: emptyArray()
            for (f in libros) {
                if (f.endsWith(".pdf", ignoreCase = true)) {
                    list.add("libros/$f")
                }
            }

            // Check in 'PDF LIBROS'
            val pdfLibros = context.assets.list("PDF LIBROS") ?: emptyArray()
            for (f in pdfLibros) {
                if (f.endsWith(".pdf", ignoreCase = true) && !list.contains("PDF LIBROS/$f")) {
                    list.add("PDF LIBROS/$f")
                }
            }

            // Check in root assets
            val rootAssets = context.assets.list("") ?: emptyArray()
            for (f in rootAssets) {
                if (f.endsWith(".pdf", ignoreCase = true) && !list.contains(f)) {
                    list.add(f)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error discovering asset PDFs", e)
        }
        return list
    }
}

package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

object AppImageHelper {
    private val memoryCache = ConcurrentHashMap<String, Bitmap>()

    suspend fun loadTransparentBitmap(context: Context, assetPath: String): Bitmap? {
        memoryCache[assetPath]?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                // Try candidate paths to guarantee finding the user's asset
                val candidatePaths = listOf(
                    assetPath,
                    assetPath.replace("imágenes/", "imagenes/"),
                    assetPath.replace("imagenes/", "imágenes/"),
                    if (!assetPath.contains("/")) "imagenes/$assetPath" else assetPath,
                    assetPath.replace(" ", "_"),
                    assetPath.replace("_", " "),
                    "imagenes/icono_1_new.png",
                    "imagenes/icono_2_new.png",
                    "imagenes/icono_3_new.png",
                    "imagenes/icono 1 new.png",
                    "imagenes/icono 2 new.png",
                    "imagenes/icono 3 new.png"
                ).distinct()

                var originalBitmap: Bitmap? = null
                for (path in candidatePaths) {
                    try {
                        val inputStream = context.assets.open(path)
                        originalBitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()
                        if (originalBitmap != null) break
                    } catch (_: Exception) {
                    }
                }

                if (originalBitmap == null) return@withContext null

                val width = originalBitmap.width
                val height = originalBitmap.height
                val transparentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                val pixels = IntArray(width * height)
                originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

                // Check if already has transparent pixels
                var hasAlpha = false
                for (p in pixels) {
                    if (((p shr 24) and 0xFF) < 250) {
                        hasAlpha = true
                        break
                    }
                }

                if (hasAlpha) {
                    // Image already has transparency, use directly
                    transparentBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
                } else {
                    // Remove white / light background with smooth antialiasing
                    val threshold = 238
                    val upperThreshold = 250

                    for (i in pixels.indices) {
                        val pixel = pixels[i]
                        val alpha = (pixel shr 24) and 0xFF
                        val r = (pixel shr 16) and 0xFF
                        val g = (pixel shr 8) and 0xFF
                        val b = pixel and 0xFF

                        val minChannel = min(r, min(g, b))

                        if (minChannel >= upperThreshold) {
                            pixels[i] = 0x00000000
                        } else if (minChannel >= threshold) {
                            val factor = (upperThreshold - minChannel).toFloat() / (upperThreshold - threshold).toFloat()
                            val newAlpha = (alpha * factor).toInt().coerceIn(0, 255)
                            pixels[i] = (newAlpha shl 24) or (r shl 16) or (g shl 8) or b
                        } else {
                            pixels[i] = pixel
                        }
                    }
                    transparentBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
                }

                originalBitmap.recycle()
                memoryCache[assetPath] = transparentBitmap
                transparentBitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

@Composable
fun TransparentAssetImage(
    assetPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current
    var bitmap by remember(assetPath) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(assetPath) {
        bitmap = AppImageHelper.loadTransparentBitmap(context, assetPath)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        bitmap?.let { b ->
            Image(
                bitmap = b.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = Modifier.matchParentSize(),
                contentScale = contentScale
            )
        }
    }
}

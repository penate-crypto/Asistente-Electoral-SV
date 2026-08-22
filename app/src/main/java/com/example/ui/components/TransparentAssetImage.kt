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
                val inputStream = context.assets.open(assetPath)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                if (originalBitmap == null) return@withContext null

                val width = originalBitmap.width
                val height = originalBitmap.height
                val transparentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                val pixels = IntArray(width * height)
                originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

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
                        // Completely white background -> fully transparent
                        pixels[i] = 0x00000000
                    } else if (minChannel >= threshold) {
                        // Transition edge -> feather alpha for pristine crisp borders
                        val factor = (upperThreshold - minChannel).toFloat() / (upperThreshold - threshold).toFloat()
                        val newAlpha = (alpha * factor).toInt().coerceIn(0, 255)
                        pixels[i] = (newAlpha shl 24) or (r shl 16) or (g shl 8) or b
                    } else {
                        // Original colored pixel preserved exactly
                        pixels[i] = pixel
                    }
                }

                transparentBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
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

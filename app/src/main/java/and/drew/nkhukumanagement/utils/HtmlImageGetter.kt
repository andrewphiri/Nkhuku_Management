package and.drew.nkhukumanagement.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.widget.TextView
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HtmlImageGetter(
    private val scope: CoroutineScope,
    private val res: Resources,
    private val coil: ImageRequest.Builder,
    private val textView: TextView
) : Html.ImageGetter {
    override fun getDrawable(source: String?): Drawable {
        val holder = BitmapDrawablePlaceHolder(res, null)

        scope.launch(Dispatchers.IO) {
            runCatching {
                val request = coil
                    .data(source)
                    .target {  }
                    .build()
                val result = request.context.imageLoader.execute(request)

                val bitmap: Bitmap? = if (result is SuccessResult) {
                    (result.drawable as BitmapDrawable).bitmap
                } else {
                    null
                }
                val drawable = BitmapDrawable(res, bitmap)
                val width =  drawable.intrinsicWidth
                val height = drawable.intrinsicHeight
                drawable.setBounds(0,0, width, height)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.bitmap = drawable.bitmap
                } else {
                    holder.setDrawable(drawable)
                    holder.setBounds(0,0, width, height)

                    withContext(Dispatchers.Main) {
                        textView.text = textView.text
                        }
                }
            }
        }
        return holder
    }

    internal class BitmapDrawablePlaceHolder(res: Resources, bitmap: Bitmap?) : BitmapDrawable(res, bitmap) {
       private var drawable: Drawable? = null
        override fun draw(canvas: Canvas) {
            drawable?.run { draw(canvas) }
        }

        fun setDrawable(drawable: Drawable) {
            this.drawable = drawable
        }
    }
}

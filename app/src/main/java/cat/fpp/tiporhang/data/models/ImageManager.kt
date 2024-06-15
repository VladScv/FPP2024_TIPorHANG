package cat.fpp.tiporhang.data.models

import android.content.Context
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ImageView
import cat.fpp.tiporhang.data.models.utils.ImageIndex
import cat.fpp.tiporhang.data.models.utils.ImageIndex.*

class ImageManager private constructor(context: Context, private val frameLayout: FrameLayout) {
    private val imageViews: MutableList<ImageView> = mutableListOf()

    init {
        for (resId in ImageIndex.entries) {
            val imageView = ImageView(context)
            imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
            )
            imageView.visibility = INVISIBLE
            imageView.setImageResource(resId.src)
            imageViews.add(imageView)
            frameLayout.addView(imageView)
        }

    }
    //Workaround to force updating images without remaining views
    fun reloadImage() {
        frameLayout.removeAllViews()
        for (img in imageViews) {
            frameLayout.addView(img)
        }
    }
    /** Updates image frames visibility,
     *  FRAME0 and TITLE are treated separately to avoid superposition
     */
    fun updateFrame(frame: ImageIndex) {
        when (frame) {
            FRAME0, TITLE -> {
                for (img in imageViews) img.visibility = INVISIBLE
                imageViews[frame.value].visibility = VISIBLE
            }
            else -> {
                imageViews[frame.value].visibility = VISIBLE
            }
        }
        reloadImage()
    }
//Allows to use same object instance from different contexts (MainActivity & GameManager)
    companion object {
        @Volatile
        private var INSTANCE: ImageManager? = null

        fun getInstance(context: Context, frameLayout: FrameLayout): ImageManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ImageManager(context, frameLayout).also { INSTANCE = it }
            }
    }
}

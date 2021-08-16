package com.sunragav.scalablecapital.feature.commits.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.sunragav.scalablecapital.R
import com.sunragav.scalablecapital.core.util.hide
import com.sunragav.scalablecapital.core.util.show
import com.sunragav.scalablecapital.databinding.EmptyCommitsViewBinding
import timber.log.Timber


@SuppressLint("CustomViewStyleable")
class EmptyCommitsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var continueAnim = false
    private var binding = EmptyCommitsViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var imageNum = 1
    private var totalImages = 0
    private val fadeIn: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    private val fadeOut: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.EmptyCommitsView)
        totalImages =
            attributes.getInt(R.styleable.EmptyCommitsView_totalImages, 0)
        attributes.recycle()
    }

    private fun render() {
        binding.ivFunny.startAnimation(fadeOut)
        fadeOut.onAnimEnded {
            imageNum++
            if (imageNum > totalImages) imageNum = 1
            loadImage()
            binding.ivFunny.startAnimation(fadeIn)
            fadeIn.onAnimEnded {
                render()
            }
        }
    }

    private fun loadImage() {
        val imageUri = "@drawable/${resources.getString(R.string.empty_img_prefix, imageNum)}"
        val imageResource = resources.getIdentifier(imageUri, null, context.packageName)

        Timber.d(
            "Loading image:%s pkg:%s imageRes:%d",
            imageUri,
            context.packageName,
            imageResource
        )
        binding.ivFunny.setImageDrawable(ContextCompat.getDrawable(context, imageResource))
    }

    private fun Animation.onAnimEnded(perform: () -> Unit) {
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (continueAnim) {
                    perform()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

    }

    fun start() {
        show()
        loadImage()
        continueAnim = true
        render()
    }

    fun stop() {
        continueAnim = false
        hide()
    }

    companion object {
        const val ASSET_LOC = "file:///android_asset/%s"
    }
}
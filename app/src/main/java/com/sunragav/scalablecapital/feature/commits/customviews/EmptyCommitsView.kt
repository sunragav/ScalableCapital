package com.sunragav.scalablecapital.feature.commits.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.sunragav.scalablecapital.R
import com.sunragav.scalablecapital.app.GlideApp
import com.sunragav.scalablecapital.core.util.hide
import com.sunragav.scalablecapital.core.util.show
import com.sunragav.scalablecapital.databinding.EmptyCommitsViewBinding
import timber.log.Timber
import kotlin.random.Random


@SuppressLint("CustomViewStyleable")
class EmptyCommitsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var continueAnim = false
    private var errorMsg = ""
    private var binding = EmptyCommitsViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var imageNum = 0
    private var totalImages = 0
    private val fadeIn: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    private val fadeOut: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.EmptyCommitsView)
        totalImages = attributes.getInt(R.styleable.EmptyCommitsView_totalImages, 0)
        errorMsg = attributes.getString(R.styleable.EmptyCommitsView_errorMsg) ?: ""
        attributes.recycle()
    }

    private fun render() {
        binding.ivFunny.startAnimation(fadeOut)
        fadeOut.onAnimEnded {
            imageNum++
            if (imageNum >= totalImages) imageNum = 0
            loadImage()
            binding.ivFunny.startAnimation(fadeIn)
            fadeIn.onAnimEnded {
                render()
            }
        }
    }

    private fun loadImage() {
        val imageUri = resources.getStringArray(R.array.empty_img_urls)[imageNum]
        Timber.d("Loading image:%s", imageUri)
        GlideApp.with(context)
            .load(imageUri)
            .into(binding.ivFunny)
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

    fun start(login: String? = null) {
        show()
        imageNum = Random.nextInt(0, totalImages)
        loadImage()
        binding.tvEmpty.text = login?.let { String.format(errorMsg, it) } ?: errorMsg
        continueAnim = true
        render()
    }

    fun stop() {
        continueAnim = false
        fadeIn.cancel()
        fadeOut.cancel()
        hide()
    }
}
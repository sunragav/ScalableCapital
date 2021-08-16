package com.sunragav.scalablecapital.feature.commits.customviews

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sunragav.scalablecapital.R
import com.sunragav.scalablecapital.R.styleable.AnimatableRectangleView
import com.sunragav.scalablecapital.core.util.hide
import com.sunragav.scalablecapital.core.util.show
import com.sunragav.scalablecapital.databinding.CommitsViewBinding
import com.sunragav.scalablecapital.feature.commits.repository.remote.helpers.CommitsCountOutputData


@SuppressLint("CustomViewStyleable")
class CommitsCountView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding = CommitsViewBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var _commitsCountOutputData: CommitsCountOutputData
    private var currentIndex = 0
    private var imageHeightPercentage = 0f
    private var displayYear = false

    init {
        val attributes = context.obtainStyledAttributes(attrs, AnimatableRectangleView)
        imageHeightPercentage =
            attributes.getFloat(R.styleable.AnimatableRectangleView_imageHeightPercent, 0f)
        displayYear = attributes.getBoolean(R.styleable.AnimatableRectangleView_displayYear, false)
        attributes.recycle()
    }

    fun update(commitsCountOutputData: CommitsCountOutputData) {
        binding.progressBar.hide()
        if (!commitsCountOutputData.valid) {
            hide()
        } else {
            show()
            binding.ivBar.show()
            if (displayYear) binding.tvYear.show()

            binding.ivBar.layoutParams.height = 0
            currentIndex = 0
            _commitsCountOutputData = commitsCountOutputData
            if (currentIndex < _commitsCountOutputData.commitsCountList.size) {
                render()
            }
        }
    }

    fun render() {
        val maxCommitCount = _commitsCountOutputData.maxCommit
        val height: Int
        _commitsCountOutputData.commitsCountList[currentIndex].run {
            binding.tvMonth.text = month
            binding.tvYear.text = year
            binding.tvCommits.text =
                resources.getQuantityString(R.plurals.commits, commitsCount, commitsCount)
            val ratio = commitsCount.toFloat() / maxCommitCount
            height = (binding.root.height * (1.0 - imageHeightPercentage) * ratio).toInt()
        }
        val valueAnimator =
            ValueAnimator.ofInt(
                binding.ivBar.measuredHeight,
                height
            )
        valueAnimator.duration = 1500L
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = binding.ivBar.layoutParams
            layoutParams.height = animatedValue
            binding.ivBar.layoutParams = layoutParams
        }
        valueAnimator.start()
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                currentIndex++
                if (currentIndex < _commitsCountOutputData.commitsCountList.size) {
                    render()
                }
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
    }
}
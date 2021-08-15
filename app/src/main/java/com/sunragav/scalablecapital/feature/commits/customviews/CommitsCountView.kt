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
import com.sunragav.scalablecapital.repository.async.commits.CommitsCountData
import timber.log.Timber


@SuppressLint("CustomViewStyleable")
class CommitsCountView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding = CommitsViewBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var _commitsCountData: CommitsCountData
    private var currentIndex = 0
    private var visibleHeight = 0

    init {
        val attributes = context.obtainStyledAttributes(attrs, AnimatableRectangleView)
        visibleHeight =
            attributes.getDimensionPixelSize(R.styleable.AnimatableRectangleView_imageHeight, 0)
        attributes.recycle()
    }

    fun update(commitsCountData: CommitsCountData) {
        binding.progressBar.hide()
        if (!commitsCountData.valid) {
            hide()
        } else {
            show()
            binding.ivBar.show()

            binding.ivBar.layoutParams.height = 0
            currentIndex = 0
            _commitsCountData = commitsCountData
            if (currentIndex < _commitsCountData.commitsCountList.size) {
                render()
            }
        }
    }

    fun render() {
        val currentCommitsCount = _commitsCountData.commitsCountList[currentIndex].commitsCount
        val maxCommitCount = _commitsCountData.maxCommit
        val month = _commitsCountData.commitsCountList[currentIndex].month
        binding.tvMonth.text = month
        binding.tvCommits.text =
            resources.getQuantityString(R.plurals.commits, currentCommitsCount, currentCommitsCount)
        val ratio = currentCommitsCount.toFloat() / maxCommitCount
        val height = visibleHeight.toFloat() * ratio
        Timber.d(
            "CommitsView maxCommit:%d currentComit:%d height:%f",
            maxCommitCount,
            currentCommitsCount,
            height
        )
        val valueAnimator =
            ValueAnimator.ofInt(
                binding.ivBar.measuredHeight,
                height.toInt()
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
                if (currentIndex < _commitsCountData.commitsCountList.size) {
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
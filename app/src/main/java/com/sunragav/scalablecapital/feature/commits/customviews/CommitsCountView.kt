package com.sunragav.scalablecapital.feature.commits.customviews

import android.animation.ObjectAnimator
import android.animation.RectEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sunragav.scalablecapital.R
import com.sunragav.scalablecapital.R.styleable.AnimatableRectangleView
import com.sunragav.scalablecapital.databinding.RectBinding


@SuppressLint("CustomViewStyleable")
class CommitsCountView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding: RectBinding = RectBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val attributes = context.obtainStyledAttributes(attrs, AnimatableRectangleView)
        attributes.recycle()
    }

    fun render(currentCommitsCount: Int, maxCommitCount: Int, month: String) {
        binding.tvMonth.text = month
        binding.tvCommits.text = resources.getString(R.string.commits, currentCommitsCount)
        val local = Rect()
        binding.ivBar.getLocalVisibleRect(local)
        val from = Rect(local)
        val to = Rect(local)

        /*  from.left = local.width() / 2 - local.width() / 4
          from.right = local.width() / 2 - local.width() / 4*/
/*
        to.left = local.width() / 2 - local.width() / 4
        to.right = local.width() / 2 - local.width() / 4*/
        // to.top = (local.height().toDouble()*commitsCountData.commitsCountMap[0]!!/commitsCountData.maxCommit).toInt()
        to.top = (local.height().toDouble() * (currentCommitsCount / maxCommitCount)).toInt()
        val anim: ObjectAnimator = ObjectAnimator.ofObject(
            binding.ivBar,
            "clipBounds",
            RectEvaluator(),
            from, to
        )
        anim.duration = 2000
        anim.start()
    }
}
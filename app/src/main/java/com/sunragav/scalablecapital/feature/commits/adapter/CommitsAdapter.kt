package com.sunragav.scalablecapital.feature.commits.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sunragav.scalablecapital.app.GlideApp.with
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.core.util.hide
import com.sunragav.scalablecapital.core.util.show
import com.sunragav.scalablecapital.databinding.RepoListItemBinding
import com.sunragav.scalablecapital.feature.commits.presenter.Selection
import com.sunragav.scalablecapital.feature.commits.repository.remote.models.CommitResponse
import com.sunragav.scalablecapital.presenter.transformer.GitHubViewModelTransformer
import timber.log.Timber

class CommitsAdapter(
    private val modelTransformer: GitHubViewModelTransformer,
    private val onClickListener: (selection: Selection) -> Unit
) :
    AbstractPagingAdapter<CommitResponse>(/*object : DiffUtil.ItemCallback<CommitResponse>(){
        override fun areItemsTheSame(oldItem: CommitResponse, newItem: CommitResponse): Boolean {
            return  oldItem.identifier == newItem.identifier
        }

        override fun areContentsTheSame(oldItem: CommitResponse, newItem: CommitResponse): Boolean {
            return oldItem==newItem && oldItem.selected==newItem.selected
        }

    }*/
    ) {
    lateinit var binding: RepoListItemBinding
    override fun createViewHolder(parent: ViewGroup): ViewHolder<CommitResponse> {
        binding = RepoListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return object : AbstractPagingAdapter.ViewHolder<CommitResponse>(binding) {
            override fun bind(position: Int) {
                getItem(position)?.let {
                    modelTransformer.transform(it).bindModel(position)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun CommitResponse.bindModel(position: Int) {
        with(binding.root.context)
            .load(author?.avatar_url)
            .into(binding.imgAvatar)
        binding.tvDescription.text = commit?.message
        if (selected) {
            binding.checkBox.show()
            binding.checkBox.isChecked = true
        } else {
            binding.checkBox.hide()
        }
        binding.tvRepoName.text = sha
        binding.tvOwnerName.text = author?.login
        binding.tvDate.text = commit?.author?.date
        binding.root.setOnClickListener {
            Timber.d("sha:%s clicked", sha)
            onClickListener.invoke(Selection(position = position, sha = sha, selected = !selected))
        }
    }
}



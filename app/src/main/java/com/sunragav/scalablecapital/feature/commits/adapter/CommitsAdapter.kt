package com.sunragav.scalablecapital.feature.commits.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.databinding.RepoListItemBinding
import com.sunragav.scalablecapital.feature.commits.transformer.GitHubViewModelTransformer
import com.sunragav.scalablecapital.repository.remote.model.CommitResponse

class CommitsAdapter(private val modelTransformer: GitHubViewModelTransformer) :
    AbstractPagingAdapter<CommitResponse>() {
    lateinit var binding: RepoListItemBinding
    override fun createViewHolder(parent: ViewGroup): ViewHolder<CommitResponse> {
        binding = RepoListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return object : AbstractPagingAdapter.ViewHolder<CommitResponse>(binding) {
            override fun bind(position: Int) {
                getItem(position)?.let {
                    modelTransformer.transform(it).bindModel()
                }
            }
        }
    }

    private fun CommitResponse.bindModel() {
        Glide.with(binding.root.context)
            .load(author?.avatar_url)
            .into(binding.imgAvatar)
        binding.tvDescription.text = commit?.message
        binding.tvRepoName.text = sha
        binding.tvOwnerName.text = author?.login
        binding.tvDate.text = commit?.author?.date
    }
}

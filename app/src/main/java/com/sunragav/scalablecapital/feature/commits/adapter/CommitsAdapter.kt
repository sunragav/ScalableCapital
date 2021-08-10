package com.sunragav.scalablecapital.feature.commits.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.databinding.RepoListItemBinding
import com.sunragav.scalablecapital.repository.remote.model.Commit

class CommitsAdapter : AbstractPagingAdapter<Commit>() {
    lateinit var binding: RepoListItemBinding
    override fun createViewHolder(parent: ViewGroup): ViewHolder<Commit> {
        binding = RepoListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return object : AbstractPagingAdapter.ViewHolder<Commit>(binding) {
            override fun bind(position: Int) {
                val model = getItem(position)
                com.bumptech.glide.Glide.with(binding.root.context)
                    .load(model?.author?.avatar_url)
                    .into(binding.imgAvatar)
                binding.tvDescription.text = model?.commit?.message
                binding.tvRepoName.text = model?.sha
                binding.tvOwnerName.text = model?.author?.login
            }
        }
    }
}

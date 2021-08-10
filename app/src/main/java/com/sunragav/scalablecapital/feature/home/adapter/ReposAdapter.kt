package com.sunragav.scalablecapital.feature.home.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.core.util.DeepLinks
import com.sunragav.scalablecapital.core.util.navigateUriWithDefaultOptions
import com.sunragav.scalablecapital.databinding.RepoListItemBinding
import com.sunragav.scalablecapital.repository.remote.model.Repo


class ReposAdapter : AbstractPagingAdapter<Repo>() {
    lateinit var binding: RepoListItemBinding
    override fun createViewHolder(parent: ViewGroup): ViewHolder<Repo> {
        binding = RepoListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return object : AbstractPagingAdapter.ViewHolder<Repo>(binding) {
            override fun bind(position: Int) {
                val model = getItem(position)
                com.bumptech.glide.Glide.with(binding.root.context)
                    .load(model?.owner?.avatar_url)
                    .into(binding.imgAvatar)
                binding.tvDescription.text = model?.description
                binding.tvRepoName.text = model?.full_name
                binding.tvOwnerName.text = model?.owner?.login
                binding.root.setOnClickListener {
                    it.findNavController()
                        .navigateUriWithDefaultOptions(Uri.parse("${DeepLinks.COMMITS}/${model?.name}"))
                }
            }
        }
    }

}

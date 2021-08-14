package com.sunragav.scalablecapital.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.databinding.RepoListItemBinding
import com.sunragav.scalablecapital.feature.home.ReposFragmentDirections
import com.sunragav.scalablecapital.repository.async.commits.RepoData
import com.sunragav.scalablecapital.repository.remote.model.Repo


class ReposAdapter : AbstractPagingAdapter<Repo>() {
    lateinit var binding: RepoListItemBinding
    override fun createViewHolder(parent: ViewGroup): ViewHolder<Repo> {
        binding = RepoListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return object : AbstractPagingAdapter.ViewHolder<Repo>(binding) {
            override fun bind(position: Int) {
                val model = getItem(position)
                model?.run {
                    com.bumptech.glide.Glide.with(binding.root.context)
                        .load(owner.avatar_url)
                        .into(binding.imgAvatar)
                    binding.tvDescription.text = description
                    binding.tvRepoName.text = full_name
                    binding.tvOwnerName.text = owner.login
                    binding.root.setOnClickListener { view ->
                        if (name.isNotBlank() && created_at.isNotBlank()) {
                            view.findNavController().navigate(
                                ReposFragmentDirections.actionFirstFragmentToSecondFragment(
                                    RepoData(name, created_at)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

}

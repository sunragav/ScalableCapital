package com.sunragav.scalablecapital.feature.repos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.core.util.show
import com.sunragav.scalablecapital.databinding.RepoListItemBinding
import com.sunragav.scalablecapital.feature.commits.transformer.GitHubViewModelTransformer
import com.sunragav.scalablecapital.feature.repos.ReposFragmentDirections
import com.sunragav.scalablecapital.repository.async.commits.RepoData
import com.sunragav.scalablecapital.repository.remote.model.RepoResponse


class ReposAdapter(private val modelTransformer: GitHubViewModelTransformer) :
    AbstractPagingAdapter<RepoResponse>() {
    lateinit var binding: RepoListItemBinding
    override fun createViewHolder(parent: ViewGroup): ViewHolder<RepoResponse> {
        binding = RepoListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return object : AbstractPagingAdapter.ViewHolder<RepoResponse>(binding) {
            override fun bind(position: Int) {
                val model = getItem(position)
                model?.let {
                    modelTransformer.transform(it).bindModel()
                }
            }
        }
    }

    private fun RepoResponse.bindModel() {
        Glide.with(binding.root.context)
            .load(owner.avatar_url)
            .into(binding.imgAvatar)
        binding.tvDescription.text = description
        binding.tvOwnerName.text = owner.login
        binding.tvRepoName.text = full_name
        binding.tvBranchName.text = default_branch
        binding.tvDate.text = created_at
        binding.tvStars.text = stargazers_count.toString()
        binding.tvStars.show()
        binding.root.setOnClickListener { view ->
            if (name.isNotBlank() && default_branch?.isNotBlank() == true) {
                view.findNavController().navigate(
                    ReposFragmentDirections.actionFirstFragmentToSecondFragment(
                        RepoData(
                            repoName = name,
                            defaultBranch = default_branch,
                            firstCommitDate = "",
                            lastCommitDate = ""
                        )
                    )
                )
            }
        }
    }
}

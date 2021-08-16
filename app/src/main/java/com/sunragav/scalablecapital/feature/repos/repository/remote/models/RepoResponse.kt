package com.sunragav.scalablecapital.feature.repos.repository.remote.models

import com.sunragav.scalablecapital.repository.remote.model.GitHubModel

data class RepoResponse(
    val id: Int,
    val name: String,
    val full_name: String,
    val description: String?,
    val owner: Owner,
    val stargazers_count: Int,
    val created_at: String?,
    val default_branch: String?
) : GitHubModel(identifier = id.toString()) {
    data class Owner(
        val login: String,
        val avatar_url: String?
    )
}
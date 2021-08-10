package com.sunragav.scalablecapital.repository.remote.model

data class Repo(
    val id: Int,
    val name: String,
    val full_name: String,
    val description: String?,
    val owner: Owner,
    val stargazers_count: Int
) : GitHubModel(identifier = id.toString()) {
    data class Owner(
        val login: String,
        val avatar_url: String?
    )
}
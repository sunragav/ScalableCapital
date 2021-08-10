package com.sunragav.scalablecapital.repository.remote.model

data class Commit(
    val sha: String,
    val commit: CommitMessage?,
    val author: Author?
) : GitHubModel(identifier = sha)

data class CommitMessage(
    val message: String?
)

data class Author(
    val login: String?,
    val avatar_url: String?
)
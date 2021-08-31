package com.sunragav.scalablecapital.feature.commits.repository.remote.models

import com.sunragav.scalablecapital.repository.remote.model.GitHubModel

data class CommitResponse(
    val sha: String,
    val commit: CommitMessage?,
    val author: Author?,
    var selected: Boolean = false,
    val position: Int = 0
) : GitHubModel(identifier = sha)

data class CommitMessage(
    val message: String?,
    val author: CommitAuthor?
)

data class CommitAuthor(
    val date: String?
)

data class Author(
    val login: String?,
    val avatar_url: String?
)
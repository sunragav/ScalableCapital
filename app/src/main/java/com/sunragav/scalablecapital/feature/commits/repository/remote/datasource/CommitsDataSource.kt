package com.sunragav.scalablecapital.feature.commits.repository.remote.datasource

import com.sunragav.scalablecapital.feature.commits.repository.remote.models.CommitResponse
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.datasource.GitHubPagingDataSource

class CommitsDataSource(
    private val repoService: RepoService,
    private val owner: String,
    private val repo: String
) : GitHubPagingDataSource<CommitResponse>({ page, pageSize ->
    repoService.getCommits(owner, repo, page, pageSize)
})
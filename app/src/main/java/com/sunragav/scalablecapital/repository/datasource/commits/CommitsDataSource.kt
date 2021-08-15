package com.sunragav.scalablecapital.repository.datasource.commits

import com.sunragav.scalablecapital.repository.datasource.GitHubPagingDataSource
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.model.CommitResponse

class CommitsDataSource(
    private val repoService: RepoService,
    private val owner: String,
    private val repo: String
) : GitHubPagingDataSource<CommitResponse>({ page, pageSize ->
    repoService.getCommits(owner, repo, page, pageSize)
})
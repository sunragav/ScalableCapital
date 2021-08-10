package com.sunragav.scalablecapital.repository.datasource.commits

import com.sunragav.scalablecapital.repository.datasource.GitHubPagingDataSource
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.model.Commit

class CommitsDataSource(
    private val repoService: RepoService,
    private val owner: String,
    private val repo: String
) : GitHubPagingDataSource<Commit>({ page ->
    repoService.getCommits(owner, repo, page)
})
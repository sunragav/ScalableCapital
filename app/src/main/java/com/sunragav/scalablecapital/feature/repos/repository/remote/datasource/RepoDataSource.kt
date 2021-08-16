package com.sunragav.scalablecapital.feature.repos.repository.remote.datasource

import com.sunragav.scalablecapital.feature.repos.repository.remote.models.RepoResponse
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.datasource.GitHubPagingDataSource

class RepoDataSource(
    private val repoService: RepoService,
    private val owner: String
) : GitHubPagingDataSource<RepoResponse>({ page, pageSize ->
    repoService.getRepos(
        owner,
        page,
        pageSize
    )
})
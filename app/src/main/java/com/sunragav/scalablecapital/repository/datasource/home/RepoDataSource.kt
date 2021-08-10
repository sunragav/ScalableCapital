package com.sunragav.scalablecapital.repository.datasource.home

import com.sunragav.scalablecapital.repository.datasource.GitHubPagingDataSource
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.model.Repo

class RepoDataSource(
    private val repoService: RepoService,
    private val owner: String
) : GitHubPagingDataSource<Repo>({ page -> repoService.getRepos(owner, page) })
package com.sunragav.scalablecapital.feature.repos.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunragav.scalablecapital.feature.repos.repository.remote.datasource.RepoDataSource
import com.sunragav.scalablecapital.feature.repos.repository.remote.models.RepoResponse
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.datasource.GitHubPagingDataSource.Companion.PAGE_SIZE
import kotlinx.coroutines.flow.Flow

class ReposViewModel(
    private val repoService: RepoService,
    private val owner: String
) : ViewModel() {

    val repoList: Flow<PagingData<RepoResponse>> = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        RepoDataSource(repoService, owner)
    }.flow.cachedIn(viewModelScope)

    class Factory(
        private val repoService: RepoService,
        private val owner: String
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReposViewModel::class.java)) {
                return ReposViewModel(repoService, owner) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
package com.sunragav.scalablecapital.presenter.commits

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.squareup.moshi.Moshi
import com.sunragav.scalablecapital.repository.async.commits.CommitsCountData
import com.sunragav.scalablecapital.repository.async.commits.CommitsCountHelper
import com.sunragav.scalablecapital.repository.async.commits.RepoData
import com.sunragav.scalablecapital.repository.datasource.GitHubPagingDataSource.Companion.PAGE_SIZE
import com.sunragav.scalablecapital.repository.datasource.commits.CommitsDataSource
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.model.CommitResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

typealias FlowOfPagingDataOfCommits = Flow<PagingData<CommitResponse>>

class CommitsViewModel(
    private val repoService: RepoService,
    private val moshi: Moshi,
    private val owner: String
) : ViewModel() {

    private val pagingMediatorLiveData = MediatorLiveData<LiveData<FlowOfPagingDataOfCommits>>()
    val triggerCommitsLoad = MutableLiveData<RepoData>()
    private val commitsCountMutableLiveData = MutableLiveData<CommitsCountData>()

    init {
        pagingMediatorLiveData.addSource(triggerCommitsLoad) { currentRepoData ->
            viewModelScope.launch {
                pagingMediatorLiveData.postValue(MutableLiveData<FlowOfPagingDataOfCommits>().apply {
                    value = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                        CommitsDataSource(repoService, owner, currentRepoData.repoName)
                    }.flow.cachedIn(viewModelScope)
                })
            }
            viewModelScope.launch {
                commitsCountMutableLiveData.value = CommitsCountHelper(
                    repoService,
                    moshi,
                    owner,
                    currentRepoData
                ).processCommitsCount()
            }
        }
    }

    val commitsList: LiveData<FlowOfPagingDataOfCommits>
        get() = Transformations.switchMap(pagingMediatorLiveData) { it }

    val commitsCountLiveData: LiveData<CommitsCountData>
        get() = commitsCountMutableLiveData

    class Factory(
        private val repoService: RepoService,
        private val moshi: Moshi,
        private val owner: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommitsViewModel::class.java)) {
                return CommitsViewModel(repoService, moshi, owner) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
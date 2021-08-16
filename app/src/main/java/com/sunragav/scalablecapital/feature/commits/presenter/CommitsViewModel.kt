package com.sunragav.scalablecapital.feature.commits.presenter

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunragav.scalablecapital.feature.commits.repository.remote.datasource.CommitsDataSource
import com.sunragav.scalablecapital.feature.commits.repository.remote.helpers.CommitsCountHelper
import com.sunragav.scalablecapital.feature.commits.repository.remote.helpers.CommitsCountOutputData
import com.sunragav.scalablecapital.feature.commits.repository.remote.helpers.RepoCommitData
import com.sunragav.scalablecapital.feature.commits.repository.remote.models.CommitResponse
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.datasource.GitHubPagingDataSource.Companion.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

typealias FlowOfPagingDataOfCommits = Flow<PagingData<CommitResponse>>

class CommitsViewModel(
    private val repoService: RepoService,
    private val owner: String
) : ViewModel() {

    private val pagingMediatorLiveData = MediatorLiveData<LiveData<FlowOfPagingDataOfCommits>>()
    val triggerCommitsLoad = MutableLiveData<RepoCommitData>()
    private val commitsCountMutableLiveData = MutableLiveData<CommitsCountOutputData>()

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
                    owner,
                    currentRepoData
                ).processCommitsCount()
            }
        }
    }

    val commitsList: LiveData<FlowOfPagingDataOfCommits>
        get() = Transformations.switchMap(pagingMediatorLiveData) { it }

    val commitsCountLiveData: LiveData<CommitsCountOutputData>
        get() = commitsCountMutableLiveData

    class Factory(
        private val repoService: RepoService,
        private val owner: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommitsViewModel::class.java)) {
                return CommitsViewModel(repoService, owner) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
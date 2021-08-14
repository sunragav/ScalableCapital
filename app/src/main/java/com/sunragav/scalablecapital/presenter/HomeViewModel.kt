package com.sunragav.scalablecapital.presenter

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunragav.scalablecapital.repository.async.commits.CommitsCountData
import com.sunragav.scalablecapital.repository.async.commits.CommitsCountHelper
import com.sunragav.scalablecapital.repository.async.commits.RepoData
import com.sunragav.scalablecapital.repository.datasource.commits.CommitsDataSource
import com.sunragav.scalablecapital.repository.datasource.home.RepoDataSource
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.model.Commit
import com.sunragav.scalablecapital.repository.remote.model.Repo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

typealias FlowOfPagingDataOfCommits = Flow<PagingData<Commit>>

class HomeViewModel(
    private val repoService: RepoService,
    private val owner: String
) : ViewModel() {
    val title = MutableLiveData<String>()
    val repoData = MutableLiveData<RepoData>()
    private val pagingLiveData = MediatorLiveData<LiveData<FlowOfPagingDataOfCommits>>()
    private val commitsLiveData = MutableLiveData<CommitsCountData>()

    init {
        pagingLiveData.addSource(repoData) { currentRepoData ->
            viewModelScope.launch {
                pagingLiveData.postValue(MutableLiveData<FlowOfPagingDataOfCommits>().apply {
                    value = Pager(PagingConfig(pageSize = 30)) {
                        CommitsDataSource(repoService, owner, currentRepoData.repoName)
                    }.flow.cachedIn(viewModelScope)
                })
                commitsLiveData.postValue(
                    CommitsCountHelper(
                        repoService,
                        owner,
                        currentRepoData
                    ).processCommitsCount()
                )
            }
        }
    }

    val commitsList: LiveData<FlowOfPagingDataOfCommits>
        get() = Transformations.switchMap(pagingLiveData) { it }

    val commitsCountLiveData: LiveData<CommitsCountData>
        get() = commitsLiveData

    val repoList: Flow<PagingData<Repo>> = Pager(PagingConfig(pageSize = 30)) {
        RepoDataSource(repoService, owner)
    }.flow.cachedIn(viewModelScope)

    class Factory(
        private val repoService: RepoService,
        private val owner: String
    ) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repoService, owner) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
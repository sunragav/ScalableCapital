package com.sunragav.scalablecapital.presenter

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunragav.scalablecapital.repository.datasource.commits.CommitsDataSource
import com.sunragav.scalablecapital.repository.datasource.home.RepoDataSource
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.model.Commit
import com.sunragav.scalablecapital.repository.remote.model.Repo
import kotlinx.coroutines.flow.Flow

typealias FlowOfPagingDataOfCommits = Flow<PagingData<Commit>>

class HomeViewModel(
    private val repoService: RepoService,
    private val owner: String
) : ViewModel() {
    val title = MutableLiveData<String>()
    val repoName = MutableLiveData<String>()
    private val mediatorLiveData = MediatorLiveData<LiveData<FlowOfPagingDataOfCommits>>()

    init {
        mediatorLiveData.addSource(repoName) { currentRepoName ->
            mediatorLiveData.postValue(MutableLiveData<FlowOfPagingDataOfCommits>().apply {
                value = Pager(PagingConfig(pageSize = 30)) {
                    CommitsDataSource(repoService, owner, currentRepoName)
                }.flow.cachedIn(viewModelScope)
            })
        }
    }

    val commitsList: LiveData<FlowOfPagingDataOfCommits>
        get() = Transformations.switchMap(mediatorLiveData) { it }

    val repoList: Flow<PagingData<Repo>> = Pager(PagingConfig(pageSize = 30)) {
        RepoDataSource(repoService, owner)
    }.flow.cachedIn(viewModelScope)

    class Factory(private val repoService: RepoService, private val owner: String) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repoService, owner) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
package com.sunragav.scalablecapital.feature.commits.presenter

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunragav.scalablecapital.feature.commits.repository.CommitsRepository
import com.sunragav.scalablecapital.feature.commits.repository.remote.helpers.CommitsCountHelper
import com.sunragav.scalablecapital.feature.commits.repository.remote.helpers.CommitsCountOutputData
import com.sunragav.scalablecapital.feature.commits.repository.remote.helpers.RepoCommitData
import com.sunragav.scalablecapital.feature.commits.repository.remote.models.CommitResponse
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.datasource.GitHubPagingDataSource.Companion.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

typealias FlowOfPagingDataOfCommits = Flow<PagingData<CommitResponse>>

data class Selection(val position: Int, val sha: String, val selected: Boolean)
class CommitsViewModel(
    private val repoService: RepoService,
    private val repository: CommitsRepository,
    private val owner: String
) : ViewModel() {

    private val pagingMediatorLiveData = MediatorLiveData<LiveData<FlowOfPagingDataOfCommits>>()
    private val selectionMediatorLiveData = MediatorLiveData<Int>()
    val triggerCommitsLoad = MutableLiveData<RepoCommitData>()
    val toggleSelection = MutableLiveData<Selection>()
    private val commitsCountMutableLiveData = MutableLiveData<CommitsCountOutputData>()

    init {
        pagingMediatorLiveData.addSource(triggerCommitsLoad) { currentRepoData ->
            /*viewModelScope.launch {
                pagingMediatorLiveData.postValue(MutableLiveData<FlowOfPagingDataOfCommits>().apply {
                    value = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                        CommitsDataSource(repoService, owner, currentRepoData.repoName)
                    }.flow.cachedIn(viewModelScope)
                })
            }*/
            /* viewModelScope.launch {
                 pagingMediatorLiveData.postValue(MutableLiveData<FlowOfPagingDataOfCommits>().apply {
                    value = repository.getCommits(owner,currentRepoData.repoName, PAGE_SIZE)
                 })
             }*/
            viewModelScope.launch(Dispatchers.IO) {
                commitsCountMutableLiveData.value = CommitsCountHelper(
                    repoService,
                    owner,
                    currentRepoData
                ).processCommitsCount()
            }
        }

        selectionMediatorLiveData.addSource(toggleSelection) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.select(sha = it.sha, it.selected)
                selectionMediatorLiveData.postValue(it.position)
                /* triggerCommitsLoad.value?.let {
                     selectionMediatorLiveData.postValue(repository.getSelected(it.repoName))
                 }*/
            }
        }
    }

    private val clearListCh = Channel<Unit>(Channel.CONFLATED)

    val commitsSelectionList = selectionMediatorLiveData

    @ExperimentalCoroutinesApi
    @FlowPreview
    val commitsList = flowOf(
        clearListCh.receiveAsFlow().map { PagingData.empty() },
        triggerCommitsLoad.asFlow()
            .flatMapLatest { repository.getCommits(owner, it.repoName, PAGE_SIZE) }
            .cachedIn(viewModelScope)
    ).flattenMerge(2)

/*    @ExperimentalCoroutinesApi
    @FlowPreview
    val selectedList = flowOf(
        clearSelectionListCh.receiveAsFlow(),
        toggleSelection.asFlow()
            .flatMapLatest {
                repository.select(sha = it.sha, it.selected)
                repository.getCommitBySha(it.sha)
            }
            .cachedIn(viewModelScope)
    ).flattenMerge(2)*/

    /*  val _commitsList: LiveData<FlowOfPagingDataOfCommits>
          get() = Transformations.switchMap(pagingMediatorLiveData) { it }
  */
    val commitsCountLiveData: LiveData<CommitsCountOutputData>
        get() = commitsCountMutableLiveData

    class Factory(
        private val repoService: RepoService,
        private val repository: CommitsRepository,
        private val owner: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommitsViewModel::class.java)) {
                return CommitsViewModel(repoService, repository, owner) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
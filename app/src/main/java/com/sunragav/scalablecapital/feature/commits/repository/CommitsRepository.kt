package com.sunragav.scalablecapital.feature.commits.repository

import androidx.paging.PagingData
import com.sunragav.scalablecapital.feature.commits.repository.remote.models.CommitResponse
import kotlinx.coroutines.flow.Flow

interface CommitsRepository {
    fun getCommits(owner: String, repoName: String, pageSize: Int): Flow<PagingData<CommitResponse>>
    suspend fun select(sha: String, selected: Boolean)
    suspend fun getSelected(repo: String): List<Int>
    suspend fun getCommitBySha(sha: String): CommitResponse
}
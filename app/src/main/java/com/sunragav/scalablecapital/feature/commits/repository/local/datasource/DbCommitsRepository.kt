package com.sunragav.scalablecapital.feature.commits.repository.local.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.sunragav.scalablecapital.feature.commits.repository.CommitsRepository
import com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db.CommitsDb
import com.sunragav.scalablecapital.presenter.transformer.GitHubViewModelTransformer
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import kotlinx.coroutines.flow.map

class DbCommitsRepository(private val db: CommitsDb, val repoService: RepoService) :
    CommitsRepository {
    private val transformer = GitHubViewModelTransformer()

    @OptIn(ExperimentalPagingApi::class)
    override fun getCommits(owner: String, repoName: String, pageSize: Int) = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            initialLoadSize = pageSize,
            prefetchDistance = 3
        ),
        remoteMediator = PageKeyedRemoteMediator(db, repoService, owner, repoName)
    ) {
        db.commits().commitsByRepoName(repoName)
    }.flow.map { pagingData ->
        pagingData.map { transformer.transform(it) }
    }

    override suspend fun select(sha: String, selected: Boolean) {
        db.commits().select(if (selected) 1 else 0, sha)
    }

    override suspend fun getSelected(repo: String) =
        db.commits().commitsBySelection(repo)

    override suspend fun getCommitBySha(sha: String) =
        transformer.transform(db.commits().commitsBySha(sha))
}
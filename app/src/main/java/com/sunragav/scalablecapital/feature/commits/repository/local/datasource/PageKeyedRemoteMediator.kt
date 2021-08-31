package com.sunragav.scalablecapital.feature.commits.repository.local.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db.CommitsDao
import com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db.CommitsDb
import com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db.CommitsEntity
import com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db.CommitsKeyDao
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.datasource.GitHubPagingDataSource.Companion.EMPTY_REPO
import com.sunragav.scalablecapital.repository.remote.datasource.GitHubPagingDataSource.Companion.PAGE_SIZE
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

@ExperimentalPagingApi
class PageKeyedRemoteMediator(
    private val db: CommitsDb,
    private val repoService: RepoService,
    private val owner: String,
    private val repoName: String
) : RemoteMediator<Int, CommitsEntity>() {
    private val commitsDao: CommitsDao = db.commits()
    private val remoteKeyDao: CommitsKeyDao = db.keys()

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CommitsEntity>
    ): MediatorResult {
        try {
            // Get the closest item from PagingState that we want to load data around.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    Timber.d("PageKeyedRemoteMediator Init")
                    null
                }
                LoadType.PREPEND -> {
                    Timber.d("PageKeyedRemoteMediator PREPEND")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    Timber.d("PageKeyedRemoteMediator APPEND")
                    // Query DB for SubredditRemoteKey for the subreddit.
                    // SubredditRemoteKey is a wrapper object we use to keep track of page keys we
                    // receive from the Reddit API to fetch the next or previous page.
                    /*  val remoteKey = db.withTransaction {
                          state.pages.lastOrNull()?.data?.lastOrNull()?.sha?.let {
                              Timber.d("PageKeyedRemoteMediator APPEND sha:%s",it)
                              remoteKeyDao.remoteKeyByRepoName(it)
                          }
                      }*/

                    val remoteKey = db.withTransaction {
                        Timber.d(
                            "PageKeyedRemoteMediator APPEND count:%d",
                            commitsDao.count(repoName)
                        )
                        commitsDao.count(repoName) / PAGE_SIZE + 1
                    }

                    // We must explicitly check if the page key is null when appending, since the
                    // Reddit API informs the end of the list by returning null for page key, but
                    // passing a null key to Reddit API will fetch the initial page.
                    if (remoteKey == 0) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    Timber.d("PageKeyedRemoteMediator APPEND page:%d", remoteKey)
                    remoteKey
                }
            }

            val data = repoService.getCommits(
                owner = owner,
                name = repoName,
                page = loadKey ?: 1,
                perPage = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            )
            val items = if (data.isSuccessful) {
                data.body()?.map {
                    CommitsEntity(
                        repoName = repoName,
                        sha = it.sha,
                        message = it.commit?.message,
                        selected = 0,
                        date = it.commit?.author?.date,
                        login = it.author?.login,
                        avatar_url = it.author?.avatar_url
                    )
                }
            } else null

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    commitsDao.deleteByRepoName(repoName)
                    remoteKeyDao.deleteByRepoName(repoName)
                }


                items?.let { list ->
                    if (list.isNotEmpty()) {
                        commitsDao.insertAll(list)
                        Timber.d("Last sha:%s page:%d", list.last().sha, (loadKey ?: 1) + 1)
                    }
                    /*   if (list.isNotEmpty()) remoteKeyDao.insert(
                           CommitsKeyEntity(
                               list.last().sha,
                               (loadKey ?: 1) + 1
                           )
                       )*/
                }
            }
            if (loadKey == null && items.isNullOrEmpty()) return MediatorResult.Error(
                Exception(
                    EMPTY_REPO
                )
            )

            return MediatorResult.Success(endOfPaginationReached = items.isNullOrEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
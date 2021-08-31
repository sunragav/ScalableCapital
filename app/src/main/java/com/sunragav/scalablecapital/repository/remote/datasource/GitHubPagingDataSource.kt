package com.sunragav.scalablecapital.repository.remote.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sunragav.scalablecapital.repository.remote.model.GitHubModel
import retrofit2.Response
import timber.log.Timber

abstract class GitHubPagingDataSource<T : GitHubModel>(private val serviceCall: suspend (Int, Int) -> Response<List<T>>) :
    PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            // Start refresh at page 1 if undefined.
            val currentPage = params.key ?: 1
            Timber.d("Service call for page:%d", currentPage)
            val response = serviceCall(currentPage, params.loadSize)
            if (response.isSuccessful) {
                val body = response.body() ?: emptyList()
                if (currentPage == 1 && body.isEmpty()) {
                    LoadResult.Error(java.lang.Exception(EMPTY_REPO))
                } else {
                    val data = mutableListOf<T>()
                    data.addAll(body)
                    LoadResult.Page(
                        data = data,
                        prevKey = null,
                        nextKey = if (data.isNotEmpty()) currentPage + 1 else null
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                Timber.e("Api Response errorBody:%s", errorMsg)
                LoadResult.Error(java.lang.Exception(errorMsg))
            }
        } catch (e: Exception) {
            Timber.e("Api Response errorBody:%s", e.localizedMessage)
            Timber.e("Api Response errorCause:%s", e.cause.toString())
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 15
        const val EMPTY_REPO = "Empty"
    }
}
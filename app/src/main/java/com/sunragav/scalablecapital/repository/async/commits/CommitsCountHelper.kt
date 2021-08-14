package com.sunragav.scalablecapital.repository.async.commits

import android.os.Parcelable
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.sunragav.scalablecapital.core.util.DateRange.Companion.getDateRangesForYear
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import com.sunragav.scalablecapital.repository.remote.model.graphql.CommitsResponse
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import timber.log.Timber

@Parcelize
data class RepoData(val repoName: String, val createdYear: String) : Parcelable

data class CommitsCountData(val commitsCountMap: Map<Int, CountData>, val maxCommit: Int)
data class CountData(val month: String, val commitsCount: Int)
class CommitsCountHelper(
    private val repoService: RepoService,
    private val user: String,
    private val repo: RepoData
) {

    suspend fun processCommitsCount(): CommitsCountData {
        val commitsCountMap = mutableMapOf<Int, CountData>()
        getDateRangesForYear(
            repo.createdYear.substring(0, 4),
            repo.createdYear.substring(5, 7).toInt()
        ).forEachIndexed { index, dateRangeWithMonth ->
            val query = String.format(
                GET_COMMITS_COUNT,
                user,
                repo.repoName,
                dateRangeWithMonth.dateRange.first,
                dateRangeWithMonth.dateRange.second
            )
            Timber.d(
                "Graph QL Date Range: %s - %s ",
                dateRangeWithMonth.dateRange.first,
                dateRangeWithMonth.dateRange.second
            )
            Timber.d("Graph QL Date query: %s", query)
            val paramObject = JSONObject()
            paramObject.put("query", query)

            val response = repoService.postDynamicQuery(paramObject.toString())
            commitsCountMap[index] =
                CountData(dateRangeWithMonth.month, if (response.isSuccessful) {
                    Timber.d("Graph QL response: %s", response.body())
                    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val adapter: JsonAdapter<CommitsResponse> =
                        moshi.adapter(CommitsResponse::class.java)
                    response.body()?.let {
                        val result = adapter.fromJson(it)
                        Timber.d("Graph QL response: %s", result)
                        result?.data?.repository?.obj?.history?.totalCount ?: 0
                    } ?: 0
                } else 0)
        }
        return CommitsCountData(commitsCountMap, commitsCountMap.maxOf { it.value.commitsCount })
    }

    companion object {
        val GET_COMMITS_COUNT = """
            query {
              repository(owner:"%s", name:"%s") {
                object(expression:"master") {
                  ... on Commit {
                    history(since:"%s",until:"%s") {
                      totalCount
                    }
                  }
                }
              }
            }
        """.trimIndent()
    }
}
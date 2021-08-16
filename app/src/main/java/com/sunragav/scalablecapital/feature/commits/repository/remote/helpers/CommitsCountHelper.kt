package com.sunragav.scalablecapital.feature.commits.repository.remote.helpers

import android.os.Parcelable
import com.sunragav.scalablecapital.core.util.DateRange.Companion.getDateRangesForYear
import com.sunragav.scalablecapital.feature.commits.repository.remote.models.graphql.HistoryObject
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import timber.log.Timber

@Parcelize
data class RepoCommitData(
    val repoName: String,
    val defaultBranch: String,
    val firstCommitDate: String,
    val lastCommitDate: String
) : Parcelable

data class CommitsCountOutputData(
    val commitsCountList: List<CommitsCountViewData>,
    val maxCommit: Int,
    val valid: Boolean = true
)

data class CommitsCountViewData(val month: String, val year: String, val commitsCount: Int)

class CommitsCountHelper(
    private val repoService: RepoService,
    private val user: String,
    repoCommitData: RepoCommitData
) {
    private var repo = RepoCommitData(
        repoName = repoCommitData.repoName,
        defaultBranch = repoCommitData.defaultBranch,
        firstCommitDate = "",
        lastCommitDate = ""
    )

    suspend fun processCommitsCount(): CommitsCountOutputData {
        getFirstAndLastCommitDate()
        val commitsCountList = mutableListOf<CommitsCountViewData>()
        return if (repo.lastCommitDate.isBlank() || repo.firstCommitDate.isBlank()) {
            CommitsCountOutputData(
                commitsCountList = commitsCountList,
                valid = false,
                maxCommit = 0
            )
        } else {
            val updatedMonth = repo.lastCommitDate.substring(5, 7).toInt()
            val createdMonth = repo.firstCommitDate.substring(5, 7).toInt()
            val startYear = repo.firstCommitDate.substring(0, 4).toInt()
            val finalYear = repo.lastCommitDate.substring(0, 4).toInt()
            var count = 0
            Timber.d(
                "Graph QL Repo:%s Year Range: %s-%s - %s-%s ", repo.repoName,
                createdMonth, startYear,
                updatedMonth, finalYear
            )
            for (year in startYear..finalYear) {
                val firstMonth = if (year == startYear) createdMonth else 1
                val lastMonth = if (year == finalYear) updatedMonth else 12
                getDateRangesForYear(
                    year,
                    firstMonth,
                    lastMonth
                ).forEach { dateRangeWithMonth ->
                    val query = String.format(
                        GET_COMMITS_COUNT,
                        user,
                        repo.repoName,
                        repo.defaultBranch,
                        dateRangeWithMonth.dateRange.first,
                        dateRangeWithMonth.dateRange.second
                    )
                    Timber.d(
                        "Graph QL Date Range: %s - %s ",
                        dateRangeWithMonth.dateRange.first,
                        dateRangeWithMonth.dateRange.second
                    )
                    Timber.d("Graph QL query: %s", query)
                    parseResponse(query) { obj ->
                        commitsCountList.add(
                            CommitsCountViewData(
                                month = dateRangeWithMonth.month,
                                year = year.toString(),
                                commitsCount = obj.history?.totalCount ?: 0
                            )
                        )
                        count++
                    }
                }
            }
            CommitsCountOutputData(
                commitsCountList = commitsCountList,
                maxCommit = if (count > 0) commitsCountList.maxOf { it.commitsCount } else 0,
                valid = count > 0)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun parseResponse(query: String, useResponse: (HistoryObject) -> Unit) {
        val paramObject = JSONObject()
        paramObject.put("query", query)
        Timber.d("Graph QL query : %s", query)
        val response = repoService.postDynamicQuery(paramObject.toString())
        if (response.isSuccessful) {
            Timber.d("Graph QL api response: %s", response.body())
            response.body()?.let { result ->
                Timber.d("Graph QL parsed response: %s", result)
                result.data?.repository?.obj?.let {
                    useResponse(it)
                }
            }
        }
    }

    private suspend fun getFirstAndLastCommitDate() {
        val lastCommitQuery = String.format(
            GET_LAST_COMMITS_DATE_AND_CURSOR,
            user,
            repo.repoName,
            repo.defaultBranch
        )
        var cursor = ""
        var totalCommits = 0
        parseResponse(lastCommitQuery) {
            it.history?.nodes?.get(0)?.committedDate?.let { date ->
                repo = repo.copy(lastCommitDate = date)
            }
            it.history?.let { history ->
                totalCommits = history.totalCount
                cursor = history.pageInfo?.endCursor?.split(" ")?.get(0) ?: ""
            }
        }
        if (repo.lastCommitDate.isBlank() || cursor.isBlank() || totalCommits == 0) {
            Timber.d(
                "Graph QL query failed or git repo is empty (totalCommitsCount=%d)",
                totalCommits
            )
        }
        if (totalCommits == 1) {
            repo = repo.copy(firstCommitDate = repo.lastCommitDate)
            Timber.d("Graph QL Only one commit in this repository")
        } else {
            firstCommitDate(cursor, totalCommits)
        }
    }

    private suspend fun firstCommitDate(
        cursor: String,
        totalCommits: Int
    ) {
        val firstCommitQuery = String.format(
            GET_FIRST_COMMIT,
            user,
            repo.repoName,
            repo.defaultBranch,
            cursor,
            totalCommits - 2
        )
        Timber.d("Graph QL First commit query: %s", firstCommitQuery)
        parseResponse(firstCommitQuery) {
            it.history?.nodes?.get(0)?.committedDate?.let { date ->
                repo = repo.copy(firstCommitDate = date)
            }
        }
        Timber.d(
            "Graph QL Repo:%s Year Range: %s -> %s ", repo.repoName,
            repo.firstCommitDate,
            repo.lastCommitDate
        )
    }

    companion object {
        val GET_COMMITS_COUNT = """
            query {
              repository(owner:"%s", name:"%s") {
                object(expression:"%s") {
                  ... on Commit {
                    history(since:"%s",until:"%s") {
                      totalCount
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val GET_LAST_COMMITS_DATE_AND_CURSOR = """
            query {
              repository(owner:"%s", name:"%s") {
                object(expression:"%s") {
                  ... on Commit {
                    history(first:1) {
                      nodes {
                        committedDate
                       }
                      totalCount
                      pageInfo {
                        endCursor
                      }
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val GET_FIRST_COMMIT = """
            query {
              repository(owner:"%s", name:"%s") {
                object(expression:"%s") {
                  ... on Commit {
                    history(first:1,after: "%s %d") {
                      nodes {
                        committedDate
                       }
                      totalCount
                      pageInfo {
                        endCursor
                      }
                    }
                  }
                }
              }
            }
        """.trimIndent()
    }
}
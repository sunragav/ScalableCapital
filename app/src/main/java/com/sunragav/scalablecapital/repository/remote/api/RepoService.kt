package com.sunragav.scalablecapital.repository.remote.api

import com.sunragav.scalablecapital.feature.commits.repository.remote.models.CommitResponse
import com.sunragav.scalablecapital.feature.commits.repository.remote.models.graphql.CommitsResponse
import com.sunragav.scalablecapital.feature.repos.repository.remote.models.RepoResponse
import retrofit2.Response
import retrofit2.http.*

interface RepoService {
    @GET("users/{owner}/repos")
    suspend fun getRepos(
        @Path("owner") owner: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<RepoResponse>>

    @GET("repos/{owner}/{name}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("name") name: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<CommitResponse>>

    @Headers("Content-Type: application/json")
    @POST("graphql")
    suspend fun postDynamicQuery(
        @Body body: String
    ): Response<CommitsResponse>
}
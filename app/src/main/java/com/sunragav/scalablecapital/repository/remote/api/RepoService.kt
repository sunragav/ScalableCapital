package com.sunragav.scalablecapital.repository.remote.api

import com.sunragav.scalablecapital.repository.remote.model.Commit
import com.sunragav.scalablecapital.repository.remote.model.Repo
import retrofit2.Response
import retrofit2.http.*

interface RepoService {
    @GET("users/{owner}/repos")
    suspend fun getRepos(
        @Path("owner") owner: String,
        @Query("page") page: Int
    ): Response<List<Repo>>

    @GET("repos/{owner}/{name}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("name") name: String,
        @Query("page") page: Int
    ): Response<List<Commit>>

    @Headers("Content-Type: application/json")
    @POST("graphql")
    suspend fun postDynamicQuery(
        @Body body: String
    ): Response<String>
}
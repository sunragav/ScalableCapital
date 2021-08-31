package com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CommitsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<CommitsEntity>)

    @Query("UPDATE commits SET selected = :selected where sha=:sha")
    suspend fun select(selected: Int, sha: String)


    @Query("SELECT position FROM commits WHERE repoName = :repoName AND selected=1")
    fun commitsBySelection(repoName: String): List<Int>

    @Query("SELECT * FROM commits WHERE  sha=:sha")
    fun commitsBySha(sha: String): CommitsEntity


    @Query("SELECT * FROM commits WHERE repoName = :repoName")
    fun commitsByRepoName(repoName: String): PagingSource<Int, CommitsEntity>


    @Query("SELECT COUNT(*) FROM commits WHERE repoName = :repoName")
    fun count(repoName: String): Int

    @Query("DELETE FROM commits WHERE repoName = :repoName")
    suspend fun deleteByRepoName(repoName: String)
}
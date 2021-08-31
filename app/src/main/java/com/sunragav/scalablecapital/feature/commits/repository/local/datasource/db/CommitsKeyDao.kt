package com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CommitsKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: CommitsKeyEntity)

    @Query("SELECT * FROM commits_key WHERE sha = :repoName")
    suspend fun remoteKeyByRepoName(repoName: String): CommitsKeyEntity

    @Query("DELETE FROM commits_key WHERE sha = :repoName")
    suspend fun deleteByRepoName(repoName: String)
}
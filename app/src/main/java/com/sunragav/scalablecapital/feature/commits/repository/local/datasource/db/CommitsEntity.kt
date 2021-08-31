package com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commits")
data class CommitsEntity(
    val repoName: String,
    @PrimaryKey(autoGenerate = true)
    val position: Int = 0,
    val sha: String,
    val selected: Int,
    val message: String?,
    val date: String?,
    val login: String?,
    val avatar_url: String?
)


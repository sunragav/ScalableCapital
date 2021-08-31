package com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commits_key")
data class CommitsKeyEntity(
    @PrimaryKey
    val sha: String,
    val page: Int?
)
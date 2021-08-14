package com.sunragav.scalablecapital.repository.remote.model.graphql

import com.squareup.moshi.Json

data class CommitsResponse(val data: CommitsData?)

data class CommitsData(val repository: Repository?)

data class Repository(@Json(name = "object") val obj: HistoryObject?)

data class HistoryObject(val history: History?)

data class History(val totalCount: Int)

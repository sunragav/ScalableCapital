package com.sunragav.scalablecapital.feature.commits.transformer

import com.sunragav.scalablecapital.core.util.DateRange.Companion.dateFormatter
import com.sunragav.scalablecapital.repository.remote.model.CommitResponse
import com.sunragav.scalablecapital.repository.remote.model.RepoResponse
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class GitHubViewModelTransformer {
    private val dateParser = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
    fun transform(model: CommitResponse): CommitResponse {
        val date = model.commit?.author?.date
        val newDateStr = date?.let {
            dateFormatter.parse(date)?.let {
                val transformedDate = dateParser.format(it)
                Timber.d("Original Date: %s Transformed date: %s", date, transformedDate)
                transformedDate
            }
        }
        return model.copy(commit = model.commit?.copy(author = model.commit.author?.copy(date = newDateStr)))
    }

    fun transform(model: RepoResponse): RepoResponse {
        val date = model.created_at
        val newDateStr = dateFormatter.parse(date)?.let {
            val transformedDate = dateParser.format(it)
            Timber.d("Original Date: %s Transformed date: %s", date, transformedDate)
            transformedDate
        }
        return model.copy(created_at = newDateStr)
    }
}
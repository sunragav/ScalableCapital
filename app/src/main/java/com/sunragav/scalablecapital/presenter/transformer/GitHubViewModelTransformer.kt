package com.sunragav.scalablecapital.presenter.transformer

import com.sunragav.scalablecapital.core.util.DateRange.Companion.dateFormatter
import com.sunragav.scalablecapital.feature.commits.repository.remote.models.CommitResponse
import com.sunragav.scalablecapital.feature.repos.repository.remote.models.RepoResponse
import java.text.SimpleDateFormat
import java.util.*

class GitHubViewModelTransformer {
    private val dateParser = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
    fun transform(model: CommitResponse): CommitResponse {
        val date = model.commit?.author?.date
        val newDateStr = date?.let {
            dateFormatter.parse(date)?.let {
                dateParser.format(it)
            }
        }
        return model.copy(commit = model.commit?.copy(author = model.commit.author?.copy(date = newDateStr)))
    }

    fun transform(model: RepoResponse): RepoResponse {
        val date = model.created_at
        val newDateStr = date?.let {
            dateFormatter.parse(date)?.let {
                dateParser.format(it)
            }
        }
        return model.copy(created_at = newDateStr)
    }
}
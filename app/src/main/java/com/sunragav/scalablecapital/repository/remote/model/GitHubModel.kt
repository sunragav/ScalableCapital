package com.sunragav.scalablecapital.repository.remote.model

abstract class GitHubModel(
    open val identifier: String

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GitHubModel

        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}

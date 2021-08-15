package com.sunragav.scalablecapital.app.di

import com.squareup.moshi.Moshi
import com.sunragav.scalablecapital.presenter.commits.CommitsViewModel
import com.sunragav.scalablecapital.presenter.repos.ReposViewModel
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import dagger.Module
import dagger.Provides

@Module
class PresentationModule {
    @Provides
    fun provideRepoViewModelFactory(
        repoService: RepoService,
        @Owner owner: String
    ) = ReposViewModel.Factory(repoService, owner)

    @Provides
    fun provideCommitsViewModelFactory(
        repoService: RepoService,
        moshi: Moshi,
        @Owner owner: String
    ) = CommitsViewModel.Factory(repoService, moshi, owner)
}
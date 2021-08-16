package com.sunragav.scalablecapital.app.di

import com.sunragav.scalablecapital.feature.commits.presenter.CommitsViewModel
import com.sunragav.scalablecapital.feature.repos.presenter.ReposViewModel
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
        @Owner owner: String
    ) = CommitsViewModel.Factory(repoService, owner)
}
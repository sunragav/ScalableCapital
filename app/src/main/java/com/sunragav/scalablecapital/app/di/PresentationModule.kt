package com.sunragav.scalablecapital.app.di

import com.sunragav.scalablecapital.presenter.HomeViewModel
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import dagger.Module
import dagger.Provides

@Module
class PresentationModule {
    @Provides
    fun provideRepoViewModelFactory(
        repoService: RepoService,
        @Owner owner: String
    ) =
        HomeViewModel.Factory(repoService, owner)
}
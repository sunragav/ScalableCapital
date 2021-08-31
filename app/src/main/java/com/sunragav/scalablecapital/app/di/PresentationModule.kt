package com.sunragav.scalablecapital.app.di

import android.app.Application
import com.sunragav.scalablecapital.feature.commits.presenter.CommitsViewModel
import com.sunragav.scalablecapital.feature.commits.repository.CommitsRepository
import com.sunragav.scalablecapital.feature.commits.repository.local.datasource.DbCommitsRepository
import com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db.CommitsDb
import com.sunragav.scalablecapital.feature.repos.presenter.ReposViewModel
import com.sunragav.scalablecapital.repository.remote.api.RepoService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

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
        commitsRepository: CommitsRepository,
        @Owner owner: String
    ) = CommitsViewModel.Factory(repoService, commitsRepository, owner)

    @Provides
    fun provideCommitsRepository(
        commitsDb: CommitsDb,
        repoService: RepoService
    ): CommitsRepository =
        DbCommitsRepository(commitsDb, repoService)

    @Provides
    @Singleton
    fun providesDatabase(
        application: Application
    ) = CommitsDb.create(application.applicationContext, true)

    @Provides
    @Singleton
    fun providesComicsDAO(
        commitsDb: CommitsDb
    ) = commitsDb.commits()

    @Provides
    @Singleton
    fun providesKeysDAO(
        commitsDb: CommitsDb
    ) = commitsDb.keys()

}
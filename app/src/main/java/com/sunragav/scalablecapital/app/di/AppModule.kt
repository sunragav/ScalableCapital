package com.sunragav.scalablecapital.app.di

import com.sunragav.scalablecapital.feature.commits.CommitsFragment
import com.sunragav.scalablecapital.feature.repos.ReposFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.InternalCoroutinesApi

@Module
abstract class AppModule {
    @InternalCoroutinesApi
    @ContributesAndroidInjector
    internal abstract fun contributesReposFragment(): ReposFragment

    @InternalCoroutinesApi
    @ContributesAndroidInjector
    internal abstract fun contributesCommitsFragment(): CommitsFragment
}
package com.sunragav.scalablecapital.app.di

import android.app.Application
import android.content.Context
import com.sunragav.scalablecapital.MainActivity
import com.sunragav.scalablecapital.feature.commits.CommitsFragment
import com.sunragav.scalablecapital.feature.home.ReposFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.InternalCoroutinesApi

@Module
abstract class AppModule {
    @Binds
    abstract fun bindContext(application: Application): Context

    @ContributesAndroidInjector
    internal abstract fun contributesMainActivity(): MainActivity

    @InternalCoroutinesApi
    @ContributesAndroidInjector
    internal abstract fun contributesReposFragment(): ReposFragment

    @InternalCoroutinesApi
    @ContributesAndroidInjector
    internal abstract fun contributesCommitsFragment(): CommitsFragment
}
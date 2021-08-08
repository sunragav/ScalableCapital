package com.sunragav.scalablecapital.app.di

import android.app.Application
import android.content.Context
import com.sunragav.scalablecapital.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppModule {
    @Binds
    abstract fun bindContext(application: Application): Context

    @ContributesAndroidInjector
    internal abstract fun contributesMainActivity(): MainActivity
}
package com.sunragav.scalablecapital.app.di

import com.sunragav.scalablecapital.BuildConfig
import dagger.Module
import dagger.Provides

@Module
class DataModule {
    @Provides
    @Owner
    fun provideOwner() = BuildConfig.OWNER

    @Provides
    @BaseUrl
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @AuthToken
    fun provideAuth() = BuildConfig.AUTHORISATION

    @Provides
    @AuthTokenPrefix
    fun provideAuthPrefix() = BuildConfig.AUTHORISATION_PREFIX
}
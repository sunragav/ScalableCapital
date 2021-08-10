package com.sunragav.scalablecapital.app.di

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class Owner

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrl

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

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class GraphQLBaseUrl

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthToken

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class GraphQLAuthInterceptor

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class RestAuthInterceptor

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class RestRetrofit

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class GraphQLRetrofit


@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class RestHttpClient

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class GraphQLHttpClient

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthTokenPrefix


@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class GraphQLAuthHeaderPrefix

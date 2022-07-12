package com.example.buglibrary.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MapApi

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ContentApi

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ContentApiPro

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class CoroutineScopeIO
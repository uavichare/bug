package com.example.buglibrary.di

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory


@Module
class CoreDataModule {

    @Provides
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
//            .addInterceptor(interceptor)
//            .addNetworkInterceptor(interceptor)
            .build()


    @Provides
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor().apply {
           // level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
           // else
             //   HttpLoggingInterceptor.Level.NONE
        }


    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()
}
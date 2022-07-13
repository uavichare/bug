package com.example.buglibrary.di

import android.app.Application
import com.example.buglibrary.api.ApiContent
import com.example.buglibrary.api.RequestMap
import com.example.buglibrary.db.AppDatabase
import com.example.buglibrary.helper.AppConstant
import com.example.buglibrary.repo.RemoteMapDataSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class, CoreDataModule::class])
class AppModule {

    @Singleton
    @Provides
    fun providePoiRequest(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ) = provideClient(okHttpClient, gsonConverterFactory, RequestMap::class.java)

    @Singleton
    @Provides
    fun provideContentRequest(
        @MapApi okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ) = provideClient(okHttpClient, gsonConverterFactory, ApiContent::class.java)

    @MapApi
    @Provides
    fun provideOkHttpClient(okHttpClient: OkHttpClient): OkHttpClient {
        val certificatePinner = CertificatePinner.Builder()
            .add(
                AppConstant.PINNED_BASE_URL_DEV,
                "sha256/98EXShXgNFSkORFzMdg9EEryjJKk2y0ijMEcNkFWWRE="
            )
            .build()
        return okHttpClient.newBuilder()
            .certificatePinner(certificatePinner)
            .build()
    }

    @Singleton
    @Provides
    fun provideMapRemoteDataSource(legoService: RequestMap) = RemoteMapDataSource(legoService)

    @Singleton
    @Provides
    fun provideDb(app: Application) = AppDatabase.getInstance(app)

    @Singleton
    @Provides
    fun providePoiDao(db: AppDatabase) = db.poiDao()

    @CoroutineScopeIO
    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.IO)

    private fun createRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    private fun <T> provideClient(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
        clazz: Class<T>
    ): T {
        return createRetrofit(okHttpClient, gsonConverterFactory).create(clazz)
    }
}
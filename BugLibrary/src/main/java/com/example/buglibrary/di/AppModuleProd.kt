package com.example.buglibrary.di

import com.example.buglibrary.api.ApiContent
import com.example.buglibrary.helper.AppConstant
import com.example.buglibrary.repo.RemoteContentDataSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [ViewModelModule::class, CoreDataModule::class])
class AppModuleProd {


    @Provides
    @ContentApiPro
    fun provideContentRequestProd(
        @ContentApiPro okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ) = provideClientProd(okHttpClient, gsonConverterFactory, ApiContent::class.java)


    @ContentApiPro
    @Provides
    fun provideOkHttpClientProduction(okHttpClient: OkHttpClient): OkHttpClient {
        val certificatePinner = CertificatePinner.Builder()
            .add(
                AppConstant.PINNED_BASE_URL_DEV,
                "sha256/98EXShXgNFSkORFzMdg9EEryjJKk2y0ijMEcNkFWWRE="
            )
            .build()
        return okHttpClient.newBuilder()
//            .certificatePinner(certificatePinner)
            .build()
    }


    @Provides
    fun provideRemoteDataSource(contentApi: ApiContent) = RemoteContentDataSource(contentApi)


    @CoroutineScopeIO
    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.IO)

    private fun createRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL_PRODUCTION)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }


    private fun <T> provideClientProd(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
        clazz: Class<T>
    ): T {
        return createRetrofit(
            okHttpClient,
            gsonConverterFactory
        ).create(clazz)
    }
}
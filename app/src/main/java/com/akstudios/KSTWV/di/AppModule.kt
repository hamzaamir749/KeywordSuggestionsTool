package com.akstudios.KSTWV.di

import android.content.Context
import android.os.Build
import com.akstudios.KSTWV.BuildConfig
import com.akstudios.KSTWV.adapters.BulkKeywordAdapter
import com.akstudios.KSTWV.adapters.SimilarKeywordAdapter
import com.akstudios.KSTWV.network.ApiService
import com.akstudios.KSTWV.repository.KeywordBulkRepository
import com.akstudios.KSTWV.utils.LoadingDialog
import com.akstudios.KSTWV.utils.getBaseUrl
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideBaseUrl(@ApplicationContext context: Context) = context.getBaseUrl()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        chuckInterceptor: ChuckInterceptor
    ) =
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            if (Build.VERSION.SDK_INT >= 31) {
                OkHttpClient.Builder()
                    .connectTimeout(240, TimeUnit.SECONDS)
                    .readTimeout(240, TimeUnit.SECONDS)
                    .build()
            } else {
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chuckInterceptor)
                    .connectTimeout(240, TimeUnit.SECONDS)
                    .readTimeout(240, TimeUnit.SECONDS)
                    .build()
            }
        } else {
            OkHttpClient
                .Builder()
                .build()
        }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create()
            )
        ).addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)


    @Provides
    @Singleton
    fun provideChuckInterceptor(@ApplicationContext context: Context): ChuckInterceptor =
        ChuckInterceptor(context)

    @Provides
    @Singleton
    fun provideKeywordBulkRepository(apiService: ApiService) = KeywordBulkRepository(apiService)

    @Provides
    @Singleton
    fun provideBulkKeyboardAdapter(@ApplicationContext context: Context) =
        BulkKeywordAdapter(context)

    @Provides
    @Singleton
    fun provideSimilarKeywordAdapter(@ApplicationContext context: Context) =
        SimilarKeywordAdapter(context)

    @Provides
    @Singleton
    fun provideLoadingDialog() =
        LoadingDialog()
}
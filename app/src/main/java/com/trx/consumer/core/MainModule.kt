package com.trx.consumer.core

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.stripe.android.Stripe
import com.trx.consumer.BuildConfig.kBaseUrl
import com.trx.consumer.BuildConfig.kStripeApiKey
import com.trx.consumer.base.BaseApi
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.ConfigManager
import com.trx.consumer.screens.groupplayer.GroupPlayerHandler
import com.trx.consumer.screens.liveplayer.LivePlayerHandler
import com.trx.consumer.stripe.StripeBackendManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object MainModule {

    private const val REGULAR_TIMEOUT = 60L

    @Provides
    @Singleton
    fun provideCache(application: Application): Cache =
        Cache(application.cacheDir, 20L * 1024 * 1024)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        requestInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .readTimeout(REGULAR_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REGULAR_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(REGULAR_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(requestInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRequestInterceptor(): Interceptor =
        Interceptor { chain ->
            val request = chain.request()
            val newBuilder = request.newBuilder()
            try {
                chain.proceed(newBuilder.build())
            } catch (exception: Exception) {
                okhttp3.Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(408) // status code
                    .message("Request Timeout")
                    .body(
                        ResponseBody.create("application/json; charset=utf-8".toMediaType(), "{}")
                    )
                    .build()
            }
        }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .baseUrl(kBaseUrl)
            .build()

    @Provides
    @Singleton
    fun provideTrxApi(retrofit: Retrofit): BaseApi =
        retrofit.create(BaseApi::class.java)

    @Provides
    @Singleton
    fun provideCacheManager(@ApplicationContext context: Context): CacheManager =
        CacheManager(context)

    @Provides
    @Singleton
    fun provideBackendManager(api: BaseApi, cacheManager: CacheManager): BackendManager =
        BackendManager(api, cacheManager)

    @Provides
    @Singleton
    fun provideStripe(@ApplicationContext context: Context): Stripe =
        Stripe(context, kStripeApiKey)

    @Provides
    @Singleton
    fun provideStripeBackendManager(stripe: Stripe): StripeBackendManager =
        StripeBackendManager(stripe)

    @Provides
    @Singleton
    fun provideConfigManager(cacheManager: CacheManager): ConfigManager =
        ConfigManager(cacheManager)

    @Provides
    @Singleton
    fun provideAnalyticsManager(configManager: ConfigManager): AnalyticsManager {
        return AnalyticsManager(configManager)
    }

    @Provides
    @Singleton
    fun provideLivePlayerHandler(@ApplicationContext context: Context): LivePlayerHandler =
        LivePlayerHandler(context)

    @Provides
    @Singleton
    fun provideGroupPlayerHandler(@ApplicationContext context: Context): GroupPlayerHandler =
        GroupPlayerHandler(context)
}

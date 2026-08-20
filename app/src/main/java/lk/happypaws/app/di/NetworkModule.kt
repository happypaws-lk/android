package lk.happypaws.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import lk.happypaws.app.BuildConfig
import lk.happypaws.app.data.remote.api.AuthApi
import lk.happypaws.app.data.remote.api.AuthInterceptor
import lk.happypaws.app.data.remote.api.TokenAuthenticator
import lk.happypaws.app.data.remote.api.UserApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named("AuthOkHttpClient")
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .configureDebugSsl()
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): com.google.gson.Gson {
        return com.google.gson.GsonBuilder()
            .registerTypeAdapter(lk.happypaws.app.data.remote.model.HomeSize::class.java, lk.happypaws.app.data.remote.model.HomeSizeAdapter())
            .registerTypeAdapter(lk.happypaws.app.data.remote.model.ActivityLevel::class.java, lk.happypaws.app.data.remote.model.ActivityLevelAdapter())
            .create()
    }

    @Provides
    @Singleton
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(
        @Named("AuthOkHttpClient") okHttpClient: OkHttpClient,
        gson: com.google.gson.Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(@Named("AuthRetrofit") retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHealthApi(@Named("AuthRetrofit") retrofit: Retrofit): lk.happypaws.app.data.remote.api.HealthApi {
        return retrofit.create(lk.happypaws.app.data.remote.api.HealthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .configureDebugSsl()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: com.google.gson.Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRescueApi(retrofit: Retrofit): lk.happypaws.app.data.remote.api.RescueApi {
        return retrofit.create(lk.happypaws.app.data.remote.api.RescueApi::class.java)
    }

    @Provides
    @Singleton
    @Named("MainRetrofit")
    fun provideMainRetrofit(okHttpClient: OkHttpClient, gson: com.google.gson.Gson): Retrofit = provideRetrofit(okHttpClient, gson)

    private fun OkHttpClient.Builder.configureDebugSsl(): OkHttpClient.Builder {
        if (BuildConfig.DEBUG) {
            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                hostnameVerifier { _, _ -> true }
            } catch (_: Exception) {
                // Ignore exception in debug setup fallback
            }
        }
        return this
    }
}

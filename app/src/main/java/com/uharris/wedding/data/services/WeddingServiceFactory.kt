package com.uharris.wedding.data.services

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.uharris.wedding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal object WeddingServiceFactory {

    private const val CONNECTION_TIMEOUT = 120L
    private const val READ_TIMEOUT = 120L

    inline fun <reified T> makeService(isDebug: Boolean): T {
        val okHttpClient = makeTrustAllOkHttpClient(makeLoggingInterceptor((isDebug)))
        return makeService(okHttpClient, Gson())
    }

    inline fun <reified T> makeService(okHttpClient: OkHttpClient, gson: Gson): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(T::class.java)
    }

    private fun makeBaseOkHttpClientBuilder(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
    }

    /**
     * This is insecure because certificate verification is not being performed.
     * Please update https://qa-test-backend.reign.dev certificates and use makeOkHttpClient instead
     */
    fun makeTrustAllOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val trustAllCerts = makeTrustAllCerts()
        val sslContext = makeTrustAllSSLContext(trustAllCerts)
        return makeBaseOkHttpClientBuilder(httpLoggingInterceptor)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    fun makeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return makeBaseOkHttpClientBuilder(httpLoggingInterceptor).build()
    }

    fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (isDebug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    private fun makeTrustAllSSLContext(trustAllCerts: Array<TrustManager>): SSLContext =
        SSLContext.getInstance("SSL").apply { init(null, trustAllCerts, SecureRandom()) }

    private fun makeTrustAllCerts() = arrayOf<TrustManager>(
        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOf()
        }
    )
}
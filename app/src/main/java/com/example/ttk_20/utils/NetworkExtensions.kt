package com.example.ttk_20.utils

import android.content.res.Resources
import com.example.ttk_20.R
import okhttp3.OkHttpClient
import java.io.BufferedInputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Необходимо для API < 24 (Android N 7.0)
 * */
fun OkHttpClient.Builder.setSslContext(resources: Resources) {
    try {
        val cf = CertificateFactory.getInstance("X.509")
        val caInput: InputStream = BufferedInputStream(resources.openRawResource(R.raw.certificate))
        val ca: Certificate = caInput.use { cf.generateCertificate(it) }
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)
        val trustManager = tmf.trustManagers.find { it is X509TrustManager } as X509TrustManager
        val context = SSLContext.getInstance("TLS")
        context.init(null, tmf.trustManagers, null)
        sslSocketFactory(context.socketFactory, trustManager)
    } catch (e: Exception) {
    }
}
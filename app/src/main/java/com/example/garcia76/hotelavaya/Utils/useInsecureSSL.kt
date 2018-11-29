

package com.example.garcia76.hotelavaya.Utils
import java.security.cert.X509Certificate
import javax.net.ssl.*

class useInsecureSSL {
    fun useInsecureSSL() {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
        })
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        val allHostsValid = HostnameVerifier { _, _ -> true }
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
    }
}
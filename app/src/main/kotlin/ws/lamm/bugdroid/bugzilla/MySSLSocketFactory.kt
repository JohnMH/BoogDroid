package ws.lamm.bugdroid.bugzilla

import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

import org.apache.http.HttpVersion
import org.apache.http.client.HttpClient
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.conn.scheme.PlainSocketFactory
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.scheme.SchemeRegistry
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpParams
import org.apache.http.params.HttpProtocolParams
import org.apache.http.protocol.HTTP

// Code from http://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https
class MySSLSocketFactory @Throws(NoSuchAlgorithmException::class, KeyManagementException::class, KeyStoreException::class, UnrecoverableKeyException::class)
constructor(truststore: KeyStore) : SSLSocketFactory(truststore) {
    internal var sslContext = SSLContext.getInstance("TLS")

    init {

        val tm = object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                try {
                    chain[0].checkValidity()
                } catch (e: Exception) {
                    throw CertificateException("Certificate not valid or trusted.")
                }

            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                try {
                    chain[0].checkValidity()
                } catch (e: Exception) {
                    throw CertificateException("Certificate not valid or trusted.")
                }

            }

            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                return null
            }
        }

        sslContext.init(null, arrayOf<TrustManager>(tm), null)
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(socket: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        return sslContext.socketFactory.createSocket(socket, host, port, autoClose)
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket {
        return sslContext.socketFactory.createSocket()
    }

    companion object {

        val newHttpClient: HttpClient
            get() {
                try {
                    val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())
                    trustStore.load(null, null)

                    val sf = MySSLSocketFactory(trustStore)
                    sf.hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER

                    val params = BasicHttpParams()
                    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1)
                    HttpProtocolParams.setContentCharset(params, HTTP.UTF_8)

                    val registry = SchemeRegistry()
                    registry.register(Scheme("http", PlainSocketFactory.getSocketFactory(), 80))
                    registry.register(Scheme("https", sf, 443))

                    val ccm = ThreadSafeClientConnManager(params, registry)

                    return DefaultHttpClient(ccm, params)
                } catch (e: Exception) {
                    return DefaultHttpClient()
                }

            }
    }
}

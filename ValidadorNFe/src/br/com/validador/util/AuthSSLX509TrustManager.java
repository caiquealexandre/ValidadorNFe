package br.com.validador.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class AuthSSLX509TrustManager implements X509TrustManager
{
    private X509TrustManager defaultTrustManager = null;

    public AuthSSLX509TrustManager(final X509TrustManager defaultTrustManager) {
        super();
        if (defaultTrustManager == null) {
            throw new IllegalArgumentException("Trust manager may not be null");
        }
        this.defaultTrustManager = defaultTrustManager;
    }

    public void checkClientTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
        if (true) {
            for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
            }
        }
        defaultTrustManager.checkClientTrusted(certificates,authType);
    }

    public void checkServerTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
        if (true) {
            for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
            }
        }
        defaultTrustManager.checkServerTrusted(certificates,authType);
    }

    public X509Certificate[] getAcceptedIssuers() {
        return this.defaultTrustManager.getAcceptedIssuers();
    }
}

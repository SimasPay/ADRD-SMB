package com.mfino.bsim.services;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;
public class NullTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(final X509Certificate[] chain,
                                   final String authType)
       throws CertificateException {
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain,
                                   final String authType)
       throws CertificateException {
    }

    @Override
    public final X509Certificate[] getAcceptedIssuers() {
       return new X509Certificate[] {};
    }
}
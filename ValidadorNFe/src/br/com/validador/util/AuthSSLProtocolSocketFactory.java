package br.com.validador.util;

import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.util.Enumeration;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;


public class AuthSSLProtocolSocketFactory implements SecureProtocolSocketFactory
{
   private URL keystoreUrl = null;
   private String keystorePassword = null;
   private URL truststoreUrl = null;
   private String truststorePassword = null;
   private SSLContext sslcontext = null;
   private X509Certificate x509Certificate = null;
   private PrivateKey privateKey = null;

   public AuthSSLProtocolSocketFactory(final URL keystoreUrl, final String keystorePassword, 
            final URL truststoreUrl, final String truststorePassword, final X509Certificate certificate, 
            final PrivateKey privateKey)
   {
      super();
      this.keystoreUrl = keystoreUrl;
      this.keystorePassword = keystorePassword;
      this.truststoreUrl = truststoreUrl;
      this.truststorePassword = truststorePassword;
      this.x509Certificate = certificate;
      this.privateKey = privateKey;
   }


   private static KeyStore createKeyStore(final URL url, final String password, 
            String keyStoreType)
   throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
   {
      if (url == null)
      {
         throw new IllegalArgumentException("Keystore url may not be null");
      }
      //LOG.debug("Inicializa key store");
      KeyStore keystore = KeyStore.getInstance(keyStoreType);
      InputStream is = null;
      try
      {
         is = url.openStream();
         keystore.load(is, password != null? password.toCharArray(): null);
      }
      finally
      {
         if (is != null)
            is.close();
      }
      return keystore;
   }


   private static KeyManager[] createKeyManagers(final KeyStore keystore, final String password, 
            final X509Certificate certificate, final PrivateKey privateKey)
   throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
   {
      if (keystore == null)
      {
         throw new IllegalArgumentException("Keystore may not be null");
      }
      return new KeyManager[] { new CustomX509KeyManager(certificate, privateKey) };
   }


   private static TrustManager[] createTrustManagers(final KeyStore keystore)
   throws KeyStoreException, NoSuchAlgorithmException
   {
      if (keystore == null)
      {
         throw new IllegalArgumentException("Keystore may not be null");
      }
      //LOG.debug("Inicializa trust manager");
      TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmfactory.init(keystore);
      TrustManager[] trustmanagers = tmfactory.getTrustManagers();
      for (int i = 0; i < trustmanagers.length; i++)
      {
         if (trustmanagers[i] instanceof X509TrustManager)
         {
            trustmanagers[i] = new AuthSSLX509TrustManager((X509TrustManager) trustmanagers[i]);
         }
      }
      return trustmanagers;
   }


   private SSLContext createSSLContext()
   {
      try
      {
         KeyManager[] keymanagers = null;
         TrustManager[] trustmanagers = null;
         if (this.keystoreUrl != null)
         {
            KeyStore keystore = null;
            try
            {
               keystore = createKeyStore(this.keystoreUrl, this.keystorePassword, "PKCS12");
            }
            catch (Exception e)
            {
               keystore = createKeyStore(this.keystoreUrl, this.keystorePassword, "JKS");
            }

            if (true)
            {
               Enumeration aliases = keystore.aliases();
               while (aliases.hasMoreElements())
               {
                  String alias = (String) aliases.nextElement();
                  Certificate[] certs = keystore.getCertificateChain(alias);
                  if (certs != null)
                  {
                     for (int c = 0; c < certs.length; c++)
                     {
                        if (certs[c] instanceof X509Certificate)
                        {
                           X509Certificate cert = (X509Certificate) certs[c];
                        }
                     }
                  }
               }
            }
            keymanagers = 
                  createKeyManagers(keystore, this.keystorePassword, x509Certificate, privateKey);
         }
         if (this.truststoreUrl != null)
         {
            KeyStore keystore = createKeyStore(this.truststoreUrl, this.truststorePassword, "jks");
            if (true)
            {
               Enumeration aliases = keystore.aliases();
               while (aliases.hasMoreElements())
               {
                  String alias = (String) aliases.nextElement();
                  Certificate trustedcert = keystore.getCertificate(alias);
                  if (trustedcert != null && trustedcert instanceof X509Certificate)
                  {
                     X509Certificate cert = (X509Certificate) trustedcert;
                  }
               }
            }
            trustmanagers = createTrustManagers(keystore);
         }
         SSLContext sslcontext = SSLContext.getInstance("SSL");
         sslcontext.init(keymanagers, trustmanagers, null);
         return sslcontext;
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new AuthSSLInitializationError("Unsupported algorithm exception: " + e.getMessage());
      }
      catch (KeyStoreException e)
      {
         throw new AuthSSLInitializationError("Keystore exception: " + e.getMessage());
      }
      catch (GeneralSecurityException e)
      {
         throw new AuthSSLInitializationError("Key management exception: " + e.getMessage());
      }
      catch (IOException e)
      {
         throw new AuthSSLInitializationError("I/O error reading keystore/truststore file: " + 
                                              e.getMessage());
      }
   }


   private SSLContext getSSLContext()
   {
      if (this.sslcontext == null)
      {
         this.sslcontext = createSSLContext();
      }
      return this.sslcontext;
   }

   public Socket createSocket(final String host, final int port, final InetAddress localAddress, 
                              final int localPort, final HttpConnectionParams params)
   throws IOException, UnknownHostException, ConnectTimeoutException
   {
      if (params == null)
      {
         throw new IllegalArgumentException("Parameters may not be null");
      }
      int timeout = params.getConnectionTimeout();
      SocketFactory socketfactory = getSSLContext().getSocketFactory();
      if (timeout == 0)
      {
         return socketfactory.createSocket(host, port, localAddress, localPort);
      }
      else
      {
         Socket socket = socketfactory.createSocket();
         SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
         SocketAddress remoteaddr = new InetSocketAddress(host, port);
         socket.bind(localaddr);
         socket.connect(remoteaddr, timeout);
         return socket;
      }
   }

   public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
   throws IOException, UnknownHostException
   {
      return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
   }

   public Socket createSocket(String host, int port)
   throws IOException, UnknownHostException
   {
      return getSSLContext().getSocketFactory().createSocket(host, port);
   }

   public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
   throws IOException, UnknownHostException
   {
      return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
   }
}

package br.com.validador.util;

import java.net.Socket;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
	
public class CustomX509KeyManager extends X509ExtendedKeyManager implements X509KeyManager
{
   private X509Certificate x509Certificate;
   private PrivateKey privateKey;


   public CustomX509KeyManager(X509Certificate certificate, PrivateKey privateKey)
   {
      this.x509Certificate = certificate;
      this.privateKey = privateKey;
   }

   public String chooseEngineClientAlias(String[] arg0, Principal[] arg1, SSLEngine arg2)
   {
      super.chooseEngineClientAlias(arg0, arg1, arg2);
      return x509Certificate.getIssuerDN().getName();
   }

   public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2)
   {
      return x509Certificate.getIssuerDN().getName();
   }


   public String chooseEngineServerAlias(String arg0, Principal[] arg1, SSLEngine arg2)
   {
      super.chooseEngineServerAlias(arg0, arg1, arg2);
      return null;
   }


   public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2)
   {
      return null;
   }


   public X509Certificate[] getCertificateChain(String arg0)
   {
      return new X509Certificate[]{x509Certificate};
   }


   public String[] getClientAliases(String arg0, Principal[] arg1)
   {
      return new String[]{x509Certificate.getIssuerDN().getName()};
   }


   public PrivateKey getPrivateKey(String arg0)
   {
      return privateKey;
   }


   public String[] getServerAliases(String arg0, Principal[] arg1)
   {
      return null;
   }
}
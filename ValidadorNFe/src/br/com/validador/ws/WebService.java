package br.com.validador.ws;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.w3c.dom.Document;

import br.com.validador.util.AuthSSLProtocolSocketFactory;
import br.com.validador.util.DOMUtil;


public class WebService
{
   static 
   {
      System.setProperty ( "javax.net.ssl.keyStore", "/usr/appl/validador/KeyStory_TrustStory.jks" ) ;
      System.setProperty ( "javax.net.ssl.keyStorePassword" , "123456" ) ;
      System.setProperty ( "javax.net.ssl.keyStoreProvider" , "SUN" ) ;
      System.setProperty ( "javax.net.ssl.trustStoreType" , "JKS" ) ;
      System.setProperty ( "javax.net.ssl.trustStore" , "/usr/appl/validador/KeyStory_TrustStory.jks" ) ;
      System.setProperty ( "javax.net.ssl.trustStorePassword" , "123456" ) ;
      System.setProperty ( "javax.net.ssl.trustStoreProvider" , "SUN" ) ;
   }
   
   public WebService()
   {
   }
   
   public static String solicitarWebService(String urlWebService, String arquivoSoap, String nome, String qname) throws Exception
   {
      PostMethod postMethod = null;
      KeyStore keyStore = null;
      String alias = null;
      X509Certificate certificate = null;
      PrivateKey privateKey = null;
      URL truststoreUrl = null;
      URL keystoreUrl = null;
      AuthSSLProtocolSocketFactory socket = null;
      Protocol protocol = null;
      HttpClient httpClient = null;
      String xmlResposta = null;
      try
      {
         postMethod = new PostMethod ( urlWebService );
         StringRequestEntity requestEntity;
         //TODO ALTERAR CERTIFICADO AQUI
         keyStore = KeyStore.getInstance("PKCS12");
         keyStore.load(new FileInputStream(new File("C:\\usr\\appl\\validador\\certificado.p12")), "123456".toCharArray());
         alias = (String)keyStore.aliases().nextElement();
         certificate = (X509Certificate)keyStore.getCertificate(alias);
         privateKey = (PrivateKey)keyStore.getKey(alias, "123456".toCharArray());
         truststoreUrl = new File("C:\\usr\\appl\\validador\\KeyStory_TrustStory.jks").toURI().toURL();
         keystoreUrl = new File("C:\\usr\\appl\\validador\\certificado.p12").toURI().toURL();
         socket = new AuthSSLProtocolSocketFactory(keystoreUrl, "123456", truststoreUrl, "123456", certificate, privateKey);
         protocol = new Protocol("https", (ProtocolSocketFactory)socket, 443);
         Protocol.registerProtocol("https", protocol);
         
         requestEntity = new StringRequestEntity ( arquivoSoap , "application/soap+xml" , "utf-8" ) ;
         postMethod.setRequestHeader ( new Header ( "nfeCabecMsg" ,  "<nfeCabecMsg soapenv:mustUnderstand=\"false\" xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/"+nome+"\"><cUF>51</cUF><versaoDados>2.00</versaoDados></nfeCabecMsg>" ) ) ;
         postMethod.setRequestEntity ( requestEntity ) ;

         // Configura o SOAP action para o método do web service que se deseja consumir
         postMethod.setRequestHeader ( "SOAPAction" ,  "http://www.portalfiscal.inf.br/nfe/wsdl/"+nome+"/"+qname ) ;
         // Obtém cliente HTTP
         httpClient = new HttpClient () ;
         // Executa requisição
         
         int codigoStatus = httpClient.executeMethod ( postMethod ) ;
         // Obtém o XML de retorno

         xmlResposta = postMethod.getResponseBodyAsString () ;

         if ( codigoStatus != 200 )
           throw new Exception ( xmlResposta ) ;
         
         return xmlResposta;
      }
      finally
      {
         postMethod = null;
         keyStore = null;
         alias = null;
         certificate = null;
         privateKey = null;
         truststoreUrl = null;
         keystoreUrl = null;
         socket = null;
         protocol = null;
         httpClient = null;
         xmlResposta = null;
      }
   }
   
   public static String solicitarWebServiceSemSSL(String urlWebService, String pathArquivoSoap, String nome, String qname) throws Exception
   {
      PostMethod postMethod = null;
      StringRequestEntity requestEntity = null;
      Document xmlDados = null;
      String conteudoXmlDados = null;
      HttpClient httpClient = null;
      String xmlResposta = null;
      try
      {
         postMethod = new PostMethod ( urlWebService ) ;
         for (int y = 0; y < 1; y++)
         {
           xmlDados = DOMUtil.parse(new File(pathArquivoSoap));
           conteudoXmlDados = DOMUtil.documentToString(xmlDados);
           requestEntity = new StringRequestEntity ( conteudoXmlDados , "application/soap+xml" , "utf-8" ) ;
           //postMethod.setRequestHeader ( new Header ( "nfeCabecMsg" ,  "<nfeCabecMsg soapenv:mustUnderstand=\"false\" xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/"+nome+"\"><versaoDados>1.00</versaoDados></nfeCabecMsg>" ) ) ;
           postMethod.setRequestEntity ( requestEntity ) ;
           // Configura o SOAP action para o método do web service que se deseja consumir
           postMethod.setRequestHeader ( "SOAPAction" ,  "http://www.portalfiscal.inf.br/nfe/wsdl/"+nome+"/"+qname) ;
           // Obtém cliente HTTP
           httpClient = new HttpClient () ;
           //System.out.println(conteudoXmlDados);
           // Executa requisição
           int codigoStatus = httpClient.executeMethod ( postMethod ) ;
           // Obtém o XML de retorno
           xmlResposta = postMethod.getResponseBodyAsString () ;
           //System.out.println ( xmlResposta ) ;
         }
         return xmlResposta;
      }
      finally
      {
         postMethod = null;
         requestEntity = null;
         xmlDados = null;
         conteudoXmlDados = null;
         httpClient = null;
         xmlResposta = null;
      }
   }
   
   public static void main(String[] args){
	   
	   String soap = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"><soapenv:Header><nfeCabecMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NfeConsulta2\"><cUF>35</cUF><versaoDados>3.10</versaoDados></nfeCabecMsg></soapenv:Header><soapenv:Body><nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NfeConsulta2\"><consSitNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"3.10\"><tpAmb>1</tpAmb><xServ>CONSULTAR</xServ><chNFe>35141109339936000205550160003286051648639040</chNFe></consSitNFe></nfeDadosMsg></soapenv:Body></soapenv:Envelope>";
	   //String url = "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeConsulta2";
	   String url = "https://nfe.fazenda.sp.gov.br/ws/nfeconsulta2.asmx";
	   
	   try{
		   WebService ws = new WebService();
		   System.out.println(ws.solicitarWebService(url, soap, "NfeConsulta2", "nfeConsulta2"));
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
   }
}

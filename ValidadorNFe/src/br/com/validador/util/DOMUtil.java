package br.com.validador.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class DOMUtil
{
   public DOMUtil()
   {
   }

   public static Document parse(File file)
   {
      Document document = null;
      DocumentBuilderFactory factory = null;

      try
      {
         factory = DocumentBuilderFactory.newInstance();
         factory.setValidating(false);
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         document = builder.parse(file);
      }
      catch(SAXParseException spe)
      {
         spe.printStackTrace();
         System.out.println("\n** Parsing error , line " + spe.getLineNumber() + 
               ", uri " + spe.getSystemId());
         System.out.println(" " + spe.getMessage());
      }
      catch(SAXException sxe)
      {
         sxe.printStackTrace();
      }
      catch(ParserConfigurationException pce)
      {
         pce.printStackTrace();
      }
      catch(IOException ioe)
      {
         ioe.printStackTrace();
      }

      return document;
   }

   public static Document parse(String xmlDados)
   {
      Document document = null;
      DocumentBuilderFactory factory = null;

      try
      {
         factory = DocumentBuilderFactory.newInstance();
         factory.setValidating(false);
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         document = builder.parse(new ByteArrayInputStream(xmlDados.getBytes()));
      }
      catch(SAXParseException spe)
      {
         spe.printStackTrace();
         System.out.println("\n** Parsing error , line " + spe.getLineNumber() + 
               ", uri " + spe.getSystemId());
         System.out.println(" " + spe.getMessage());
      }
      catch(SAXException sxe)
      {
         sxe.printStackTrace();
      }
      catch(ParserConfigurationException pce)
      {
         pce.printStackTrace();
      }
      catch(IOException ioe)
      {
         ioe.printStackTrace();
      }

      return document;
   }

   public static File writeXmlToFile(String filename, Document document)
   {
      File file = null;
      try
      {
         javax.xml.transform.Source source = new DOMSource(document);
         file = new File(filename);
         javax.xml.transform.Result result = new StreamResult(file);

         Transformer xformer = TransformerFactory.newInstance().newTransformer();
         xformer.transform(source, result);
      }
      catch(TransformerConfigurationException e)
      {
         System.out.println("TransformerConfigurationException: " + e);
      }
      catch(TransformerException e)
      {
         System.out.println("TransformerException: " + e);
      }

      return file;
   }


   public static String documentToString(Document document)
   throws IOException
   {
      StringWriter writer = null;

      try
      {
         javax.xml.transform.Source source = new DOMSource(document);
         writer = new StringWriter();
         javax.xml.transform.Result result = new StreamResult(writer);
         Transformer xformer = TransformerFactory.newInstance().newTransformer();
         xformer.transform(source, result);
         return writer.toString();
      }
      catch(TransformerConfigurationException e)
      {
         System.out.println("TransformerConfigurationException: " + e);
      }
      catch(TransformerException e)
      {
         System.out.println("TransformerException: " + e);
      }
      finally
      {
         if (writer != null)
         {
            writer.close();
         }
      }

      return null;
   }


   public static int countByTagName(String tag, Document document)
   {
      NodeList list = document.getElementsByTagName(tag);
      return list.getLength();
   }


   public static NodeList getNodeList(String tag, Document document)
   {
      NodeList list = document.getElementsByTagName(tag);
      return list;
   }


   public static NodeList getNodeList(String uri, String localName, 
      Document document)
   {
      NodeList list = document.getElementsByTagNameNS(uri, localName);
      return list;
   }


   public static String getValueElementsByTagNameNS(NodeList nodeList, String namespace, String name)
   {
      return getElementValue(((Element) nodeList.item(0)).getElementsByTagNameNS(namespace, 
               name).item(0));
   }


   public static String getElementValue(Node node)
   {
      return node.getChildNodes().item(0).getNodeValue();
   }
}
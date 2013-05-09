package com.aakashapp.modguru.src;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class ModifyQuizXML {

	File xmlFile;
	DocumentBuilderFactory documentBuilderFactory;
	DocumentBuilder documentBuilder;
	Document document;
	
	public ModifyQuizXML(String xmlFilePath) throws IOException, ParserConfigurationException, SAXException {
		xmlFile = new File(xmlFilePath);
		
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
		document = documentBuilder.parse(xmlFile);
	}
	
	public void setAttribute(String tag, String attr, String value) {
		Node tagNode = document.getElementsByTagName(tag).item(0);
		
		Node attrNode = document.createAttribute(attr);
		attrNode.setNodeValue(value);
		
		tagNode.getAttributes().setNamedItem(attrNode);
	}
	
	public void saveQuizFile() throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(xmlFile);
		transformer.transform(source, result);
	}
}

package com.yb.xmlvalidator.validation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import com.yb.xmlvalidator.service.RegexConstants;

public class XMLValidationCore {
	
	static Pattern patternFromURL = Pattern.compile(RegexConstants.regexSchemalocation);
	
	public void validateXMLAgainstSchema(InputStream xmlInputStream,StreamSource[] sourceSchemas) throws SAXException,IOException{
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source xmlFile = new StreamSource(xmlInputStream);
			Schema schema=factory.newSchema(sourceSchemas);
			Validator validator = schema.newValidator();  
		    	validator.validate(xmlFile);		    
	}
	
	public boolean isValidSoapEnvelope(InputStream is) throws MalformedURLException, IOException {
		StreamSource[] sourceSchemas = {new StreamSource((new URL(RegexConstants.soapNS)).openStream())};
		try {
			validateXMLAgainstSchema(is,sourceSchemas);
		} catch (SAXException | IOException e) {
			if(e.getMessage().toLowerCase().contains("must start and end")) {
				return false;
			}
			
			if(e.getMessage().toLowerCase().contains("envelope")) {
				return false;
			}
		}
		return true;
	}
	
	public String regexMatcher(Pattern patt,String input,String defaultReturn,List<Integer> groupNos) {
		Matcher matcher = patt.matcher(input);
		StringBuilder sb = new StringBuilder(groupNos.size());
		if(matcher.find()) {
			groupNos.forEach(groupNo->sb.append(matcher.group(groupNo)));
			return sb.toString();
		}else {
			return defaultReturn;
		}
		
	}
	
	public InputStream returnFinalXmlAfterStrippingSoapEnv(String xml,InputStream is) throws IOException {
		xml = xml.replaceAll(RegexConstants.removeCommentsRegex, "").replaceAll(RegexConstants.replaceNewLineRegex, "");
		
			String result = stripSoapEnv(xml,is);
			if(null==result) {
				return is;
			}else {
				
				return new ByteArrayInputStream(result.getBytes());
			}
	}
	
	 private String stripSoapEnv(String xml,InputStream is) throws IOException {
		String result = getSoapNS(xml,is);
		if(null!=result){
			String regexStripSoapEnv = ".+[\\s]*<"+result+":(?i)body>(.+)</"+result+":(?i)body>[\\s]*.+";
			Pattern patt = Pattern.compile(regexStripSoapEnv);
            return regexMatcher(patt, xml, null, new ArrayList<Integer>(Arrays.asList(1)));
		}
		return null;
	}
	
	 private String getSoapNS(String xml,InputStream is) throws IOException {
		
		String regexFindNS = ".+(?i)envelope[\\s]*xmlns:(.+)=\""+RegexConstants.soapNS+"\"";
		Pattern patt = Pattern.compile(regexFindNS);
		
		return regexMatcher(patt,xml,null,new ArrayList<Integer>(Arrays.asList(1)));
		
	   }

	 public String getContentFromURL(String url) throws ClientProtocolException, IOException {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet httpGet   =  new HttpGet(url);
				HttpResponse response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity, "UTF-8");
			
		}
	 
	 public void getAllXSDFromURL(String input,List<String> xsdList) throws ClientProtocolException, IOException {
			Matcher matcher = patternFromURL.matcher(input);
			while(matcher.find()){
				String xsd = matcher.group(2)+matcher.group(3)+matcher.group(4);
				if(!xsdList.contains(xsd)){
					xsdList.add(xsd);
				}
				getAllXSDFromURL(getContentFromURL(xsd),xsdList);
			}
		}
	 
	 public String checkForSlash(String input) {
		 int lastIndex = input.lastIndexOf("/");
		 if(lastIndex>=0) {
			 input = input.substring(lastIndex+1, input.length());
		 }
		 lastIndex = input.lastIndexOf("\\");
		 if(lastIndex>=0) {
			 input = input.substring(lastIndex+1, input.length());
		 }
		return input; 
	 }
}

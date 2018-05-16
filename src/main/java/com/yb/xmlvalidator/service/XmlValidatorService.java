package com.yb.xmlvalidator.service;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.yb.xmlvalidator.validation.ReturnBean;
import com.yb.xmlvalidator.validation.ValidateandFetchSchemas;
import com.yb.xmlvalidator.validation.XMLValidationCore;

@Service
public class XmlValidatorService {
	
	@Autowired
	XMLValidationCore core;
	
	public ReturnBean startValidationProcess(List<String> schemaFileNameList, List<String> schemaFileContentList,MultipartFile[] schemaFiles
			,MultipartFile xmlFile) throws MalformedURLException, Exception, SAXException {
		
		if(core.isValidSoapEnvelope(xmlFile.getInputStream())) {
			InputStream strippedXMLStream= core.returnFinalXmlAfterStrippingSoapEnv(new String(xmlFile.getBytes()),xmlFile.getInputStream());
			ReturnBean bean= validateUploadedSchemas(schemaFileNameList,schemaFileContentList);
			if(bean!=null && !"Valid".equalsIgnoreCase(bean.getMsg())) {
				return bean;
			}else {
				core.validateXMLAgainstSchema(strippedXMLStream, getSchemaStreamSourceArray(schemaFiles));
				return new ReturnBean("Valid", "XML is valid against schema(s)");
			}
		}else {
			return new ReturnBean("Error", "Invalid soap envelope of xml");
		}
		
	}
	
	public ReturnBean validateWithURL(String url,MultipartFile xmlFile) throws MalformedURLException, Exception, SAXException {
		List<String> xsdList = new ArrayList<String>(3);
		if(core.isValidSoapEnvelope(xmlFile.getInputStream())) {
			InputStream strippedXMLStream= core.returnFinalXmlAfterStrippingSoapEnv(new String(xmlFile.getBytes()),xmlFile.getInputStream());
			 core.getAllXSDFromURL(core.getContentFromURL(url), xsdList);
			 xsdList = xsdList.stream().filter(item->"xsd".equalsIgnoreCase(item.substring(item.length()-3))).collect(Collectors.toList());
			 StreamSource[] sourceSchemas = new StreamSource[xsdList.size()];
			for(int i=0;i<xsdList.size();i++) {
				sourceSchemas[i] = new StreamSource((new URL(xsdList.get(i))).openStream());
			}
			
			core.validateXMLAgainstSchema(strippedXMLStream,sourceSchemas);
			return new ReturnBean("Valid", "XML is valid against schema(s)");
			
		}else {
			return new ReturnBean("Error", "Invalid soap envelope of xml");
		}
		
	}
	
	public ReturnBean validateUploadedSchemas(List<String> schemaFileNameList, List<String> schemaFileContentList) throws InterruptedException, ExecutionException 
	   {
		
		 ReturnBean returnBean = new ReturnBean("Valid","Schema dependency is valid");
		
		List<String> fileUploadDiffList = new ArrayList<String>(3);
		List<Future<List<String>>> futureList = new ArrayList<Future<List<String>>>(schemaFileContentList.size());
		ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(schemaFileContentList.size());
		try {
		schemaFileContentList.forEach(content-> {
			Future<List<String>> future  = exec.submit(new ValidateandFetchSchemas(content, false, schemaFileNameList,core));
			futureList.add(future);
			});
		
		for(Future<List<String>> future: futureList) {
			
				List<String> list = future.get();
				list.forEach(item->{
					if(!fileUploadDiffList.contains(item)) {
						fileUploadDiffList.add(item);
					}
				});
			
		}
		
		if(!fileUploadDiffList.isEmpty()) {
			returnBean = new ReturnBean("Files Missing",fileUploadDiffList);
		}
				}finally {
			exec.shutdown();
		}
		return returnBean;
		
	}
	
	private StreamSource[] getSchemaStreamSourceArray(MultipartFile[] schemaFiles) throws Exception {
		List<StreamSource> sourceList = new ArrayList<StreamSource>(schemaFiles.length);
		for(int i=0;i<schemaFiles.length;i++) {
			 if(schemaFiles[i].getOriginalFilename().substring(schemaFiles[i].getOriginalFilename().length()-3).equalsIgnoreCase("xsd")) {
				 sourceList.add(new StreamSource(schemaFiles[i].getInputStream()));
			 }
		}
		return sourceList.toArray(new StreamSource[sourceList.size()]);
	}
	
}

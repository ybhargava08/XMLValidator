package com.yb.xmlvalidator.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.yb.xmlvalidator.service.XmlValidatorService;
import com.yb.xmlvalidator.validation.ReturnBean;
import com.yb.xmlvalidator.validation.XMLValidationCore;

@RestController
public class XmlValidatorController {
	
	@Autowired
	XmlValidatorService service;
	
	@Autowired
	XMLValidationCore core;
	
	@PostMapping(value="/uploadfiles",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public ReturnBean getMultipleFiles(@RequestParam("schemafiles") MultipartFile[] schemaFiles,@RequestParam("xmlfile") MultipartFile xmlFile) 
			throws SAXException, Exception {
		List<String> schemaFileNameList = new ArrayList<String>(schemaFiles.length);
		List<String> schemaFileContentList = new ArrayList<String>(schemaFiles.length);
		
		for(int i=0;i<schemaFiles.length;i++) {
			MultipartFile file = schemaFiles[i];
			schemaFileNameList.add(core.checkForSlash(file.getOriginalFilename()));			
				schemaFileContentList.add(new String(file.getBytes(),"UTF-8"));
		}
			return service.startValidationProcess(schemaFileNameList, schemaFileContentList,schemaFiles,xmlFile);
	
	}
	
	@PostMapping(value="/validateschemas",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public ReturnBean validateSchemas(@RequestParam("schemafiles") MultipartFile[] schemaFiles) 
			throws UnsupportedEncodingException, IOException, InterruptedException, ExecutionException {
		List<String> schemaFileNameList = new ArrayList<String>(schemaFiles.length);
		List<String> schemaFileContentList = new ArrayList<String>(schemaFiles.length);
		
		for(int i=0;i<schemaFiles.length;i++) {
			MultipartFile file = schemaFiles[i];
			schemaFileNameList.add(core.checkForSlash(file.getOriginalFilename()));			
				schemaFileContentList.add(new String(file.getBytes(),"UTF-8"));
		} 
			return service.validateUploadedSchemas(schemaFileNameList, schemaFileContentList);
	
	 }
	
	@PostMapping("/validateWithUrl")
	public ReturnBean validateWithUrl(@RequestPart("schemaUrl") String schemaUrl,@RequestPart("xmlfile") MultipartFile xmlFile) 
			throws MalformedURLException, SAXException, Exception {
		
			return service.validateWithURL(schemaUrl, xmlFile);
		
	}
}

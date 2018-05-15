package com.yb.xmlvalidator.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

@RestController
public class XmlValidatorController {

	@Autowired
	XmlValidatorService service;
	
	@PostMapping(value="/uploadfiles",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public ReturnBean getMultipleFiles(@RequestParam("schemafiles") MultipartFile[] schemaFiles,@RequestParam("xmlfile") MultipartFile xmlFile) {
		List<String> schemaFileNameList = new ArrayList<String>(schemaFiles.length);
		List<String> schemaFileContentList = new ArrayList<String>(schemaFiles.length);
		try {
		for(int i=0;i<schemaFiles.length;i++) {
			MultipartFile file = schemaFiles[i];
			schemaFileNameList.add(file.getOriginalFilename());			
				schemaFileContentList.add(new String(file.getBytes(),"UTF-8"));
		}
			return service.startValidationProcess(schemaFileNameList, schemaFileContentList,schemaFiles,xmlFile);
		} catch (IOException | SAXException e) {
			return new ReturnBean("Error",e.getMessage());
		}
	}
	
	@PostMapping(value="/validateschemas",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public ReturnBean validateSchemas(@RequestParam("schemafiles") MultipartFile[] schemaFiles) {
		List<String> schemaFileNameList = new ArrayList<String>(schemaFiles.length);
		List<String> schemaFileContentList = new ArrayList<String>(schemaFiles.length);
		try {
		for(int i=0;i<schemaFiles.length;i++) {
			MultipartFile file = schemaFiles[i];
			schemaFileNameList.add(file.getOriginalFilename());			
				schemaFileContentList.add(new String(file.getBytes(),"UTF-8"));
		} 
			return service.validateUploadedSchemas(schemaFileNameList, schemaFileContentList);
		}catch(IOException e) {
			 return new ReturnBean("Error",e.getMessage());
		}
	 }
	
	@PostMapping("/validateWithUrl")
	public ReturnBean validateWithUrl(@RequestPart("schemaUrl") String schemaUrl,@RequestPart("xmlfile") MultipartFile xmlFile) {
		try {
			return service.validateWithURL(schemaUrl, xmlFile);
		} catch (IOException | SAXException e) {
			return new ReturnBean("Error",e.getMessage());
		}
	}
}

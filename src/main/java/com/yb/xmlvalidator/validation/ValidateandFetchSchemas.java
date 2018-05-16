package com.yb.xmlvalidator.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yb.xmlvalidator.service.RegexConstants;

public class ValidateandFetchSchemas implements Callable<List<String>>{
		
	 static Pattern patternFromURL = Pattern.compile(RegexConstants.regexSchemalocation);
	 static Pattern patternFromBasePath = Pattern.compile(RegexConstants.regexSchemalocationFromFile);
	
	 private String schema;	 
	 private boolean fromURL;
	 private List<String> uploadedFileNameList;
	 
	 private XMLValidationCore core;
	 
	 public ValidateandFetchSchemas(String input,boolean fromURL,List<String> uploadedFileNameList,XMLValidationCore _core) {
		 this.schema = input;
		 this.fromURL = fromURL;
		 this.uploadedFileNameList = uploadedFileNameList;
		 this.core = _core;
	 }
	 
	@Override
	public List<String> call() throws Exception {
		List<String> xsdList = new ArrayList<String>(3);
		if(fromURL) {
			//getAllXSDFromURL(getContentFromURL(schema),xsdList);
		}else {
			 String inputSchema = (schema.length()>2001)?schema.substring(0, 2001):schema;
			inputSchema = inputSchema.replaceAll(RegexConstants.removeCommentsRegex,"").replaceAll(RegexConstants.replaceNewLineRegex, "");
			getAllXSDFromFile(inputSchema,xsdList);
		}
		
		return xsdList;
	}
	

	
	 void getAllXSDFromFile(String input,List<String> xsdList) throws IOException {
		 Matcher matcher = patternFromBasePath.matcher(input);
			while(matcher.find()){
				String xsdPath = matcher.group(2)+matcher.group(3);
				xsdPath = core.checkForSlash(xsdPath);
				if(!uploadedFileNameList.contains(xsdPath) && !xsdList.contains(xsdPath)){
					xsdList.add(xsdPath);
				}
			}
	 }
	 
}

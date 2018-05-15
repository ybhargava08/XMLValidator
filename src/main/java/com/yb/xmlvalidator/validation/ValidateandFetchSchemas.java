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
	 
	 public ValidateandFetchSchemas(String input,boolean fromURL,List<String> uploadedFileNameList) {
		 this.schema = input;
		 this.fromURL = fromURL;
		 this.uploadedFileNameList = uploadedFileNameList;
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
		
		
		//xsdList = xsdList.stream().filter(item->"xsd".equalsIgnoreCase(item.substring(item.length()-3))).collect(Collectors.toList());
		/*StreamSource[] sourceSchemas = new StreamSource[xsdList.size()];
		for(int i=0;i<xsdList.size();i++){
			InputStream iss = fromURL?(new URL(xsdList.get(i))).openStream():(InputStream) (new FileInputStream(new File(xsdList.get(i))));
		    sourceSchemas[i] = new StreamSource(iss);
		}
		
		if(sourceSchemas.length>0) {
			return sourceSchemas;
		}*/
		return xsdList;
	}
	
	/* String getContentFromURL(String url) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet httpGet   =  new HttpGet(url);
		String resp =null;
		try {
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			resp = EntityUtils.toString(entity, "UTF-8");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resp;
	}*/
	
	/* void getAllXSDFromURL(String input,List<String> xsdList) {
		Matcher matcher = patternFromURL.matcher(input);
		while(matcher.find()){
			String xsd = matcher.group(2)+matcher.group(3)+matcher.group(4);
			if(!xsdList.contains(xsd)){
				xsdList.add(xsd);
			}
			getAllXSDFromURL(getContentFromURL(xsd),xsdList);
		}
	}*/
	
	 void getAllXSDFromFile(String input,List<String> xsdList) throws IOException {
		 Matcher matcher = patternFromBasePath.matcher(input);
			while(matcher.find()){
				String xsdPath = matcher.group(2)+matcher.group(3);
				xsdPath = checkForSlash(xsdPath);
				if(!uploadedFileNameList.contains(xsdPath) && !xsdList.contains(xsdPath)){
					xsdList.add(xsdPath);
				}
			}
	 }
	 
	 /*String getFirstNLines(String filePath,long linesToRead) {
		 
			 StringBuilder sb = new StringBuilder();
			 BufferedReader br = null;
			 try {
				br = new BufferedReader(new FileReader(filePath));
				String line;
				long counter=0;
				
				while((line=br.readLine())!=null && counter<=linesToRead) {
					sb.append(line);
					counter++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if(null!=br) {
						br.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		 
		   return sb.toString();
	 }*/
	 
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

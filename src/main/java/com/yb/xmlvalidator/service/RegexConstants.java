package com.yb.xmlvalidator.service;

public class RegexConstants {

	public static final String regexSchemalocation = ".+?(?i)(location)[\\s]*=[\\s]*\"(?i)(http)(.+)(\\.xsd|wsdl)\".*?";
	public static final String regexSchemalocationFromFile = ".+?(?i)(location)[\\s]*=[\\s]*\"(.+?)(\\.xsd|wsdl)\".*?";
	public static final String soapNS="http://schemas.xmlsoap.org/soap/envelope/";
	public static final String removeCommentsRegex ="(?s)<!--.*?-->";
	public static final String replaceNewLineRegex = "[\\t\\n\\r]+";
}

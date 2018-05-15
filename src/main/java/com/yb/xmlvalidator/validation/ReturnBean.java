package com.yb.xmlvalidator.validation;

public class ReturnBean {

	private String msg;
	private Object data;
	
	public ReturnBean(String message,Object data) {
		this.msg = message;
		this.data = data;
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public String toString() {
		return this.msg+" "+this.data;
	}
	
}

package com.yb.xmlvalidator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

	@GetMapping("/xmlvalidator")
	public String getLoadingPage() {
		return "XmlValidate";
	}
}

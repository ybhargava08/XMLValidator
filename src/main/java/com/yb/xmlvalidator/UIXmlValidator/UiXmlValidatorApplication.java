package com.yb.xmlvalidator.UIXmlValidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import com.yb.xmlvalidator.validation.XMLValidationCore;

@SpringBootApplication
@ComponentScan(basePackages= {"com.yb.xmlvalidator"})
@EnableAspectJAutoProxy
public class UiXmlValidatorApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(UiXmlValidatorApplication.class, args);
	}
	
	@Bean
	public XMLValidationCore core() {
		return new XMLValidationCore();
	}
}

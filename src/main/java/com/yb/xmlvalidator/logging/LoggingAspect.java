package com.yb.xmlvalidator.logging;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.xml.sax.SAXException;

import com.yb.xmlvalidator.validation.ReturnBean;

@Configuration
@Aspect
public class LoggingAspect {

	Logger log = Logger.getLogger(LoggingAspect.class);
	
	@Around("execution(* com.yb.xmlvalidator..*.*(..)) && !execution(* com.yb.xmlvalidator.validation.XMLValidationCore.isValidSoapEnvelope(..))")
	public Object logMethodStartEnd(ProceedingJoinPoint pjp) throws Throwable {
		
		 StopWatch stopWatch = new StopWatch();
		 stopWatch.start();
		
		 log.info("Starting execution of class : "+pjp.getTarget().getClass().getName() +" and method : "+pjp.getSignature().getName());
		 Object returnVal = null;
		 try {
			 returnVal = pjp.proceed();
		}catch(Exception ex) {
			log.error("Logging exception from logging aspect",ex);
			if(pjp.getTarget().getClass().getName().toLowerCase().contains("controller")) {
				if(ex instanceof SAXException) {
					returnVal =new ReturnBean("Error",ex.getMessage());
				}else {
					returnVal = new ReturnBean("Error","Oops some error occurred !!");
				}
			}else {
				throw ex;
			}
			
		}
		
		log.info("After execution value from class : "+pjp.getTarget().getClass().getName() +" and method : "+pjp.getSignature().getName()
				+" Time Taken: "+stopWatch.getTotalTimeMillis()+" ms");
		return returnVal;
		
	}

	
}

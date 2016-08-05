package com.learnSpring.email;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunApp {

	public static void main(String[] args) {

		// Create a Spring container by providing the configuration file
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-mail.xml");

		// Get ePostMan bean and call sendMail()
		EPostMan postMan = (EPostMan) context.getBean("ePostMan");
		postMan.sendMail("kaminy.d@gmail.com", "kaminydoraisami@gmail.com", " Spring+OAuth mail with attachment",
				"Hello! \n\nThis email is sent using JavaMailSender\n\nCheck out attachment!");

	}
}
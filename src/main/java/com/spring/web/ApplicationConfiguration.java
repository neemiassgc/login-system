package com.spring.web;

import org.springframework.boot.SpringApplication;
import com.spring.web.controller.PageController;
import com.spring.web.controller.SessionController;
import com.spring.web.database.Transaction;
import com.spring.web.services.Token;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(value = {
		PageController.class, SessionController.class, Transaction.class, Token.class
} )

public class ApplicationConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationConfiguration.class, args);
	}
}

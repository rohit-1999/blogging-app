package com.blog;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BloggingApplication  {

	public static void main(String[] args) {
		SpringApplication.run(BloggingApplication.class, args);
	}
	
	
	
	@Bean
	public ModelMapper modelMapper() {
		
		return new ModelMapper();
	}

	

}

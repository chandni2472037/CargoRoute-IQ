package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TariffService1Application {

	public static void main(String[] args) {
		SpringApplication.run(TariffService1Application.class, args);
		

	}
	
	

}

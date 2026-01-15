package com.laddco.branchdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BranchdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BranchdemoApplication.class, args);
	}

}

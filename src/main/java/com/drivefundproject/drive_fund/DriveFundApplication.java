package com.drivefundproject.drive_fund;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableCaching
@EnableAsync
public class DriveFundApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriveFundApplication.class, args);
	}

}

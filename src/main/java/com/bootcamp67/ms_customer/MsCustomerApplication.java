package com.bootcamp67.ms_customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * The type Ms customer application.
 */
@SpringBootApplication
@EnableReactiveMongoRepositories
public class MsCustomerApplication {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
		SpringApplication.run(MsCustomerApplication.class, args);
	}

}

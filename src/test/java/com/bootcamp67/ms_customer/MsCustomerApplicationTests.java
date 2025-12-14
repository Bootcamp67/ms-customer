package com.bootcamp67.ms_customer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * The type Ms customer application tests.
 */
@SpringBootTest(
    properties = "spring.cloud.kubernetes.enabled=false"
)
@ActiveProfiles("test")
class MsCustomerApplicationTests {

  /**
   * Context loads.
   */
  @Test
	void contextLoads() {
	}

}

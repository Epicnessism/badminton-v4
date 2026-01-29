package com.wangindustries.badmintondbBackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

//These are IT tests, it loads the spring context
@SpringBootTest
@TestPropertySource( properties = {
		"dynamodb.local.enabled=true"
})
class BadmintondbBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}

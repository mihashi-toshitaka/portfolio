package com.mihashi_toshitaka.portfolio;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class PortfolioApplicationTests {

	@BeforeAll
	static void loadEnv() {
		Dotenv dotenv = Dotenv.load(); // .env を自動で探して読み込む
		dotenv.entries().forEach(entry -> {
			// Spring が参照できるように JVM の system properties に注入
			System.setProperty(entry.getKey(), entry.getValue());
		});
	}

	@Test
	void contextLoads() {
	}

}
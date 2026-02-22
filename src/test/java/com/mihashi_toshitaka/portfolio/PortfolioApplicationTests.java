package com.mihashi_toshitaka.portfolio;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PortfolioApplicationTests {

  @BeforeAll
  static void loadEnv() {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load(); // .env を自動で探して読み込む
    dotenv
        .entries()
        .forEach(
            entry -> {
              // Spring が参照できるように JVM の system properties に注入
              System.setProperty(entry.getKey(), entry.getValue());
            });
  }

  @Test
  void contextLoads() {}
}

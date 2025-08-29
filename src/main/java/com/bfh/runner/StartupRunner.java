package com.bfh.runner;

import com.bfh.config.AppProps;
import com.bfh.config.BfhProps;
import com.bfh.model.FinalQueryPayload;
import com.bfh.model.GenerateWebhookRequest;
import com.bfh.model.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class StartupRunner implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);
  private final WebClient webClient;
  private final AppProps appProps;
  private final BfhProps bfhProps;

  public StartupRunner(WebClient webClient, AppProps appProps, BfhProps bfhProps) {
    this.webClient = webClient;
    this.appProps = appProps;
    this.bfhProps = bfhProps;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("== BFH Java Qualifier: starting ==");
    log.info("Using regNo={}, name={}, email={}", appProps.getRegNo(), appProps.getName(), appProps.getEmail());

    // 1) Generate webhook
    GenerateWebhookResponse resp = webClient.post()
        .uri(bfhProps.getGenerateUrl())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new GenerateWebhookRequest(appProps.getName(), appProps.getRegNo(), appProps.getEmail()))
        .retrieve()
        .bodyToMono(GenerateWebhookResponse.class)
        .doOnError(e -> log.error("Failed to generate webhook", e))
        .block();

    if (resp == null || resp.getAccessToken() == null) {
      throw new IllegalStateException("No accessToken received from generateWebhook endpoint.");
    }

    final String accessToken = resp.getAccessToken();
    // Always submit to testWebhook/JAVA as per requirement
    final String targetWebhook = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    log.info("Received accessToken (JWT). Submitting final query to {}", targetWebhook);

    // 2) Final SQL query for Question 1 (since regNo is odd)
    String finalQuery =
        "SELECT \n" +
        "    p.AMOUNT AS SALARY,\n" +
        "    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,\n" +
        "    TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,\n" +
        "    d.DEPARTMENT_NAME\n" +
        "FROM PAYMENTS p\n" +
        "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID\n" +
        "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID\n" +
        "WHERE DAY(p.PAYMENT_TIME) <> 1\n" +
        "ORDER BY p.AMOUNT DESC\n" +
        "LIMIT 1;";

    // 3) Save locally
    Path out = Path.of("final-query.sql");
    Files.writeString(out, finalQuery);
    log.info("Saved final SQL to {}", out.toAbsolutePath());

    // 4) Submit the final query
    FinalQueryPayload payload = new FinalQueryPayload(finalQuery);

    String submitResponse = webClient.post()
        .uri(targetWebhook)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.set("Authorization", accessToken)) // JWT token
        .bodyValue(payload)
        .retrieve()
        .bodyToMono(String.class)
        .onErrorResume(e -> {
          log.error("Submission failed: {}", e.toString());
          return Mono.just("Submission failed: " + e.getMessage());
        })
        .block();

    log.info("Submission response: {}", submitResponse);
    log.info("== BFH Java Qualifier: done ==");
  }
}

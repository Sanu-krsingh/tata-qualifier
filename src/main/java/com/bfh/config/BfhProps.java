package com.bfh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bfh")
public class BfhProps {
  private String generateUrl;
  private String submitFallbackUrl;

  public String getGenerateUrl() { return generateUrl; }
  public void setGenerateUrl(String generateUrl) { this.generateUrl = generateUrl; }

  public String getSubmitFallbackUrl() { return submitFallbackUrl; }
  public void setSubmitFallbackUrl(String submitFallbackUrl) { this.submitFallbackUrl = submitFallbackUrl; }
}
 

 package com.bfh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProps {
  private String name;
  private String regNo;
  private String email;

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getRegNo() { return regNo; }
  public void setRegNo(String regNo) { this.regNo = regNo; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}

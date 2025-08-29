package com.bfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateWebhookResponse {
  @JsonProperty("accessToken")
  private String accessToken;

  @JsonProperty("webhook")
  private String webhook;

  public String getAccessToken() { return accessToken; }
  public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

  public String getWebhook() { return webhook; }
  public void setWebhook(String webhook) { this.webhook = webhook; }
}

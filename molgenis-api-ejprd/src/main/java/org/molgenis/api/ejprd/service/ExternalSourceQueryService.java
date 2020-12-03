package org.molgenis.api.ejprd.service;

import org.molgenis.api.ejprd.model.DataResponse;
import org.molgenis.api.ejprd.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class ExternalSourceQueryService implements ResourceQueryService {
  private static final Logger LOG = LoggerFactory.getLogger(InternalResourceQueryService.class);

  private RestTemplate restTemplate = new RestTemplate();
  private final String serviceBaseURL;

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public ExternalSourceQueryService(String serviceBaseURL) {
    this.serviceBaseURL = serviceBaseURL;
  }

  @Override
  public <T> T query(String orphaCode, String diseaseName, Integer skip, Integer limit) {
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(
            String.format("%s?orphaCode=%s", serviceBaseURL, orphaCode));

    ResponseEntity<String> response;
    try {
      response = restTemplate.getForEntity(builder.toUriString(), String.class);
    } catch (ResourceAccessException ex) {
      return null;
    }

    // TODO: handle NullPointerException in case response.getBody() is malformed
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return (T) DataResponse.fromJson(response.getBody());
    }
    return (T) ErrorResponse.fromJson(response.getBody());
  }

  @Override
  public DataResponse getById(String resourceId) {
    return null;
  }
}
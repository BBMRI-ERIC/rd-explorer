package org.molgenis.api.ejprd.model;

import com.google.auto.value.AutoValue;
import java.util.List;
import javax.annotation.Nullable;
import org.molgenis.api.model.response.PageResponse;

@AutoValue
public abstract class DataResponse {

  public abstract String getApiVersion();

  public abstract List<ResourceResponse> getResourceResponses();

  public abstract PageResponse getPage();

  @Nullable
  public abstract ErrorResponse getError();

  public static DataResponse create(
      String apiVersion,
      List<ResourceResponse> resourcesResponses,
      PageResponse page,
      ErrorResponse error) {
    return builder()
        .setApiVersion(apiVersion)
        .setResourceResponses(resourcesResponses)
        .setPage(page)
        .setError(error)
        .build();
  }

  public static DataResponse.Builder builder() {
    return new AutoValue_DataResponse.Builder();
  }

  @SuppressWarnings(
      "java:S1610") // Abstract classes without fields should be converted to interfaces
  @AutoValue.Builder
  public abstract static class Builder {

    public abstract DataResponse.Builder setApiVersion(String apiVersion);

    public abstract DataResponse.Builder setResourceResponses(
        List<ResourceResponse> resourcesResponses);

    public abstract DataResponse.Builder setError(ErrorResponse error);

    public abstract DataResponse.Builder setPage(PageResponse page);

    public abstract DataResponse build();
  }
}
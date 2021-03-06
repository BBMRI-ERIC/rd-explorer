package org.molgenis.data.file;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import org.molgenis.util.exception.CodedRuntimeException;

/** Thrown when unzipping a file fails. */
// S2166 'Classes named like "Exception" should extend "Exception" or a subclass' often gives false
// positives at dev time
@SuppressWarnings({"java:S110", "java:S2166"})
public class CodedUnzipException extends CodedRuntimeException {
  private static final String ERROR_CODE = "DF01";

  private final String name;

  public CodedUnzipException(String name) {
    super(ERROR_CODE);
    this.name = requireNonNull(name);
  }

  public CodedUnzipException(String name, Throwable cause) {
    super(ERROR_CODE, cause);
    this.name = requireNonNull(name);
  }

  @Override
  public String getMessage() {
    return format("name:%s", name);
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {name};
  }
}

package org.molgenis.web.exception;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.web.exception.ExceptionResponseType.PROBLEM;
import static org.molgenis.web.exception.Problem.Error.builder;

import java.net.URI;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.molgenis.i18n.ContextMessageSource;
import org.molgenis.test.AbstractMockitoTest;
import org.molgenis.util.exception.CodedRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class ProblemExceptionResponseGeneratorTest extends AbstractMockitoTest {
  @Mock private ContextMessageSource contextMessageSource;
  private ProblemExceptionResponseGenerator problemExceptionResponseGenerator;

  @BeforeEach
  void setUpBeforeMethod() {
    problemExceptionResponseGenerator = new ProblemExceptionResponseGenerator(contextMessageSource);

    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  void testGetType() {
    assertEquals(PROBLEM, problemExceptionResponseGenerator.getType());
  }

  @Test
  void testCreateExceptionResponse() {
    Exception exception = mock(Exception.class);
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    Problem problem =
        Problem.builder()
            .setType(URI.create("http://localhost/problem"))
            .setTitle("Bad Request")
            .setStatus(400)
            .build();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    ResponseEntity<Problem> responseEntity = new ResponseEntity<>(problem, httpHeaders, httpStatus);
    assertEquals(
        responseEntity,
        problemExceptionResponseGenerator.createExceptionResponse(exception, httpStatus, false));
  }

  @Test
  void testCreateExceptionResponseErrorCoded() {
    CodedRuntimeException exception = mock(CodedRuntimeException.class);
    when(exception.getErrorCode()).thenReturn("A1");
    when(exception.getLocalizedMessage()).thenReturn("message always exposed");
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    Problem problem =
        Problem.builder()
            .setType(URI.create("http://localhost/problem"))
            .setTitle("Bad Request")
            .setStatus(400)
            .setDetail("message always exposed")
            .setErrorCode("A1")
            .build();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    ResponseEntity<Problem> responseEntity = new ResponseEntity<>(problem, httpHeaders, httpStatus);
    assertEquals(
        responseEntity,
        problemExceptionResponseGenerator.createExceptionResponse(exception, httpStatus, false));
  }

  @Test
  void testCreateExceptionResponseDevEnvironment() {
    Exception exception = mock(Exception.class);
    when(exception.getLocalizedMessage()).thenReturn("message only exposed in dev environment");
    when(exception.getStackTrace())
        .thenReturn(
            new StackTraceElement[] {
              new StackTraceElement("declaringClass", "methodName", "fileName", 0)
            });
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    Problem problem =
        Problem.builder()
            .setType(URI.create("http://localhost/problem"))
            .setTitle("Bad Request")
            .setStatus(400)
            .setDetail("message only exposed in dev environment")
            .setStackTrace(singletonList("declaringClass.methodName(fileName:0)"))
            .build();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
    ResponseEntity<Problem> responseEntity = new ResponseEntity<>(problem, httpHeaders, httpStatus);
    assertEquals(
        responseEntity,
        problemExceptionResponseGenerator.createExceptionResponse(exception, httpStatus, true));
  }

  @Test
  void testCreateExceptionResponseInputInvalidObjectErrorCoded() {
    String errorCode = "MYERR01";
    String errorMessage = "My error message";

    CodedRuntimeException wrappedException = mock(CodedRuntimeException.class);
    when(wrappedException.getErrorCode()).thenReturn(errorCode);
    when(wrappedException.getLocalizedMessage()).thenReturn(errorMessage);

    ObjectError objectError = new ObjectError("MyChildObject0", "MyDefaultMessage0");
    objectError.wrap(wrappedException);

    BindingResult bindingResult = new MapBindingResult(emptyMap(), "MyObject");
    bindingResult.addError(objectError);

    BindException bindException = new BindException(bindingResult);
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    ResponseEntity<Problem> problemResponse =
        problemExceptionResponseGenerator.createExceptionResponse(bindException, httpStatus, false);
    assertEquals(httpStatus, problemResponse.getStatusCode());
    Problem problem = problemResponse.getBody();
    assertTrue(problem.getType().toString().endsWith("/input-invalid"));
    assertEquals(
        singletonList(builder().setErrorCode(errorCode).setDetail(errorMessage).build()),
        problem.getErrors());
  }

  @Test
  void testCreateExceptionResponseInputInvalidObjectErrorConstraintViolation() {
    String errorMessage = "My error message";

    ConstraintViolation constraintViolation = mock(ConstraintViolation.class);

    String code = "MYCODE";
    String[] arguments = {"arg0"};
    ObjectError objectError =
        new ObjectError("MyChildObject0", new String[] {code}, arguments, "MyDefaultMessage0");
    objectError.wrap(constraintViolation);

    BindingResult bindingResult = new MapBindingResult(emptyMap(), "MyObject");
    bindingResult.addError(objectError);

    BindException bindException = new BindException(bindingResult);
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    when(contextMessageSource.getMessage(code, arguments)).thenReturn(errorMessage);
    ResponseEntity<Problem> problemResponse =
        problemExceptionResponseGenerator.createExceptionResponse(bindException, httpStatus, false);
    assertEquals(httpStatus, problemResponse.getStatusCode());
    Problem problem = problemResponse.getBody();
    assertTrue(problem.getType().toString().endsWith("/input-invalid"));
    assertEquals(
        singletonList(builder().setErrorCode(code).setDetail(errorMessage).build()),
        problem.getErrors());
  }

  @Test
  void testCreateExceptionResponseInputInvalidFieldErrorCoded() {
    String errorCode = "MYERR01";
    String errorMessage = "My error message";

    CodedRuntimeException wrappedException = mock(CodedRuntimeException.class);
    when(wrappedException.getErrorCode()).thenReturn(errorCode);
    when(wrappedException.getLocalizedMessage()).thenReturn(errorMessage);

    String field = "MyField";
    String value = "invalidValue";
    FieldError fieldError =
        new FieldError(
            "MyObject",
            field,
            value,
            true,
            new String[] {errorCode},
            new String[] {"arg0"},
            "defaultMessage");
    fieldError.wrap(wrappedException);

    BindingResult bindingResult = new MapBindingResult(emptyMap(), "MyObject");
    bindingResult.addError(fieldError);

    BindException bindException = new BindException(bindingResult);
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    ResponseEntity<Problem> problemResponse =
        problemExceptionResponseGenerator.createExceptionResponse(bindException, httpStatus, false);
    assertEquals(httpStatus, problemResponse.getStatusCode());
    Problem problem = problemResponse.getBody();
    assertTrue(problem.getType().toString().endsWith("/input-invalid"));
    assertEquals(
        singletonList(
            builder()
                .setErrorCode(errorCode)
                .setField(field)
                .setValue(value)
                .setDetail(errorMessage)
                .build()),
        problem.getErrors());
  }
}

package ksp.vilius.usersupport.controllers;

import com.auth0.jwt.exceptions.TokenExpiredException;
import ksp.vilius.usersupport.exceptions.EmailExistsException;
import ksp.vilius.usersupport.exceptions.EmailNotFoundException;
import ksp.vilius.usersupport.exceptions.UserNotFoundException;
import ksp.vilius.usersupport.exceptions.UsernameExistsException;
import ksp.vilius.usersupport.models.HttpResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Locale;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandlerController {


    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {

        return createHttpResponse(BAD_REQUEST, "Your account has been disabled!");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {

        return createHttpResponse(BAD_REQUEST, "Incorrect credentials!");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {

        return createHttpResponse(FORBIDDEN, "You don't have enough permissions!");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedAccountException() {

        return createHttpResponse(UNAUTHORIZED, "Your account has been locked!");
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException() {

        return createHttpResponse(UNAUTHORIZED, "token expired");
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<HttpResponse> emailExistsException() {

        return createHttpResponse(BAD_REQUEST, "This email has been used already!");
    }

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<HttpResponse> usernameExistsException() {

        return createHttpResponse(BAD_REQUEST, "Username already exists");
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException() {

        return createHttpResponse(BAD_REQUEST, "The email you have provided has not been found!");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException() {

        return createHttpResponse(BAD_REQUEST, "This user doesn't exist!");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse> noHandlerFoundException() {
        return createHttpResponse(BAD_REQUEST, "This page not found");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        HttpMethod supportMethod = Objects.requireNonNull(ex.getSupportedHttpMethods().iterator().next());
        return createHttpResponse(METHOD_NOT_ALLOWED, "Method is not allowed" + supportMethod);
    }

    public ResponseEntity<HttpResponse> createHttpResponse(HttpStatus status, String message) {

        HttpResponse httpResponse = new HttpResponse(status.value(), status, status.getReasonPhrase().toUpperCase(Locale.ROOT), message);

        return new ResponseEntity(httpResponse, valueOf(status.value()));
    }
}

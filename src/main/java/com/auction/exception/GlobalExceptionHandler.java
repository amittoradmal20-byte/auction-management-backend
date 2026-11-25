
package com.auction.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==========================================
    // RESOURCE NOT FOUND - 404
    // ==========================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn(
                "Resource not found. URI='{}', Message='{}'",
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(
                        HttpStatus.NOT_FOUND,
                        ex.getMessage(),
                        request));
    }

    // ==========================================
    // DUPLICATE RESOURCE - 409
    // ==========================================

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        log.warn(
                "Duplicate resource. URI='{}', Message='{}'",
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(
                        HttpStatus.CONFLICT,
                        ex.getMessage(),
                        request));
    }

    // ==========================================
    // BUSINESS EXCEPTION - 400
    // ==========================================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex,
            HttpServletRequest request) {

        log.warn(
                "Business exception. URI='{}', Message='{}'",
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request));
    }

    // ==========================================
    // INVALID REFRESH TOKEN - 401
    // ==========================================

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse>
            handleInvalidRefreshTokenException(
                    InvalidRefreshTokenException ex,
                    HttpServletRequest request) {

        log.warn(
                "Invalid refresh token. URI='{}', Message='{}'",
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(
                        HttpStatus.UNAUTHORIZED,
                        ex.getMessage(),
                        request));
    }

    // ==========================================
    // UNAUTHORIZED ACCESS - 401
    // ==========================================

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {

        log.warn(
                "Unauthorized access. URI='{}', Message='{}'",
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(
                        HttpStatus.UNAUTHORIZED,
                        ex.getMessage(),
                        request));
    }

    // ==========================================
    // AUTHORIZATION DENIED - 403
    // ==========================================

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {

        log.warn(
                "Access denied. URI='{}'",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(
                        HttpStatus.FORBIDDEN,
                        "You do not have permission to access this resource.",
                        request));
    }

    // ==========================================
    // REQUEST VALIDATION - 400
    // ==========================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>>
            handleValidationException(
                    MethodArgumentNotValidException ex,
                    HttpServletRequest request) {

        log.warn(
                "Validation failed. URI='{}'",
                request.getRequestURI());

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();

        response.put("success", false);
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    // ==========================================
    // INVALID CREDENTIALS - 401
    // ==========================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        log.warn(
                "Authentication failed due to invalid credentials. URI='{}'",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid username or password.",
                        request));
    }

    // ==========================================
    // MAXIMUM UPLOAD SIZE EXCEEDED - 413
    // ==========================================

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request) {

        log.warn(
                "Maximum upload size exceeded. URI='{}', MaxSize={}",
                request.getRequestURI(),
                ex.getMaxUploadSize());

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(buildError(
                        HttpStatus.PAYLOAD_TOO_LARGE,
                        "Upload exceeds the maximum allowed size.",
                        request));
    }

    // ==========================================
    // MISSING MULTIPART REQUEST PART - 400
    // ==========================================

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestPart(
            MissingServletRequestPartException ex,
            HttpServletRequest request) {

        log.warn(
                "Required multipart request part missing. "
                + "URI='{}', Part='{}'",
                request.getRequestURI(),
                ex.getRequestPartName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST,
                        "Required multipart request part '"
                                + ex.getRequestPartName()
                                + "' is missing.",
                        request));
    }

    // ==========================================
    // MISSING REQUEST PARAMETER - 400
    // ==========================================

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        log.warn(
                "Required request parameter missing. "
                + "URI='{}', Parameter='{}'",
                request.getRequestURI(),
                ex.getParameterName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST,
                        "Required request parameter '"
                                + ex.getParameterName()
                                + "' is missing.",
                        request));
    }

    // ==========================================
    // INVALID MULTIPART REQUEST - 400
    // ==========================================

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipartException(
            MultipartException ex,
            HttpServletRequest request) {

        log.warn(
                "Invalid multipart request. URI='{}', Message='{}'",
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST,
                        "Invalid multipart request. "
                                + "Please provide a valid file upload.",
                        request));
    }

    // ==========================================
    // UNSUPPORTED MEDIA TYPE - 415
    // ==========================================

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        log.warn(
                "Unsupported media type. URI='{}', ContentType='{}'",
                request.getRequestURI(),
                ex.getContentType());

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(buildError(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        "The request Content-Type is not supported "
                                + "by this endpoint.",
                        request));
    }

    // ==========================================
    // INVALID PARAMETER TYPE - 400
    // ==========================================

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn(
                "Invalid request parameter type. "
                + "URI='{}', Parameter='{}'",
                request.getRequestURI(),
                ex.getName());

        String message =
                "Invalid value for parameter '"
                        + ex.getName()
                        + "'.";

        if (ex.getRequiredType() != null) {

            message += " Expected type: "
                    + ex.getRequiredType().getSimpleName()
                    + ".";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST,
                        message,
                        request));
    }

    // ==========================================
    // UNHANDLED EXCEPTION - 500
    // ==========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Unhandled exception. URI='{}'",
                request.getRequestURI(),
                ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. "
                                + "Please try again later.",
                        request));
    }

    // ==========================================
    // BUILD STANDARD ERROR RESPONSE
    // ==========================================

    private ErrorResponse buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage(message);
        errorResponse.setPath(request.getRequestURI());

        return errorResponse;
    }
}

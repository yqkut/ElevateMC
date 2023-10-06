package com.elevatemc.elib.util.imgur;

/**
 * Custom exception class that handles Web Exceptions.
 *
 * @author DV8FromTheWorld (Austin Keener)
 * @version v1.0.0  July 16, 2014
 */
@SuppressWarnings("serial")
public class ImgurException extends RuntimeException {
    private ImgurStatusCode code;

    /**
     * Creates a new instance of WebException containing
     * the StatusCode and the original exception.
     *
     * @param code  The StatusCode related to this exception.
     * @param cause The Throwable that set off this exception.
     */
    public ImgurException(ImgurStatusCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    /**
     * Creates a new instance of WebException containing the StatusCode.
     *
     * @param code The StatusCode related to this exception.
     */
    public ImgurException(ImgurStatusCode code) {
        this(code, null);
    }

    /**
     * Creates a new instance of WebException containing
     * the StatusCode(based on the provided httpCode) and the original exception.
     *
     * @param httpCode The httpCode related to this exception.
     * @param cause    The Throwable that set off this exception.
     */
    public ImgurException(int httpCode, Throwable cause) {
        this(ImgurStatusCode.getStatus(httpCode), cause);
    }

    /**
     * Creates a new instance of WebException containing
     * the StatusCode(based on the provided httpCode).
     *
     * @param httpCode The httpCode related to this exception.
     */
    public ImgurException(int httpCode) {
        this(httpCode, null);
    }

    /**
     * Gets the Http StatusCode associated with this exception.
     *
     * @return The StatusCode that caused the exception.
     */
    public ImgurStatusCode getStatusCode() {
        return code;
    }

    /**
     * Gets the description of the exception based on the description of the StatusCode.
     *
     * @return Description of exception based on Http StatusCode.
     */
    @Override
    public String getMessage() {
        return code.getDescription();
    }
}
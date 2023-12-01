package com.thelocalmarketplace.software.exceptions;

/**
 * Exception class - Thrown when the attendant tries to open the hardware when it is not disabled
 */
public class NotDisabledSessionException extends Exception {
    public NotDisabledSessionException(String str) {super(str);}
}

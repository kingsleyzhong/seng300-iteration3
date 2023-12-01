package com.thelocalmarketplace.software.exceptions;

/**
 * Exception class - Thrown when changes are attempted to be made to a station that is closed
 */
public class ClosedHardwareException extends Exception {
    public ClosedHardwareException(String str) {super(str);}
}

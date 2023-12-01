package com.thelocalmarketplace.software.exceptions;

/**
 * Exception class - Thrown when a coin/banknote of incorrect denomination is attempted to be input into a dispenser
 */
public class IncorrectDenominationException extends Exception {
    public IncorrectDenominationException(String str) {super(str);}
}

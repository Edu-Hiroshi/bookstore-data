package com.eduhi.bookstore.exception;

public class BadRequestException extends Exception{
    public BadRequestException(String message) {
        super (message);
    }
}

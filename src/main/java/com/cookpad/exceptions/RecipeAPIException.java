package com.cookpad.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RecipeAPIException extends RuntimeException {
    private HttpStatus status;

    public RecipeAPIException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}

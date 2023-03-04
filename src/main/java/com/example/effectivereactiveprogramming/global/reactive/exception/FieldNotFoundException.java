package com.example.effectivereactiveprogramming.global.reactive.exception;

import reactor.core.publisher.Mono;

import java.util.function.Function;

public class FieldNotFoundException extends RuntimeException {
    public FieldNotFoundException(String fieldName, Class<?> clazz) {
        super(String.format("Cannot find field '%s' in %s", fieldName, clazz));
    }
}

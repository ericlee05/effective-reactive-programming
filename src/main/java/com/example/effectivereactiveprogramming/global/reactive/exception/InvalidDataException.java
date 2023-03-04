package com.example.effectivereactiveprogramming.global.reactive.exception;

import reactor.core.publisher.Mono;

import java.util.function.Function;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(Function<?, Mono<Boolean>> condition) {
        super(String.format("Condition %s returned false.", condition.getClass()));
    }
}

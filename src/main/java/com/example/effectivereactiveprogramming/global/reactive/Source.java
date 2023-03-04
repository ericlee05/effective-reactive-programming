package com.example.effectivereactiveprogramming.global.reactive;

import com.example.effectivereactiveprogramming.global.reactive.exception.InvalidDataException;
import lombok.RequiredArgsConstructor;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class Source<T> {
    private final Mono<T> data;

    public static <T> Source<T> from(Mono<T> mono) {
        return new Source<>(mono);
    }

    public Source<T> valid(Function<T, Mono<Boolean>> condition) {
        return valid(condition, new InvalidDataException(condition));
    }

    public Source<T> valid(Function<T, Mono<Boolean>> condition, Throwable throwable) {
        return new Source<>(data.zipWith(data.flatMap(condition))
                .flatMap(tuple -> tuple.getT2() ? Mono.just(tuple.getT1()) : Mono.error(throwable)));
    }

    public Source<T> intercept(Consumer<T> consumer) {
        return new Source<>(data.doOnNext(consumer));
    }

    public <R> Source<R> postProcess(Function<T, R> converter) {
        return new Source<>(data.map(converter));
    }

    public Mono<T> complete() {
        return data;
    }
}

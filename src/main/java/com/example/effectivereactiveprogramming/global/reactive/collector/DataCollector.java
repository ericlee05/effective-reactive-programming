package com.example.effectivereactiveprogramming.global.reactive.collector;

import reactor.core.publisher.Mono;

public interface DataCollector<T> {
    Mono<T> compute();
}

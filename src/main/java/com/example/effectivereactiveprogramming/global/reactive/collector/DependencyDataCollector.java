package com.example.effectivereactiveprogramming.global.reactive.collector;

import com.example.effectivereactiveprogramming.global.reactive.exception.FieldNotFoundException;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DependencyDataCollector<T> implements DataCollector<T> {
    private static class TemporaryDataset {
        private final Map<Field, Object> fieldAndValues = new HashMap<>();
    }

    private final Class<T> clazz;
    private final Map<String, Function<T, Mono<?>>> bindings = new HashMap<>();

    public DependencyDataCollector(Class<T> clazz) {
        this.clazz = clazz;
    }

    public DependencyDataCollector<T> nextBind(String fieldName, Function<T, Mono<?>> mono) {
        bindings.put(fieldName, mono);
        return this;
    }

    private Mono<T> bindFieldAndValue(DependencyDataCollector.TemporaryDataset temporaryDataset) {
        ObjectUtil<T> util = new ObjectUtil<>(clazz);
        T instance = util.createInstance();

        for (Map.Entry<Field, Object> entry : temporaryDataset.fieldAndValues.entrySet())
            util.bindValue(instance, entry);

        return Mono.just(instance);
    }

    @Override
    public Mono<T> compute() {
        final List<Field> FIELDS = List.of(clazz.getDeclaredFields());

        Mono<DependencyDataCollector.TemporaryDataset> temporaryDataset = Mono.just(new DependencyDataCollector.TemporaryDataset());
        for (Map.Entry<String, Function<T, Mono<?>>> bind : bindings.entrySet()) {
            Field field = FIELDS.stream().filter(it -> it.getName().equals(bind.getKey())).findFirst()
                    .orElseThrow(() -> new FieldNotFoundException(bind.getKey(), clazz));

            Mono<Object> processedByLast = temporaryDataset.flatMap(this::bindFieldAndValue)
                    .flatMap(part -> bind.getValue().apply(part));

            temporaryDataset = temporaryDataset.zipWith(processedByLast, (tmp, data) -> {
                tmp.fieldAndValues.put(field, data);
                return tmp;
            });
        }

        return temporaryDataset.flatMap(this::bindFieldAndValue);
    }
}

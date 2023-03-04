package com.example.effectivereactiveprogramming.global.reactive.collector;

import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.util.Map;

@AllArgsConstructor
class ObjectUtil<T> {
    private final Class<T> clazz;

    public T createInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException exception) {
            throw new RuntimeException(String.format("Class %s does not have default constructor.", clazz));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void bindValue(T instance, Map.Entry<Field, Object> entry) {
        try {
            entry.getKey().setAccessible(true);
            entry.getKey().set(instance, entry.getValue());
            entry.getKey().setAccessible(false);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(String.format("Can't access field '%s'.", entry.getKey().getName()));
        }
    }
}

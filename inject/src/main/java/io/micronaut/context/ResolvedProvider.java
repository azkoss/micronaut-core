package io.micronaut.context;

import javax.inject.Provider;

/**
 * A resolved provider
 *
 * @author Graeme Rocher
 * @since 1.0
 */
class ResolvedProvider<T> implements Provider<T> {
    private final T bean;

    ResolvedProvider(T bean) {
        this.bean = bean;
    }

    @Override
    public T get() {
        return bean;
    }
}

package io.micronaut.inject.failures.ctorexception;

import javax.inject.Singleton;

@Singleton
public class C {
    public C() {
        throw new RuntimeException("bad");
    }
}

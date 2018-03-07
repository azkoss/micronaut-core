package io.micronaut.inject.field.protectedwithqualifier;

import io.micronaut.inject.qualifiers.One;

import javax.inject.Inject;
import javax.inject.Named;

public  class B {
    @Inject
    @One
    protected A a;

    @Inject
    @Named("twoA")
    protected A a2;

    public A getA() {
        return a;
    }

    public A getA2() {
        return a2;
    }
}

package io.micronaut.inject.constructor.multipleinjection

import io.micronaut.context.BeanContext
import io.micronaut.context.DefaultBeanContext
import io.micronaut.context.BeanContext
import io.micronaut.context.DefaultBeanContext
import spock.lang.Specification

class ConstructorMultipleInjectionSpec extends Specification {

    void "test injection with constructor"() {
        given:
        BeanContext context = new DefaultBeanContext()
        context.start()

        when:"A bean is obtained that has a constructor with @Inject and multiple arguments"
        B b =  context.getBean(B)

        then:"The implementation is injected"
        b.a != null
        b.a.is(context.getBean(A))
        b.c != null
        b.c.is(context.getBean(C))
    }
}
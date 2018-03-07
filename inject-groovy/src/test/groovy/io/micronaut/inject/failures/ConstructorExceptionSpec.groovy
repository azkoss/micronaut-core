package io.micronaut.inject.failures

import io.micronaut.context.BeanContext
import io.micronaut.context.DefaultBeanContext
import io.micronaut.context.exceptions.DependencyInjectionException
import io.micronaut.context.BeanContext
import io.micronaut.context.DefaultBeanContext
import io.micronaut.context.exceptions.DependencyInjectionException
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by graemerocher on 17/05/2017.
 */
class ConstructorExceptionSpec extends Specification {

    void "test error message when exception occurs in constructor"() {
        given:
        BeanContext context = new DefaultBeanContext()
        context.start()

        when:"A bean is obtained that has a setter with @Inject"
        B b =  context.getBean(B)

        then:"The implementation is injected"
        def e = thrown(DependencyInjectionException)
        //e.cause.message == 'bad'
        e.message == '''\
Failed to inject value for parameter [c] of class: io.micronaut.inject.failures.ConstructorExceptionSpec$A

Path Taken: B.a --> new A([C c])'''
    }

    @Singleton
    static class C {
        C() {
            throw new RuntimeException("bad")
        }
    }
    @Singleton
    static class A {
        A(C c) {

        }
    }

    static class B {
        @Inject
        private A a

        A getA() {
            return this.a
        }
    }

}

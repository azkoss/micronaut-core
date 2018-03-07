package io.micronaut.inject.constructor

import io.micronaut.context.BeanContext
import io.micronaut.context.DefaultBeanContext
import io.micronaut.context.BeanContext
import io.micronaut.context.DefaultBeanContext
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Singleton
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * Created by graemerocher on 26/05/2017.
 */
class ConstructorStreamSpec extends Specification {
    void "test injection via constructor that takes a stream"() {
        given:
        BeanContext context = new DefaultBeanContext()
        context.start()

        when:
        B b =  context.getBean(B)

        then:
        b.all != null
        b.all.size() == 2
        b.all.contains(context.getBean(AImpl))
    }

    static interface A {

    }

    @Singleton
    static class AImpl implements A {

    }

    @Singleton
    static class AnotherImpl implements A {

    }

    static class B {
        private Stream<A> all

        @Inject B(Stream<A> all) {
            this.all = all
        }

        private List<A> allList

        List<A> getAll() {
            if(allList == null) {
                allList = this.all.collect(Collectors.toList())
            }
            return allList
        }
    }
}


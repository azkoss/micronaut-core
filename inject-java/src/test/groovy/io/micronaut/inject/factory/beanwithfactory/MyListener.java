package io.micronaut.inject.factory.beanwithfactory;

import io.micronaut.context.event.BeanInitializedEventListener;
import io.micronaut.context.event.BeanInitializingEvent;

import javax.inject.Singleton;

@Singleton
public class MyListener implements BeanInitializedEventListener<BFactory> {

    @Override
    public BFactory onInitialized(BeanInitializingEvent<BFactory> event) {
        BFactory bean = event.getBean();
        assert bean.getMethodInjected() != null;
        assert bean.getFieldA() != null;
        assert bean.getAnotherField() != null;
        assert bean.a != null;
        assert !bean.postConstructCalled;
        assert !bean.getCalled;
        bean.name = "changed";
        return bean;
    }
}

package com.reliance.myeventbus;

import java.lang.reflect.Method;

/**
 * Created by sunz
 * E-mail itzhishuaisun@sina.com
 */

public class SubscribeMethod {
    private Method method;
    private ThreadModel thead;
    private Class<?> eventType;

    public SubscribeMethod(Method method, ThreadModel thead, Class<?> eventType) {
        this.method = method;
        this.thead = thead;
        this.eventType = eventType;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setThead(ThreadModel thead) {
        this.thead = thead;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }

    public Method getMethod() {
        return method;
    }

    public ThreadModel getThead() {
        return thead;
    }

    public Class<?> getEventType() {
        return eventType;
    }
}

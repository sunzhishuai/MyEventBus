package com.reliance.myeventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by sunzhish
 * E-mail itzhishuaisun@sina.com
 */
@Target(ElementType.METHOD)
public @interface SubscribeThread {
    ThreadModel Thread();
}

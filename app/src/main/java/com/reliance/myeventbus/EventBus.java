package com.reliance.myeventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * Created by sunzhis
 * E-mail itzhishuaisun@sina.com
 */

public class EventBus {
    private static EventBus instance;
    private Map<Class<?>, List<SubscribeMethod>> map;
    private Handler handler = new Handler();
    private ExecutorService executorService;

    private EventBus() {
        map = new HashMap<>();
    }


    public static EventBus getDefault() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    /**
     * 注册
     *
     * @param object
     */
    public void regist(Object object) {
        Class<?> aClass = object.getClass();
        List<SubscribeMethod> subscribeMethods = map.get(aClass);
        if (subscribeMethods == null || subscribeMethods.size() == 0) {
            subscribeMethods = new ArrayList<>();
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (int i = 0; i < declaredMethods.length; i++) {
                SubscribeThread annotation = declaredMethods[i].getAnnotation(SubscribeThread.class);
                if (annotation != null) {
                    Class<?>[] parameterTypes = declaredMethods[i].getParameterTypes();
                    if (parameterTypes.length != 1) {
                        throw new IllegalArgumentException("regist methods must is one");
                    } else {
                        ThreadModel thread = annotation.Thread();
                        Class<?> parameterType = parameterTypes[0];
                        subscribeMethods.add(new SubscribeMethod(declaredMethods[i], thread, parameterType));
                    }
                }
            }
            map.put(object.getClass(), subscribeMethods);
        }

        executorService = Executors.newCachedThreadPool();

    }

    /**
     * 注销
     *
     * @param object
     */

    public void unregist(Object object) {
        map.remove(object.getClass());
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 事件分发
     */
    public void post(final Object object) {
        //循环 map 找到 分发函数
        Iterator<Class<?>> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            final Class<?> methodClass = iterator.next();
            List<SubscribeMethod> subscribeMethods = map.get(methodClass);
            for (int i = 0; i < subscribeMethods.size(); i++) {
                final SubscribeMethod subscribeMethod = subscribeMethods.get(i);
                Class<?> eventType = subscribeMethod.getEventType();
                if (eventType.isAssignableFrom(object.getClass())) {
                    switch (subscribeMethod.getThead()) {
                        case MainThread:
                            if (Utils.isInMainThread())
                                invokeMethod(methodClass, subscribeMethod, object);
                            else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invokeMethod(methodClass, subscribeMethod, object);
                                    }
                                });
                            }
                            break;
                        case BackgroundThread:
                            if (Utils.isInMainThread()) {
                                Object o = null;
                                try {
                                    o = methodClass.newInstance();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                final Object finalO = o;
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            subscribeMethod.getMethod().invoke(finalO, object);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            final Object o = methodClass.newInstance();
                                            executorService.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        subscribeMethod.getMethod().invoke(o, object);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                            break;
                        case PostThead:
                            if (Utils.isInMainThread()) {
                                invokeMethod(methodClass, subscribeMethod, object);
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Object o = null;
                                        try {
                                            o = methodClass.newInstance();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        final Object finalO = o;
                                        executorService.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    subscribeMethod.getMethod().invoke(finalO, object);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                            break;
                    }
                }
            }
        }
    }

    private void invokeMethod(Class<?> methodClass, SubscribeMethod subscribeMethod, Object object) {
        try {
            Object o = methodClass.newInstance();
            subscribeMethod.getMethod().invoke(o, object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

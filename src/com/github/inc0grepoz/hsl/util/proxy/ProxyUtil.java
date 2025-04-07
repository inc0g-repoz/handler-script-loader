package com.github.inc0grepoz.hsl.util.proxy;

import java.lang.reflect.Proxy;

@SuppressWarnings("unchecked")
public class ProxyUtil {

    public static <T> T create(Class<T> clazz, Object object) {
        Class<?> objectClass = object.getClass();
        return (T) Proxy.newProxyInstance(
            ProxyUtil.class.getClassLoader(),
            new Class[] { clazz },
            (proxy, method, args) -> {
                return objectClass
                        .getMethod(method.getName(), method.getParameterTypes())
                        .invoke(object, args);
            }
        );
    }

}

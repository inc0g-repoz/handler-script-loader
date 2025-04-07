package com.github.inc0grepoz.hsl.util.proxy;

public interface IScript {

    static IScript of(Object object) {
        return ProxyUtil.create(IScript.class, object);
    }

    Object getFunction(String name, int paramCount);

}

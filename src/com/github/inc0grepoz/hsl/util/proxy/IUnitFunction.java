package com.github.inc0grepoz.hsl.util.proxy;

public interface IUnitFunction {

    static IUnitFunction of(Object object) {
        return ProxyUtil.create(IUnitFunction.class, object);
    }

    Object call(Object... params);

}

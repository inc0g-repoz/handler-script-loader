package com.github.inc0grepoz.hsl.util.proxy;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public interface IScriptExecutor {

    static IScriptExecutor of(Object object) {
        return ProxyUtil.create(IScriptExecutor.class, object);
    }

    Object load(Reader reader) throws IOException;

    Object load(File file) throws IOException;

    void unloadAll();

    void setLoaderDirectory(File loaderDirectory);

}

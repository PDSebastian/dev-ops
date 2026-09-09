package com.example.springbd3big.unit.support;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class InterfaceStubSupport {

    private InterfaceStubSupport() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T stub(Class<T> type, Map<String, Function<Object[], Object>> handlers) {
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    if (methodName.equals("toString")) {
                        return type.getSimpleName() + "Stub";
                    }
                    if (methodName.equals("hashCode")) {
                        return System.identityHashCode(proxy);
                    }
                    if (methodName.equals("equals")) {
                        return proxy == args[0];
                    }

                    Function<Object[], Object> handler = handlers.get(methodName);
                    if (handler != null) {
                        return handler.apply(args == null ? new Object[0] : args);
                    }

                    Class<?> returnType = method.getReturnType();
                    if (!returnType.isPrimitive()) {
                        return null;
                    }
                    if (returnType == boolean.class) {
                        return false;
                    }
                    if (returnType == char.class) {
                        return '\0';
                    }
                    return 0;
                }
        );
    }

    public static <T> Function<Object[], Object> constant(T value) {
        return args -> value;
    }

    public static <T> Function<Object[], Object> fail(RuntimeException exception) {
        Objects.requireNonNull(exception);
        return args -> {
            throw exception;
        };
    }
}

/*
 * This file is part of VisualVM for IDEA
 *
 * Copyright (c) 2008, Esko Luontola. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *
 *     * Neither the name of the copyright holder nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.orfjackal.visualvm4idea.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Esko Luontola
 * @since 18.10.2008
 */
public class Reflect {

    private final Object obj;
    private final Class<?> cls;

    public static Reflect on(Object obj) {
        return new Reflect(obj);
    }

    public static Reflect on(Class<?> cls) {
        return new Reflect(null, cls);
    }

    private Reflect(Object obj) {
        this(obj, obj == null ? null : obj.getClass());
    }

    private Reflect(Object obj, Class<?> cls) {
        this.obj = obj;
        this.cls = cls;
    }

    public Object value() {
        return obj;
    }

    public Reflect field(String fieldName) {
        try {
            for (Class<?> cls = this.cls; cls != null; cls = cls.getSuperclass()) {
                try {
                    return tryReadDeclaredField(cls, fieldName);
                } catch (NoSuchFieldException e) {
                    // try superclass
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("No field '" + fieldName + "' in class " + cls);
    }

    public MethodCall method(String methodName, Class<?>... parameterTypes) {
        try {
            for (Class<?> cls = this.cls; cls != null; cls = cls.getSuperclass()) {
                try {
                    return tryCallDeclaredMethod(cls, methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                    // try superclass
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("No method '" + methodName + "' in class " + cls);
    }

    private Reflect tryReadDeclaredField(Class<?> cls, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = cls.getDeclaredField(fieldName);
        field.setAccessible(true);
        return new Reflect(field.get(obj));
    }

    private MethodCall tryCallDeclaredMethod(Class<?> cls, String methodName, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = cls.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return new MethodCall(method);
    }


    public class MethodCall {

        private final Method method;

        private MethodCall(Method method) {
            this.method = method;
        }

        public Reflect with(Object... parameters) {
            try {
                return new Reflect(method.invoke(obj, parameters));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

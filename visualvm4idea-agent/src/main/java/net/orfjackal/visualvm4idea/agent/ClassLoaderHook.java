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

package net.orfjackal.visualvm4idea.agent;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Esko Luontola
 * @since 10.10.2008
 */
public class ClassLoaderHook {

    public static final String HOOK_LIB_PROPERTY = "net.orfjackal.visualvm4idea.agent.hookLibrary";

    private static boolean hooked = false;

    public static synchronized void hook(ClassLoader parent) {
        if (!hooked) {
            hooked = true;
            try {
                tryStartHookInClassLoader(parent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void tryStartHookInClassLoader(ClassLoader parent) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ClassLoader loader = new URLClassLoader(new URL[]{getHookLibrary()}, parent);
        Class<?> clazz = loader.loadClass("net.orfjackal.visualvm4idea.core.HookInstaller");
        clazz.getMethod("start").invoke(null);
    }

    private static URL getHookLibrary() throws MalformedURLException {
        return new File(System.getProperty(HOOK_LIB_PROPERTY)).toURI().toURL();
    }
}

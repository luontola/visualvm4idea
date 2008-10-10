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

package net.orfjackal.visualvm4idea.core;

import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.host.Host;

import java.util.Set;

/**
 * @author Esko Luontola
 * @since 10.10.2008
 */
public class DebugRunner implements Runnable {

    private static Thread t;

    public synchronized static void start() {
        if (t == null) {
            t = new Thread(new DebugRunner());
            t.setDaemon(true);
            t.start();
            System.out.println("DebugRunner started");
        }
    }

    public void run() {
        while (true) {
            try {
                printDebugInfo();
            } catch (Throwable t) {
                t.printStackTrace(System.out);
                return;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private void printDebugInfo() {
        System.out.println("---");
//        ClassLoader cl = DebugRunner.class.getClassLoader();
//        System.out.println("cl = " + cl);
//        try {
//            System.getProperties().store(System.out, null);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        Set<DataSource> dataSources = DataSourceRepository.sharedInstance().getDataSources();
        Set<DataSource> dataSources = Host.LOCALHOST.getRepository().getDataSources();
        for (DataSource dataSource : dataSources) {
            System.out.println("dataSource: " + dataSource);
        }
    }
}

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

package net.orfjackal.visualvm4idea.visualvm;

import net.orfjackal.visualvm4idea.util.ProcessExecutorImpl;
import net.orfjackal.visualvm4idea.util.ServerConnection;
import net.orfjackal.visualvm4idea.util.ServerExecutor;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Esko Luontola
 * @since 17.10.2008
 */
public class VisualVmController {

    private volatile ServerConnection connection;
    private volatile ServerExecutor executor;

    public void beginProfilingApplication(int port) {
        // TODO: class loader problems, the class loader which is used on visualvm to load objects from stream, can not find this class
        // maybe it would be better to just send strings?
        runOnVisualVm(new BeginProfilingApplicationOnPort(port));
    }

    private Object runOnVisualVm(Callable<?> command) {
        startVisualVmIfNotRunning();
        awaitVisualVmConnected();
        try {
            return executor.runRemotely(command);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            closeConnection();
            throw new RuntimeException(e);
        }
    }

    private void startVisualVmIfNotRunning() {
        if (connection == null) {
            try {
                connection = new ServerConnection();
                executor = new ServerExecutor(connection);
                String agentPath = "D:\\DEVEL\\VisualVM for IDEA\\visualvm4idea\\visualvm4idea-dist\\target\\visualvm4idea\\lib\\visualvm4idea-visualvm-agent.jar";
                String libPath = "D:\\DEVEL\\VisualVM for IDEA\\visualvm4idea\\visualvm4idea-dist\\target\\visualvm4idea\\lib\\visualvm4idea-core.jar";

                new ProcessExecutorImpl()
                        .exec("D:\\DEVEL\\VisualVM for IDEA\\visualvm_101\\bin\\visualvm.exe",
                                "-J-javaagent:" + agentPath,
                                "-J-Dvisualvm4idea.lib=" + libPath,
                                "-J-Dvisualvm4idea.port=" + connection.getPort());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void awaitVisualVmConnected() {
        if (connection.isConnected()) {
            return;
        }
        try {
            connection.awaitClientConnected(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            connection = null;
        }
        if (connection != null && connection.isConnected()) {
            return;
        }
        throw new IllegalStateException("Not connected to VisualVM");
    }

    private void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connection = null;
        executor = null;
    }


    public static class BeginProfilingApplicationOnPort implements Callable<Object>, Serializable {
        private static final long serialVersionUID = 1L;

        private final int port;

        public BeginProfilingApplicationOnPort(int port) {
            this.port = port;
        }

        public Object call() throws Exception {
            System.out.println("VisualVmController$BeginProfilingApplicationOnPort.call");
            System.out.println("port = " + port);
            return null;
        }
    }

    public static void main(String[] args) {
        new VisualVmController().beginProfilingApplication(5140);
    }
}

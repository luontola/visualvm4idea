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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Esko Luontola
 * @since 17.10.2008
 */
public class ServerConnection {

    private final ServerSocket serverSocket;
    private final Lock lock = new ReentrantLock();
    private final Condition clientConnected = lock.newCondition();

    private volatile Socket socket;
    private volatile ObjectInputStream in;
    private volatile ObjectOutputStream out;

    public ServerConnection() throws IOException {
        serverSocket = new ServerSocket(0);
        Thread t = new Thread(new WaitForOneClientToConnect());
        t.setDaemon(true);
        t.start();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public ObjectInputStream getInput() {
        assert isConnected();
        return in;
    }

    public ObjectOutputStream getOutput() {
        assert isConnected();
        return out;
    }

    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    public void awaitClientConnected() throws InterruptedException {
        lock.lock();
        try {
            clientConnected.await();
        } finally {
            lock.unlock();
        }
    }

    public void awaitClientConnected(int timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        try {
            clientConnected.await(timeout, unit);
        } finally {
            lock.unlock();
        }
    }

    private void signalClientConnected() {
        lock.lock();
        try {
            clientConnected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private class WaitForOneClientToConnect implements Runnable {
        public void run() {
            try {
                socket = serverSocket.accept();
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                serverSocket.close();
                signalClientConnected();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}

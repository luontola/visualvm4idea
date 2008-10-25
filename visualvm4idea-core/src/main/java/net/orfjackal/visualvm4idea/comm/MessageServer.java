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

package net.orfjackal.visualvm4idea.comm;

import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @author Esko Luontola
 * @since 25.10.2008
 */
public class MessageServer implements MessageSender {

    private final MessageClientLauncher clientLauncher;
    private final ServerSocket serverSocket;
    private final BlockingQueue<RequestHandle> requests = new LinkedBlockingQueue<RequestHandle>();

    public MessageServer(MessageClientLauncher clientLauncher) {
        this.clientLauncher = clientLauncher;
        try {
            this.serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Thread t = new Thread(new MessageQueueProsessor());
        t.setDaemon(true);
        t.start();
    }

    public Future<String[]> send(String... message) {
        RequestHandle msg = new RequestHandle(message);
        requests.add(msg);
        return msg;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @TestOnly
    int getRequestQueueSize() {
        return requests.size();
    }

    private void retryRequest(RequestHandle request) throws InterruptedException {
        ArrayList<RequestHandle> tmp = new ArrayList<RequestHandle>();
        tmp.add(request);
        requests.drainTo(tmp);
        requests.addAll(tmp);
    }

    private class MessageQueueProsessor implements Runnable {

        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public void run() {
            try {
                while (true) {
                    tryToProcessNextRequest();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void tryToProcessNextRequest() throws InterruptedException {
            RequestHandle request = requests.take();
            try {
                if (notConnected()) {
                    openConnection();
                }
                processRequest(request);
            } catch (IOException e) {
                retryRequest(request);
                closeConnection();
            }
        }

        private boolean notConnected() {
            return socket == null;
        }

        private void openConnection() throws IOException {
            launchNewClient();
            acceptConnectionToClient();
        }

        private void launchNewClient() {
            clientLauncher.launch(getPort());
        }

        private void acceptConnectionToClient() throws IOException {
            socket = serverSocket.accept();
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }

        private void processRequest(RequestHandle request) throws IOException {
            out.writeObject(request.getMessage());
            out.flush();
            String[] response = readNextResponse();
            request.setResponse(response);
        }

        private String[] readNextResponse() throws IOException {
            try {
                return (String[]) in.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        private void closeConnection() {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                socket = null;
            }
        }
    }

    private static class RequestHandle extends FutureTask<String[]> {

        private final String[] message;

        public RequestHandle(String[] message) {
            super(new NullCallable());
            this.message = message;
        }

        public String[] getMessage() {
            return message;
        }

        public void setResponse(String[] response) {
            super.set(response);
        }
    }

    private static class NullCallable implements Callable<String[]> {
        public String[] call() throws Exception {
            throw new UnsupportedOperationException();
        }
    }
}

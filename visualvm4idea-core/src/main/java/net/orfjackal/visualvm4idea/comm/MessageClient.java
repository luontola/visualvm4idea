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

import java.io.*;
import java.net.Socket;

/**
 * @author Esko Luontola
 * @since 25.10.2008
 */
public class MessageClient {

    private final MessageReciever reciever;
    private final int port;

    private volatile Socket socket;
    private volatile ObjectInputStream in;
    private volatile ObjectOutputStream out;


    public MessageClient(MessageReciever reciever, int port) {
        this.reciever = reciever;
        this.port = port;
        Thread t = new Thread(new RequestProsessor());
        t.setDaemon(true);
        t.start();
    }

    private void connect() throws IOException {
        socket = new Socket("localhost", port);
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class RequestProsessor implements Runnable {

        public void run() {
            try {
                connect();
                while (true) {
                    tryToProcessNextRequest(in, out);
                }
            } catch (IOException e) {
                // connection closed
            } finally {
                close();
            }
        }

        private void tryToProcessNextRequest(ObjectInput in, ObjectOutput out) throws IOException {
            String[] request = readNextRequest(in);
            String[] response = reciever.messageRecieved(request);
            out.writeObject(response);
            out.flush();
        }

        private String[] readNextRequest(ObjectInput in) throws IOException {
            try {
                return (String[]) in.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

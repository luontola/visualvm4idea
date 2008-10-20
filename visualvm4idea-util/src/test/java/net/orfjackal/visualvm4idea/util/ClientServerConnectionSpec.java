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

import jdave.Block;
import jdave.Group;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Esko Luontola
 * @since 17.10.2008
 */
@RunWith(JDaveRunner.class)
@Group({"fast"})
public class ClientServerConnectionSpec extends Specification<Object> {

    private ServerConnection server;
    private ClientConnection client;

    public void create() throws Exception {
        server = new ServerConnection();
    }

    public void destroy() throws Exception {
        if (server != null) {
            server.close();
        }
        if (client != null) {
            client.close();
        }
    }

    private void startClient() throws Exception {
        client = new ClientConnection(server.getPort());
        server.awaitClientConnected(100, TimeUnit.MILLISECONDS);
    }


    public class WhenTheClientIsStarted {

        public Object create() throws IOException {
            return null;
        }

        public void theClientConnectsToTheServer() throws Exception {
            specify(!server.isConnected());
            startClient();
            specify(server.isConnected());
        }

        public void aSecondClientCanNotConnectToTheServer() throws Exception {
            startClient();
            specify(server.isConnected());
            specify(new Block() {
                public void run() throws Throwable {
                    new ClientConnection(server.getPort());
                }
            }, should.raise(IOException.class));
            specify(server.isConnected());
        }

        public void clientCanSendMessagesToTheServer() throws Exception {
            startClient();
            client.getOutput().writeUTF("message");
            client.getOutput().flush();
            specify(server.getInput().readUTF(), should.equal("message"));
        }

        public void serverCanSendMessagesToTheClient() throws Exception {
            startClient();
            server.getOutput().writeUTF("message");
            server.getOutput().flush();
            specify(client.getInput().readUTF(), should.equal("message"));
        }
    }
}

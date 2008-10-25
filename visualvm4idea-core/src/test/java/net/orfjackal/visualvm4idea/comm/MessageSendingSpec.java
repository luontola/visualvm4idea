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

import jdave.Group;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Esko Luontola
 * @since 25.10.2008
 */
@RunWith(JDaveRunner.class)
@Group({"fast"})
public class MessageSendingSpec extends Specification<Object> {


    public class WhenServerIsCreated {

        private MessageServer server;
        private MessageClientLauncher clientLauncher;

        public void create() throws Exception {
            clientLauncher = mock(MessageClientLauncher.class);
            server = new MessageServer(clientLauncher);
        }

        public void noMessagesAreQueued() {
            specify(server.getRequestQueueSize(), should.equal(0));
        }

        public void sendingAMessageWillQueueTheMessageAndLaunchAClient() throws Exception {
            checking(new Expectations() {{
                one(clientLauncher).launch(server.getPort());
            }});
            server.send("message");
            specify(server.getRequestQueueSize(), should.equal(1));
            Thread.sleep(30); // wait for ClientLauncher.launch()
        }
    }

    public class WhenClientConnectsToServer {

        private MessageServer server;
        private MessageReciever clientReciever;

        public void create() throws Exception {
            clientReciever = mock(MessageReciever.class);
            server = new MessageServer(new MessageClientLauncher() {
                public void launch(int port) {
                    new MessageClient(clientReciever, port);
                }
            });
        }

        public void messagesAreQueuedAndSentToClient() throws Exception {
            checking(new Expectations() {{
                one(clientReciever).messageRecieved("message"); will(returnValue(new String[]{"OK"}));
            }});
            server.send("message");
            specify(server.getRequestQueueSize(), should.equal(1));
            Thread.sleep(30); // wait for message to arrive
        }

        public void theServerWillRecieveAResponseFromTheClient() throws Exception {
            checking(new Expectations() {{
                one(clientReciever).messageRecieved("message"); will(returnValue(new String[]{"OK"}));
            }});
            Future<String[]> response = server.send("message");
            specify(response.get(100, TimeUnit.MILLISECONDS), should.containInOrder("OK"));
        }
        
        public void multipleMessagesCanBeSent() throws Exception {
            checking(new Expectations() {{
                one(clientReciever).messageRecieved("message1"); will(returnValue(new String[]{"OK1"}));
                one(clientReciever).messageRecieved("message2"); will(returnValue(new String[]{"OK2"}));
            }});
            specify(server.send("message1").get(100, TimeUnit.MILLISECONDS), should.containInOrder("OK1"));
            specify(server.send("message2").get(100, TimeUnit.MILLISECONDS), should.containInOrder("OK2"));
        }
    }
}

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
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

/**
 * @author Esko Luontola
 * @since 17.10.2008
 */
@RunWith(JDaveRunner.class)
@Group({"fast"})
public class RemoteExecutionSpec extends Specification<Object> {

    public class WhenServerSendsACommandToTheClient {

        private ServerConnection serverCon;
        private ClientConnection clientCon;
        private ServerExecutor server;
        private ClientExecutor client;

        public Object create() throws IOException {
            serverCon = new ServerConnection();
            clientCon = new ClientConnection(serverCon.getPort());
            server = new ServerExecutor(serverCon);
            client = new ClientExecutor(clientCon);
            return null;
        }

        public void destroy() throws IOException {
            clientCon.close();
            serverCon.close();
        }

        public void theClientWillExecuteTheCommandAndReturnTheValue() throws Exception {
            specify(server.runRemotely(new ValueCallable("foo")), should.equal("foo"));
        }

        public void ifTheClientThrowsAnExceptionTheServerWillRethrowIt() {
            specify(new Block() {
                public void run() throws Throwable {
                    server.runRemotely(new ExceptionCallable());
                }
            }, should.raise(RemoteException.class));
        }
    }

    private static class ValueCallable implements Callable<String>, Serializable {

        private final String value;

        public ValueCallable(String value) {
            this.value = value;
        }

        public String call() throws Exception {
            return value;
        }
    }

    private static class ExceptionCallable implements Callable<String>, Serializable {

        public String call() throws Exception {
            throw new IllegalArgumentException();
        }
    }
}

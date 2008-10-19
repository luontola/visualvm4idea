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

package net.orfjackal.visualvm4idea.handles;

import jdave.Group;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import net.orfjackal.visualvm4idea.program.agent.ProfiledAppAgent;
import net.orfjackal.visualvm4idea.util.ServerConnection;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Esko Luontola
 * @since 17.10.2008
 */
@RunWith(JDaveRunner.class)
@Group({"fast"})
public class StartingProfiledApplicationSpec extends Specification<Object> {

    public class WhenTheApplicationToBeProfiledStarts {

        private ServerConnection server;
        private ProfiledAppHandle handle;
        private ProfiledApp app;

        public Object create() throws IOException {
            server = new ServerConnection();
            handle = new ProfiledAppHandle(server);
            app = new ProfiledApp(server.getPort());
            return null;
        }

        public void destroy() throws IOException {
            server.close();
        }

/*
        public void itConnectsToTheProfilerAndWaitsForInstructions() throws InterruptedException {
            specify(server.isConnected(), should.equal(false));
            startProfiledApp();
            specify(server.isConnected(), should.equal(true));
            specify(app.premainEntered());
            app.main.await(100, TimeUnit.MILLISECONDS);
            specify(!app.mainEntered());
        }
*/

/*
        public void itContinuesExecutionWhenToldSoByTheProfiler() throws InterruptedException {
            startProfiledApp();
            specify(!app.mainEntered());
            handle.resumeApplication();
            app.main.await(100, TimeUnit.MILLISECONDS);
            specify(app.mainEntered());
        }
*/

        private void startProfiledApp() {
            Thread t = new Thread(app);
            t.setDaemon(true);
            t.start();
            try {
                server.awaitConnection(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ProfiledApp implements Runnable {

        private final CountDownLatch premain = new CountDownLatch(1);
        private final CountDownLatch main = new CountDownLatch(1);
        private final int port;

        public ProfiledApp(int port) {
            this.port = port;
        }

        public void run() {
            premain.countDown();
            ProfiledAppAgent.premain("port=" + port, null);
            main.countDown();
        }

        public boolean premainEntered() {
            return premain.getCount() == 0;
        }

        public boolean mainEntered() {
            return main.getCount() == 0;
        }
    }
}

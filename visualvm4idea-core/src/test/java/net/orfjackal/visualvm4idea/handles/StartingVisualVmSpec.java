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
import net.orfjackal.visualvm4idea.core.VisualVmHookRunner;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

/**
 * @author Esko Luontola
 * @since 17.10.2008
 */
@RunWith(JDaveRunner.class)
@Group({"fast"})
public class StartingVisualVmSpec extends Specification<Object> {

    public class WhenVisualVmIsStarted {

        private VisualVmHandle handle;
        private VisualVmHookRunner hook;
        private Thread hookThread;

        public Object create() {
            handle = new VisualVmHandle();
            hook = new VisualVmHookRunner(handle.getPort());
            return null;
        }

        public void destroy() {
            hookThread.interrupt();
            handle.close();
        }

        public void itWillConnectToTheHandle() throws InterruptedException {
            specify(!handle.isConnected());
            startVisualVm();
            handle.awaitConnection(100, TimeUnit.MILLISECONDS);
            specify(handle.isConnected());
        }

        private void startVisualVm() {
            hookThread = new Thread(hook);
            hookThread.setDaemon(true);
            hookThread.start();
        }
    }
}

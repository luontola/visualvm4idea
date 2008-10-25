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

import net.orfjackal.visualvm4idea.comm.MessageClientLauncher;
import net.orfjackal.visualvm4idea.comm.MessageSender;
import net.orfjackal.visualvm4idea.comm.MessageServer;
import net.orfjackal.visualvm4idea.util.ProcessExecutorImpl;

/**
 * @author Esko Luontola
 * @since 17.10.2008
 */
public class VisualVmCommandSender {

    private MessageSender visualvm;

    public VisualVmCommandSender() {
        visualvm = new MessageServer(new MessageClientLauncher() {
            public void launch(int serverPort) {
                startVisualVm(serverPort);
            }
        });
    }

    public void beginProfilingApplication(int port) {
        // TODO: class loader problems, the class loader which is used on visualvm to load objects from stream, can not find this class
        // maybe it would be better to just send strings?
    }

    private static void startVisualVm(int serverPort) {
        String agentPath = "D:\\DEVEL\\VisualVM for IDEA\\visualvm4idea\\visualvm4idea-dist\\target\\visualvm4idea\\lib\\visualvm4idea-visualvm-agent.jar";
        String libPath = "D:\\DEVEL\\VisualVM for IDEA\\visualvm4idea\\visualvm4idea-dist\\target\\visualvm4idea\\lib\\visualvm4idea-core.jar";
        new ProcessExecutorImpl()
                .exec("D:\\DEVEL\\VisualVM for IDEA\\visualvm_101\\bin\\visualvm.exe",
                        "-J-javaagent:" + agentPath,
                        "-J-Dvisualvm4idea.lib=" + libPath,
                        "-J-Dvisualvm4idea.port=" + serverPort);
    }

    public static void main(String[] args) {
        new VisualVmCommandSender().beginProfilingApplication(5140);
    }
}

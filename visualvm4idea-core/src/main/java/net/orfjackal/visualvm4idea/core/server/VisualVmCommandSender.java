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

package net.orfjackal.visualvm4idea.core.server;

import net.orfjackal.visualvm4idea.comm.MessageSender;
import net.orfjackal.visualvm4idea.comm.MessageServer;
import net.orfjackal.visualvm4idea.core.commands.Command;
import net.orfjackal.visualvm4idea.core.commands.ProfileAppCommand;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * @author Esko Luontola
 * @since 17.10.2008
 */
public class VisualVmCommandSender {
    private static final Logger log = Logger.getLogger(VisualVmCommandSender.class.getName());

    private MessageSender visualvm;

    public VisualVmCommandSender() {
        visualvm = new MessageServer(new VisualVmLauncher());
    }

    public void beginProfilingApplication(int port) {
        runCommand(new ProfileAppCommand(port));
    }

    private String[] runCommand(Command command) {
        String[] message = command.toMessage();
        try {
            log.info("Sent request " + Arrays.toString(message));
            String[] response = visualvm.send(message).get(10, TimeUnit.SECONDS);
            log.info("Got response " + Arrays.toString(response) + " to request " + Arrays.toString(message));
            return response;

        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while waiting for response, request was: " + Arrays.toString(message), e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Remote exception on VisualVM, request was: " + Arrays.toString(message), e);
        } catch (TimeoutException e) {
            throw new RuntimeException("No response from VisualVM, request was: " + Arrays.toString(message), e);
        }
    }

    public static void main(String[] args) {
        VisualVmCommandSender sender = new VisualVmCommandSender();
        sender.beginProfilingApplication(5140);
    }
}

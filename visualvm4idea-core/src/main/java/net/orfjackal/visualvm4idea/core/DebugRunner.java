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

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.application.jvm.Jvm;
import com.sun.tools.visualvm.application.jvm.JvmFactory;
import com.sun.tools.visualvm.core.datasource.DataSourceRepository;
import net.orfjackal.visualvm4idea.visualvm.ProfilerSupportWrapper;

import java.util.Set;

/**
 * @author Esko Luontola
 * @since 10.10.2008
 */
public class DebugRunner implements Runnable {

    // TODO: remove debug code

    public void run() {
        while (true) {
            try {
                printDebugInfo();
            } catch (Throwable t) {
                t.printStackTrace(System.out);
                return;
            }
            sleep(5000);
        }
    }

    private void printDebugInfo() {
        System.out.println("---");

        Set<Application> applications = DataSourceRepository.sharedInstance().getDataSources(Application.class);
        for (Application app : applications) {

            System.out.println("app = " + app);
            System.out.println("app.getHost() = " + app.getHost());
            System.out.println("app.getId() = " + app.getId());
            System.out.println("app.getMaster() = " + app.getMaster());
            System.out.println("app.getOwner() = " + app.getOwner());
            System.out.println("app.getPid() = " + app.getPid());
            System.out.println("app.getRepository() = " + app.getRepository());
            System.out.println("app.getState() = " + app.getState());
            System.out.println("app.getStorage() = " + app.getStorage());
            System.out.println("app.isLocalApplication() = " + app.isLocalApplication());
            System.out.println("app.isRemoved() = " + app.isRemoved());
            System.out.println("app.isVisible() = " + app.isVisible());
            System.out.println("app.supportsUserRemove() = " + app.supportsUserRemove());

            Jvm jvm = JvmFactory.getJVMFor(app);
            System.out.println("jvm.getCommandLine() = " + jvm.getCommandLine());
            System.out.println("jvm.getJavaHome() = " + jvm.getJavaHome());
            System.out.println("jvm.getJvmArgs() = " + jvm.getJvmArgs());
            System.out.println("jvm.getJvmFlags() = " + jvm.getJvmFlags());
            System.out.println("jvm.getMainArgs() = " + jvm.getMainArgs());
            System.out.println("jvm.getMainClass() = " + jvm.getMainClass());
            System.out.println("jvm.getVmInfo() = " + jvm.getVmInfo());
            System.out.println("jvm.getVmName() = " + jvm.getVmName());
            System.out.println("jvm.getVmVendor() = " + jvm.getVmVendor());
            System.out.println("jvm.getVmVersion() = " + jvm.getVmVersion());

            ProfilerSupportWrapper.selectProfilerView(app);
            sleep(1000);
        }
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}

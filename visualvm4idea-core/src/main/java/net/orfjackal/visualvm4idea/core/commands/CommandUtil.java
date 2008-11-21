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

package net.orfjackal.visualvm4idea.core.commands;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.application.jvm.Jvm;
import com.sun.tools.visualvm.application.jvm.JvmFactory;
import com.sun.tools.visualvm.core.datasource.DataSourceRepository;
import org.netbeans.lib.profiler.common.AttachSettings;

import java.util.Set;

/**
 * @author Esko Luontola
 * @since 12.11.2008
 */
public class CommandUtil {

    private CommandUtil() {
    }

    public static Application getProfiledApplication(int appUniqueId) {
        Application app;
        do {
            sleep(500);
            app = findProfiledApp(appUniqueId);
        } while (app == null);
        return app;
    }

    private static Application findProfiledApp(int appUniqueId) {
        Set<Application> apps = DataSourceRepository.sharedInstance().getDataSources(Application.class);
        for (Application app : apps) {
            Jvm jvm = JvmFactory.getJVMFor(app);
            if (jvm.getJvmArgs().contains(getAppUniqueIdCommand(appUniqueId))) {
                return app;
            }
        }
        return null;
    }

    public static String getAppUniqueIdCommand(int appUniqueId) {
        return "-Dvisualvm4idea.appUniqueId=" + appUniqueId;
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    static AttachSettings getAttachSettings(int profilerPort) {
        // com.sun.tools.visualvm.profiler.ApplicationProfilerView.MasterViewSupport.MasterViewSupport()
        // com.sun.tools.visualvm.profiler.ApplicationProfilerView.MasterViewSupport.initSettings()
        final AttachSettings attachSettings = new AttachSettings();
        attachSettings.setDirect(true);
        attachSettings.setHost("localhost");
        attachSettings.setPort(profilerPort);
        return attachSettings;
    }
}

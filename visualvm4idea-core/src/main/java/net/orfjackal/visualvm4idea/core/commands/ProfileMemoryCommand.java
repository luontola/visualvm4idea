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
import com.sun.tools.visualvm.core.datasource.Storage;
import com.sun.tools.visualvm.profiler.MemorySettingsSupport;
import net.orfjackal.visualvm4idea.util.Reflect;
import net.orfjackal.visualvm4idea.visualvm.MemorySettings;
import net.orfjackal.visualvm4idea.visualvm.ProfilerSupportWrapper;
import org.netbeans.modules.profiler.NetBeansProfiler;
import org.netbeans.modules.profiler.utils.IDEUtils;

/**
 * @author Esko Luontola
 * @since 12.11.2008
 */
public class ProfileMemoryCommand implements Command {

    public int profilerPort;
    public MemorySettings.AllocMode allocMode;
    public int allocInterval;
    public boolean recordAllocTraces;

    public String getCommandId() {
        return "PROFILE_MEMORY";
    }

    public String[] toMessage() {
        return new String[]{
                getCommandId(),
                String.valueOf(profilerPort),
                allocMode.name(),
                String.valueOf(allocInterval),
                String.valueOf(recordAllocTraces),
        };
    }

    public Command fromMessage(String[] message) {
        int i = 0;
        ProfileMemoryCommand cmd = new ProfileMemoryCommand();
        cmd.profilerPort = Integer.parseInt(message[++i]);
        cmd.allocMode = MemorySettings.AllocMode.valueOf(message[++i]);
        cmd.allocInterval = Integer.parseInt(message[++i]);
        cmd.recordAllocTraces = Boolean.parseBoolean(message[++i]);
        return cmd;
    }

    public String[] call() {
        // com.sun.tools.visualvm.profiler.ApplicationProfilerView.MasterViewSupport.handleMemoryProfiling()
        IDEUtils.runInProfilerRequestProcessor(new Runnable() {
            public void run() {
                NetBeansProfiler.getDefaultNB().attachToApp(
                        getMemorySettings().toProfilingSettings(),
                        CommandUtil.getAttachSettings(profilerPort));
                Application app = CommandUtil.getProfiledApplication();
                copySettingsToUserInterface(app);
                ProfilerSupportWrapper.setProfiledApplication(app);
                ProfilerSupportWrapper.selectProfilerView(app);
            }
        });
        return OK_RESPONSE;
    }

    private void copySettingsToUserInterface(Application app) {
        // com.sun.tools.visualvm.profiler.MemorySettingsSupport.saveSettings()
        final String SNAPSHOT_VERSION = (String) Reflect.on(MemorySettingsSupport.class)
                .field("SNAPSHOT_VERSION").get().value();
        final String CURRENT_SNAPSHOT_VERSION = (String) Reflect.on(MemorySettingsSupport.class)
                .field("CURRENT_SNAPSHOT_VERSION").get().value();

        Storage storage = app.getStorage();
        storage.setCustomProperty(SNAPSHOT_VERSION, CURRENT_SNAPSHOT_VERSION);
        storage.setCustomProperty(MemorySettingsSupport.PROP_MODE, Integer.toString(allocMode.getType()));
        storage.setCustomProperty(MemorySettingsSupport.PROP_STACKTRACES, Integer.toString(recordAllocTraces ? -1 : 0));
        storage.setCustomProperty(MemorySettingsSupport.PROP_TRACK_EVERY, Integer.toString(allocInterval));
    }

    private MemorySettings getMemorySettings() {
        MemorySettings memorySettings = new MemorySettings();
        memorySettings.allocMode = allocMode;
        memorySettings.allocInterval = allocInterval;
        memorySettings.recordAllocTraces = recordAllocTraces;
        return memorySettings;
    }
}

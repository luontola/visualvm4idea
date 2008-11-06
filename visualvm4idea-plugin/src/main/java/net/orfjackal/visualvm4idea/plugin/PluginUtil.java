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

package net.orfjackal.visualvm4idea.plugin;

import com.intellij.openapi.application.ApplicationManager;
import net.orfjackal.visualvm4idea.plugin.server.VisualVmCommandSender;
import net.orfjackal.visualvm4idea.util.FileUtil;

/**
 * @author Esko Luontola
 * @since 6.11.2008
 */
public class PluginUtil {

    private PluginUtil() {
    }

    public static PluginSettingsComponent getPluginSettings() {
        return ApplicationManager.getApplication().getComponent(PluginSettingsComponent.class);
    }

    public static String getVisualVmExecutable() {
        String visualvmHome = getPluginSettings().getVisualvmHome();
        return FileUtil.getFile(visualvmHome, "bin", "visualvm.exe").getAbsolutePath();
    }

    public static String getVisualVmAgentCommand() {
        String visualvmHome = getPluginSettings().getVisualvmHome();
        String libDir = FileUtil.getFile(visualvmHome, "profiler2", "lib").getAbsolutePath();
        String agent = FileUtil.getFile(libDir, "deployed", "jdk16", "windows", "profilerinterface.dll").getAbsolutePath();
        return "-agentpath:" + agent + "=" + libDir + "," + VisualVmCommandSender.PROFILER_PORT;
    }
}
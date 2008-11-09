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

package net.orfjackal.visualvm4idea.plugin.config;

import net.orfjackal.visualvm4idea.plugin.PluginSettingsComponent;
import net.orfjackal.visualvm4idea.util.FileUtil;

import java.io.File;

/**
 * @author Esko Luontola
 * @since 9.11.2008
 */
public abstract class AbstractVisualVmConfig implements VisualVmConfig, SystemVars {

    private final String visualVmHome;
    private final SystemVars systemVars;

    public AbstractVisualVmConfig(String visualVmHome, SystemVars systemVars) {
        this.visualVmHome = visualVmHome;
        this.systemVars = systemVars;
    }

    public String getVisualVmHome() {
        return visualVmHome;
    }

    public String getSystemArch() {
        return systemVars.getSystemArch();
    }

    public String getProfilerInterfaceName() {
        return systemVars.getProfilerInterfaceName();
    }

    public String getVisualVmExecutableName() {
        return systemVars.getVisualVmExecutableName();
    }

    public boolean isValid() {
        return getVisualVmHome() != null && getVisualVmHome().length() > 0
                && new File(getVisualVmHome()).isDirectory()
                && new File(getVisualVmExecutable()).isFile()
                && new File(getAppProfilerLib()).isDirectory();
    }

    public String getVisualVmHookAgent() {
        String pluginHome = PluginSettingsComponent.getInstance().getPluginHome();
        return FileUtil.getFile(pluginHome, "lib", "visualvm4idea-visualvm-agent.jar").getAbsolutePath();
    }

    public String getVisualVmHookLib() {
        String pluginHome = PluginSettingsComponent.getInstance().getPluginHome();
        return FileUtil.getFile(pluginHome, "lib", "visualvm4idea-core.jar").getAbsolutePath();
    }
}

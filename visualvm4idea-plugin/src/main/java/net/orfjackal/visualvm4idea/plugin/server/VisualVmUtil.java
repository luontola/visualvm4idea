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

package net.orfjackal.visualvm4idea.plugin.server;

import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.projectRoots.*;
import net.orfjackal.visualvm4idea.core.commands.CommandUtil;
import net.orfjackal.visualvm4idea.plugin.*;
import net.orfjackal.visualvm4idea.plugin.config.*;
import net.orfjackal.visualvm4idea.util.FileUtil;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * @author Esko Luontola
 * @since 6.11.2008
 */
public class VisualVmUtil {

    public static final List<SystemVars> SYSTEMS = Arrays.asList(
            new WindowsSystemVars(),
            new Windows64SystemVars(),
            new LinuxSystemVars(),
            new Linux64SystemVars(),
            new MacSystemVars()
    );

    private VisualVmUtil() {
    }

    @NotNull
    public static String getAppUniqueIdCommand(ProfilerSettings settings) {
        return CommandUtil.getAppUniqueIdCommand(settings.getAppUniqueId());
    }

    @NotNull
    public static String getAppProfilerCommand(JdkVersion jdkVersion) {
        VisualVmConfig config = getConfig();
        String agent = config.getAppProfilerAgent(jdkVersion);
        String lib = config.getAppProfilerLib();
        return "-agentpath:" + agent + "=" + lib + "," + VisualVmCommandSender.PROFILER_PORT;
    }

    @NotNull
    public static String getVisualVmExecutable() {
        return getConfig().getVisualVmExecutable();
    }

    @NotNull
    public static String getVisualVmHookAgent() {
        return getConfig().getVisualVmHookAgent();
    }

    @NotNull
    public static String getVisualVmHookLib() {
        return getConfig().getVisualVmHookLib();
    }

    public static void checkCurrentConfig() throws RuntimeConfigurationException {
        String visualVmHome = getVisualVmHome();
        VisualVmConfig config = getValidConfig(visualVmHome, getCurrentSystem());
        if (config == null) {
            throw new RuntimeConfigurationException(
                    "VisualVM could not be found from \"" + visualVmHome + "\"",
                    "VisualVM Plugin not configured");
        }
    }

    @NotNull
    private static VisualVmConfig getConfig() {
        String visualVmHome = getVisualVmHome();
        VisualVmConfig config = getValidConfig(visualVmHome, getCurrentSystem());
        if (config == null) {
            throw new IllegalStateException(
                    "VisualVM Plugin not configured: VisualVM could not be found from \"" + visualVmHome + "\"");
        }
        return config;
    }

    @NotNull
    public static String getVisualVmHome() {
        String configuredHome = PluginSettingsComponent.getInstance().getVisualVmHome();
        List<String> homes = new ArrayList<String>();
        homes.add(configuredHome);
        homes.addAll(getAutodetectedVisualVmHomes());
        for (String home : homes) {
            if (isValidHome(home)) {
                return home;
            }
        }
        return configuredHome;
    }

    @NotNull
    public static List<String> getAutodetectedVisualVmHomes() {
        List<String> autodetected = new ArrayList<String>();
        for (Sdk jdk : ProjectJdkTable.getInstance().getAllJdks()) {
            final String jdkHome = FileUtil.getCanonicalPath(jdk.getHomePath());
            if (VisualVmUtil.isValidHome(jdkHome)) {
                autodetected.add(jdkHome);
            }
        }
        return Collections.unmodifiableList(autodetected);
    }

    public static boolean isValidHome(@NotNull String visualVmHome) {
        return getValidConfig(visualVmHome, getCurrentSystem()) != null;
    }

    @NotNull
    private static SystemVars getCurrentSystem() {
        // TODO: support for 64 bit systems
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return new WindowsSystemVars();
        }
        if (osName.contains("linux")) {
            return new LinuxSystemVars();
        }
        if (osName.contains("mac")) {
            return new MacSystemVars();
        }
        throw new IllegalArgumentException("Unknown OS: " + osName);
    }

    @Nullable
    private static VisualVmConfig getValidConfig(String visualVmHome, SystemVars system) {
        List<? extends VisualVmConfig> allConfigs = Arrays.asList(
                new BundledVisualVm10Config(visualVmHome, system),
                new BundledVisualVm11Config(visualVmHome, system),
                new ExternalVisualVm10Config(visualVmHome, system),
                new ExternalVisualVm11Config(visualVmHome, system)
        );
        for (VisualVmConfig config : allConfigs) {
            if (config.isValid()) {
                return config;
            }
        }
        return null;
    }
}

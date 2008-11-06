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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.JavaProgramRunner;
import com.intellij.execution.runners.RunnerInfo;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.SettingsEditor;
import net.orfjackal.visualvm4idea.plugin.server.VisualVmCommandSender;

/**
 * @author Esko Luontola
 * @since 14.10.2008
 */
public class CpuProfilerRunner implements JavaProgramRunner<ProfilerSettings> {
    private static final Logger log = Logger.getInstance(CpuProfilerRunner.class.getName());

    private final VisualVmCommandSender visualvm;
    private final RunnerInfo runnerInfo = new CpuProfilerRunnerInfo();

    public CpuProfilerRunner(VisualVmCommandSender visualvm) {
        this.visualvm = visualvm;
    }

    // on run: 1
    public void patch(JavaParameters javaParameters, RunnerSettings settings, boolean beforeExecution) throws ExecutionException {
        ProfilerSettings profilerSettings = (ProfilerSettings) settings.getData();
        profilerSettings.configureOnPatch(javaParameters);

        log.info("CpuProfilerRunner.patch");
        log.info("javaParameters = " + javaParameters);
        log.info("settings = " + settings);
        log.info("settings.getData() = " + settings.getData());
        log.info("settings.getRunProfile() = " + settings.getRunProfile());
        log.info("beforeExecution = " + beforeExecution);
        // see: com.intellij.debugger.impl.DebuggerManagerImpl.createDebugParameters()
        // javaParameters.getVMParametersList().replaceOrAppend(...);

        // http://profiler.netbeans.org/docs/help/5.5/attach.html#direct_attach
        javaParameters.getVMParametersList().prepend(PluginUtil.getVisualVmAgentCommand());
    }

    // on run: 2
    public void onProcessStarted(RunnerSettings settings, ExecutionResult executionResult) {
        ProfilerSettings profilerSettings = (ProfilerSettings) settings.getData();
        log.info("CpuProfilerRunner.onProcessStarted");
        log.info("settings = " + settings);
        log.info("executionResult = " + executionResult);

        visualvm.beginProfilingApplication(VisualVmCommandSender.PROFILER_PORT,
                profilerSettings.profileNewRunnables,
                profilerSettings.getClassesToProfileFrom(),
                profilerSettings.getFilterType(),
                profilerSettings.getFilterValue());
    }

    // on run: 3
    public AnAction[] createActions(ExecutionResult executionResult) {
        log.info("CpuProfilerRunner.createActions");
        return new AnAction[0]; // TODO
    }

    public RunnerInfo getInfo() {
        return runnerInfo;
    }

    public ProfilerSettings createConfigurationData(ConfigurationInfoProvider settingsProvider) {
        return new ProfilerSettings();
    }

    public void checkConfiguration(RunnerSettings settings, ConfigurationPerRunnerSettings configurationPerRunnerSettings) throws RuntimeConfigurationException {
        ProfilerSettings profilerSettings = (ProfilerSettings) settings.getData();
        log.info("CpuProfilerRunner.checkConfiguration");
    }

    public SettingsEditor<ProfilerSettings> getSettingsEditor(RunConfiguration configuration) {
        return new ProfilerSettingsEditor();
    }
}

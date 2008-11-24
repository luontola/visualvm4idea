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

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.*;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import net.orfjackal.visualvm4idea.util.Reflect;

/**
 * @author Esko Luontola
 * @since 24.11.2008
 */
public class DefaultJavaProgramRunnerWrapper {

    // HACK: the ungliest copy-paste ever because of violating the Open-Closed Principle, from DefaultJavaProgramRunner
    // - why did IDEA 8 have to mess up the API even more??!

    public static RunContentDescriptor doExecute(JavaPatchableProgramRunner<?> runner, Project project, Executor executor,
                                                 RunProfileState state, RunContentDescriptor contentToReuse,
                                                 ExecutionEnvironment env) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        boolean addJavaActions = true;
        ExecutionResult executionresult;
        if (state instanceof JavaCommandLine) {
            runner.patch(((JavaCommandLine) state).getJavaParameters(), state.getRunnerSettings(), true);
            ProcessProxy processproxy = ProcessProxyFactory.getInstance().createCommandLineProxy((JavaCommandLine) state);
            executionresult = state.execute(executor, runner);
            if (processproxy != null && executionresult != null) {
                processproxy.attach(executionresult.getProcessHandler());
            }
            if ((state instanceof JavaCommandLineState) && !((JavaCommandLineState) state).shouldAddJavaProgramRunnerActions()) {
                addJavaActions = false;
            }
        } else {
            executionresult = state.execute(executor, runner);
        }
        if (executionresult == null) {
            return null;
        }
        runner.onProcessStarted(env.getRunnerSettings(), executionresult);

        RunContentBuilder builder = new RunContentBuilder(project, runner, executor);
        builder.setExecutionResult(executionresult);
        builder.setEnvironment(env);
        if (addJavaActions) {
            addDefaultActions(builder);
        }
        RunContentDescriptor result = builder.showRunContent(contentToReuse);
        AnAction aanaction[] = runner.createActions(builder.getExecutionResult());
        for (AnAction anaction : aanaction) {
            builder.addAction(anaction);
        }
        return result;
    }

    private static void addDefaultActions(RunContentBuilder runcontentbuilder) {
        Reflect.on(DefaultJavaProgramRunner.class)
                .method("addDefaultActions", RunContentBuilder.class).call(runcontentbuilder).value();
    }
}

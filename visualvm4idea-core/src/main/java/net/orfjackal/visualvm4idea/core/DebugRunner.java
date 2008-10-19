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
import com.sun.tools.visualvm.profiler.CPUSettingsSupport;
import com.sun.tools.visualvm.profiler.MemorySettingsSupport;
import com.sun.tools.visualvm.profiler.ProfilerSupport;
import net.orfjackal.visualvm4idea.util.Reflect;
import net.orfjackal.visualvm4idea.visualvm.ProfilerSupportWrapper;
import org.netbeans.lib.profiler.client.ClientUtils;
import org.netbeans.lib.profiler.common.AttachSettings;
import org.netbeans.lib.profiler.common.ProfilingSettings;
import org.netbeans.lib.profiler.common.ProfilingSettingsPresets;
import org.netbeans.lib.profiler.common.filters.SimpleFilter;
import org.netbeans.lib.profiler.global.CommonConstants;
import org.netbeans.modules.profiler.NetBeansProfiler;
import org.netbeans.modules.profiler.utils.IDEUtils;

import java.util.Set;

/**
 * @author Esko Luontola
 * @since 10.10.2008
 */
public class DebugRunner implements Runnable {

    // TODO: remove debug code

    public void run() {
        while (true) {
            System.out.println("--");
            try {
                beginProfiling();
//                printDebugInfo();
            } catch (Throwable t) {
                t.printStackTrace(System.out);
                return;
            }
            sleep(10000);
        }
    }

    private static void beginProfiling() {
        Set<Application> apps = DataSourceRepository.sharedInstance().getDataSources(Application.class);
        for (Application app : apps) {
            System.out.println("app = " + app);
            System.out.println("app.getPid() = " + app.getPid());

            Jvm jvm = JvmFactory.getJVMFor(app);
            if (jvm.getClass().getSimpleName().equals("DefaultJvm")) {
                System.out.println("jvm = " + jvm);

                // com.sun.tools.visualvm.profiler.ApplicationProfilerView.MasterViewSupport.MasterViewSupport()
                // com.sun.tools.visualvm.profiler.ApplicationProfilerView.MasterViewSupport.initSettings()
                final AttachSettings attachSettings = new AttachSettings();
                attachSettings.setDirect(true);
//                attachSettings.setDynamic16(true);
                attachSettings.setPid(app.getPid());
//                attachSettings.setHost("localhost");
//                attachSettings.setPort(5140);
                System.out.println("attachSettings = " + attachSettings);

                // com.sun.tools.visualvm.profiler.ApplicationProfilerView.MasterViewSupport.handleCPUProfiling()
                // com.sun.tools.visualvm.profiler.CPUSettingsSupport.saveSettings()
//                Storage storage = app.getStorage();
//                storage.setCustomProperty(CPUSettingsSupport.SNAPSHOT_VERSION, CURRENT_SNAPSHOT_VERSION);
//                storage.setCustomProperty(CPUSettingsSupport.PROP_ROOT_CLASSES, rootsArea.getTextArea().getText());
//                storage.setCustomProperty(CPUSettingsSupport.PROP_PROFILE_RUNNABLES, Boolean.toString(runnablesCheckBox.isSelected()));
//                storage.setCustomProperty(CPUSettingsSupport.PROP_FILTER_TYPE, Integer.toString(inclFilterRadioButton.isSelected() ?
//                        SimpleFilter.SIMPLE_FILTER_INCLUSIVE : SimpleFilter.SIMPLE_FILTER_EXCLUSIVE));
//                storage.setCustomProperty(CPUSettingsSupport.PROP_FILTER_VALUE, filtersArea.getTextArea().getText());

                // com.sun.tools.visualvm.profiler.ApplicationProfilerView.MasterViewSupport.handleCPUProfiling()
                // com.sun.tools.visualvm.profiler.CPUSettingsSupport.getSettings()
                final ProfilingSettings profilingSettings = ProfilingSettingsPresets.createCPUPreset();
                profilingSettings.setInstrScheme(CommonConstants.INSTRSCHEME_LAZY);
                String instrFilter = "java.*, javax.*, sun.*, sunw.*, com.sun.*";
                profilingSettings.setSelectedInstrumentationFilter(
                        new SimpleFilter(instrFilter, SimpleFilter.SIMPLE_FILTER_EXCLUSIVE, instrFilter)
                );
                profilingSettings.setInstrumentationRootMethods(new ClientUtils.SourceCodeSelection[]{
                        new ClientUtils.SourceCodeSelection("net.orfjackal.**", "*", null)
                });
                profilingSettings.setInstrumentSpawnedThreads(true);
                System.out.println("profilingSettings = " + profilingSettings);

                // com.sun.tools.visualvm.profiler.ApplicationProfilerView.MasterViewSupport.handleCPUProfiling()
                ProfilerSupportWrapper.setProfiledApplication(app);
                IDEUtils.runInProfilerRequestProcessor(new Runnable() {
                    public void run() {
                        NetBeansProfiler.getDefaultNB().attachToApp(profilingSettings, attachSettings);
                    }
                });
                System.out.println("profiling started");

                sleep(10000);
                System.out.println("jvm1 = " + JvmFactory.getJVMFor(app));
                System.out.println("providers = " + Reflect.on(JvmFactory.getDefault()).field("providers").get().value());
                System.out.println("modelCache = " + Reflect.on(JvmFactory.getDefault()).field("modelCache").get().value());
                System.out.println("JvmFactory.clearCache()");
                Reflect.on(JvmFactory.getDefault()).method("clearCache").call();
                System.out.println("providers = " + Reflect.on(JvmFactory.getDefault()).field("providers").get().value());
                System.out.println("modelCache = " + Reflect.on(JvmFactory.getDefault()).field("modelCache").get().value());
                System.out.println("jvm2 = " + JvmFactory.getJVMFor(app));

                throw new RuntimeException("ok");
            }
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

            Object masterViewSupport = Reflect.on(ProfilerSupport.getInstance())
                    .field("profilerViewProvider").get()
                    .method("view", Application.class).call(app)
                    .field("masterViewSupport").get().value();
            System.out.println("masterViewSupport = " + masterViewSupport);

            CPUSettingsSupport cpuSettingsSupport = (CPUSettingsSupport)
                    Reflect.on(masterViewSupport).field("cpuSettingsSupport").get().value();
            MemorySettingsSupport memorySettingsSupport = (MemorySettingsSupport)
                    Reflect.on(masterViewSupport).field("memorySettingsSupport").get().value();
            System.out.println("cpuSettingsSupport = " + cpuSettingsSupport);
            System.out.println("memorySettingsSupport = " + memorySettingsSupport);
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

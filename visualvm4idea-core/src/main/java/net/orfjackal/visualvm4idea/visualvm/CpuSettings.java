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

package net.orfjackal.visualvm4idea.visualvm;

import static net.orfjackal.visualvm4idea.util.StringUtil.splitCommaSeparated;
import org.netbeans.lib.profiler.client.ClientUtils;
import org.netbeans.lib.profiler.common.ProfilingSettings;
import org.netbeans.lib.profiler.common.ProfilingSettingsPresets;
import org.netbeans.lib.profiler.common.filters.SimpleFilter;
import org.netbeans.lib.profiler.global.CommonConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Esko Luontola
 * @since 20.10.2008
 */
public class CpuSettings {

    public static final String DEFAULT_EXCLUDES = "java.*, javax.*, sun.*, sunw.*, com.sun.*";

    public boolean spawnedThreads = true;
    public String roots = "";
    public String includeClasses = "";
    public String excludeClasses = "";

    public ProfilingSettings toProfilingSettings() {
        // com.sun.tools.visualvm.profiler.ApplicationProfilerView.MasterViewSupport.handleCPUProfiling()
        // com.sun.tools.visualvm.profiler.CPUSettingsSupport.getSettings()
        final ProfilingSettings profilingSettings = ProfilingSettingsPresets.createCPUPreset();
        profilingSettings.setInstrScheme(CommonConstants.INSTRSCHEME_LAZY);
        profilingSettings.setInstrumentSpawnedThreads(spawnedThreads);
        profilingSettings.setInstrumentationRootMethods(asSourceCodeSelection(splitCommaSeparated(roots)));
        profilingSettings.setSelectedInstrumentationFilter(getInstrumentationFilter());
        System.out.println("profilingSettings = " + profilingSettings);
        return profilingSettings;
    }

    private SimpleFilter getInstrumentationFilter() {
        if (includeClasses.length() == 0 && excludeClasses.length() == 0) {
            return SimpleFilter.NO_FILTER;
        }
        String filter;
        int filterType;
        if (includeClasses.length() > 0) {
            filter = includeClasses;
            filterType = SimpleFilter.SIMPLE_FILTER_INCLUSIVE;
        } else {
            filter = excludeClasses;
            filterType = SimpleFilter.SIMPLE_FILTER_EXCLUSIVE;
        }
        return new SimpleFilter(filter, filterType, filter);
    }

    private static ClientUtils.SourceCodeSelection[] asSourceCodeSelection(List<String> roots) {
        List<ClientUtils.SourceCodeSelection> selections = new ArrayList<ClientUtils.SourceCodeSelection>();
        for (String root : roots) {
            selections.add(new ClientUtils.SourceCodeSelection(root, "*", null));
        }
        return selections.toArray(new ClientUtils.SourceCodeSelection[selections.size()]);
    }
}

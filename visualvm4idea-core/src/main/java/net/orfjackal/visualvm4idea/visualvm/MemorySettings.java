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

import org.netbeans.lib.profiler.common.ProfilingSettings;
import org.netbeans.lib.profiler.common.ProfilingSettingsPresets;

/**
 * @author Esko Luontola
 * @since 12.11.2008
 */
public class MemorySettings {

    public enum AllocMode {
        ALLOC, ALLOC_AND_GC
    }

    public MemorySettings.AllocMode allocMode = AllocMode.ALLOC_AND_GC;
    public int allocInterval = 10;
    public boolean recordAllocTraces = false;

    public ProfilingSettings toProfilingSettings() {
        // com.sun.tools.visualvm.profiler.ApplicationProfilerView.MasterViewSupport.handleMemoryProfiling()
        // com.sun.tools.visualvm.profiler.MemorySettingsSupport.getSettings()
        ProfilingSettings settings;
        settings = allocMode.equals(AllocMode.ALLOC)
                ? ProfilingSettingsPresets.createMemoryPreset(ProfilingSettings.PROFILE_MEMORY_ALLOCATIONS)
                : ProfilingSettingsPresets.createMemoryPreset(ProfilingSettings.PROFILE_MEMORY_LIVENESS);
        settings.setAllocTrackEvery(allocInterval);
        settings.setAllocStackTraceLimit(recordAllocTraces ? -1 : 0);
        return settings;
    }
}

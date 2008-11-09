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

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

/**
 * @author Esko Luontola
 * @since 6.11.2008
 */
@State(
        name = "VisualVmPlugin",
        storages = {@Storage(
                id = "other",
                file = "$APP_CONFIG$/other.xml"
        )}
)
public class PluginSettingsComponent implements ApplicationComponent, Configurable, PersistentStateComponent<PluginSettings> {
    private static final Logger log = Logger.getInstance(PluginSettingsComponent.class.getName());

    private PluginSettings settings = new PluginSettings();
    private File pluginHome;
    private PluginSettingsEditor editor;

    public static PluginSettingsComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(PluginSettingsComponent.class);
    }

    public String getVisualVmHome() {
        return settings.getVisualVmHome();
    }

    public String getPluginHome() {
        return pluginHome.getAbsolutePath();
    }

    // ApplicationComponent

    @NotNull
    public String getComponentName() {
        return "PluginSettingsComponent";
    }

    public void initComponent() {
        IdeaPluginDescriptor visualvmPlugin = ApplicationManager.getApplication().getPlugin(VisualVmPlugin.PLUGIN_ID);
        pluginHome = visualvmPlugin.getPath();
    }

    public void disposeComponent() {
    }

    // Configurable

    @Nls
    public String getDisplayName() {
        return Messages.message("action.pluginSettings");
    }

    @Nullable
    public Icon getIcon() {
        return Resources.LOGO_32;
    }

    @Nullable
    @NonNls
    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        return getEditor().getRootComponent();
    }

    public boolean isModified() {
        return getEditor().isModified(settings);
    }

    public void apply() throws ConfigurationException {
        getEditor().checkConfig();
        getEditor().exportTo(settings);
    }

    public void reset() {
        getEditor().importFrom(settings);
    }

    private PluginSettingsEditor getEditor() {
        if (editor == null) {
            editor = new PluginSettingsEditor();
        }
        return editor;
    }

    public void disposeUIResources() {
        editor = null;
    }

    // PersistentStateComponent

    public PluginSettings getState() {
        return settings;
    }

    public void loadState(PluginSettings state) {
        this.settings = state;
    }
}

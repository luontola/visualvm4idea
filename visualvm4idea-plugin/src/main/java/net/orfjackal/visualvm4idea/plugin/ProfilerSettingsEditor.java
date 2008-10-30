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

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.util.NewInstanceFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author Esko Luontola
 * @since 30.10.2008
 */
public class ProfilerSettingsEditor extends SettingsEditor<ProfilerSettings> {

    private JPanel rootPane;

    // CPU profiling settings

    private JRadioButton startFromMainClassRadioButton;
    private JRadioButton startFromOtherClassesRadioButton;
    private JTextArea otherClassesToStartFromField;

    private JCheckBox profileNewRunnablesCheckBox;

    private JRadioButton filterIncludeRadioButton;
    private JTextArea filterIncludeField;
    private JRadioButton filterExcludeRadioButton;
    private JTextArea filterExcludeField;

    // Memory profiling settings

    private JRadioButton profileAllocRadioButton;
    private JRadioButton profileAllocAndGcRadioButton;

    private JSpinner profileAllocIntervalSpinner;

    private JCheckBox recordAllocTracesCheckBox;

    public ProfilerSettingsEditor() {
        super(NewInstanceFactory.fromClass(ProfilerSettings.class));
        profileAllocIntervalSpinner.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        Dimension size = profileAllocIntervalSpinner.getPreferredSize();
        size.width = 70;
        profileAllocIntervalSpinner.setPreferredSize(size);
    }

    protected void resetEditorFrom(ProfilerSettings settings) {
        startFromMainClassRadioButton.setSelected(settings.startFromMode == ProfilerSettings.StartFrom.MAIN_CLASS);
        startFromOtherClassesRadioButton.setSelected(settings.startFromMode == ProfilerSettings.StartFrom.OTHER_CLASSES);
        otherClassesToStartFromField.setText(settings.otherClassesToStartFrom);

        profileNewRunnablesCheckBox.setSelected(settings.profileNewRunnables);

        filterIncludeRadioButton.setSelected(settings.filteringMode == ProfilerSettings.FilterMode.INCLUDE);
        filterIncludeField.setText(settings.filterIncludeClasses);
        filterExcludeRadioButton.setSelected(settings.filteringMode == ProfilerSettings.FilterMode.EXCLUDE);
        filterExcludeField.setText(settings.filterExcludeClasses);

        profileAllocRadioButton.setSelected(settings.profileAllocMode == ProfilerSettings.AllocMode.ALLOC);
        profileAllocAndGcRadioButton.setSelected(settings.profileAllocMode == ProfilerSettings.AllocMode.ALLOC_AND_GC);

        profileAllocIntervalSpinner.setValue(settings.profileAllocInterval);

        recordAllocTracesCheckBox.setSelected(settings.recordAllocTraces);
    }

    protected void applyEditorTo(ProfilerSettings settings) throws ConfigurationException {
        settings.startFromMode = startFromMainClassRadioButton.isSelected()
                ? ProfilerSettings.StartFrom.MAIN_CLASS
                : ProfilerSettings.StartFrom.OTHER_CLASSES;
        settings.otherClassesToStartFrom = otherClassesToStartFromField.getText();

        settings.profileNewRunnables = profileNewRunnablesCheckBox.isSelected();

        settings.filteringMode = filterIncludeRadioButton.isSelected()
                ? ProfilerSettings.FilterMode.INCLUDE
                : ProfilerSettings.FilterMode.EXCLUDE;
        settings.filterIncludeClasses = filterIncludeField.getText();
        settings.filterExcludeClasses = filterExcludeField.getText();

        settings.profileAllocMode = profileAllocRadioButton.isSelected()
                ? ProfilerSettings.AllocMode.ALLOC
                : ProfilerSettings.AllocMode.ALLOC_AND_GC;

        settings.profileAllocInterval = (Integer) profileAllocIntervalSpinner.getValue();

        settings.recordAllocTraces = recordAllocTracesCheckBox.isSelected();
    }

    @NotNull
    protected JComponent createEditor() {
        return rootPane;
    }

    protected void disposeEditor() {
    }
}

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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Esko Luontola
 * @since 6.11.2008
 */
public class PluginSettingsEditor {

    private JPanel rootComponent;

    private ButtonGroup visualvmHomeGroup;
    private JPanel autodetectedHomesPanel;

    private JRadioButton customHomeRadioButton;
    private JTextField customHomeField;
    private JButton customHomeBrowse;

    @Nullable private File lastDirectory;

    public PluginSettingsEditor() {
        customHomeBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseForDirectory(customHomeField);
            }
        });

        customHomeRadioButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateComponents();
            }
        });
        updateComponents();
    }

    private void browseForDirectory(@NotNull JTextField target) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);

        if (target.getText().equals("") && lastDirectory != null) {
            chooser.setCurrentDirectory(lastDirectory);
        } else {
            File selectedDir = new File(target.getText());
            chooser.setCurrentDirectory(selectedDir.getParentFile());
            chooser.setSelectedFile(selectedDir);
        }

        int result = chooser.showOpenDialog(rootComponent);
        if (result == JFileChooser.APPROVE_OPTION) {
            target.setText(chooser.getSelectedFile().getAbsolutePath());
        }
        lastDirectory = chooser.getCurrentDirectory();
    }

    private void updateComponents() {
        customHomeField.setEnabled(customHomeRadioButton.isSelected());
        customHomeBrowse.setEnabled(customHomeRadioButton.isSelected());
    }

    public JPanel getRootComponent() {
        return rootComponent;
    }

    public void importFrom(@NotNull PluginSettings in) {
        customHomeRadioButton.setEnabled(true);
        customHomeField.setText(in.getVisualVmHome());
    }

    public void exportTo(@NotNull PluginSettings out) {
        if (customHomeRadioButton.isSelected()) {
            out.setVisualVmHome(customHomeField.getText());
        }
    }

    public boolean isModified(@NotNull PluginSettings prev) {
        return customHomeRadioButton.isSelected()
                && !customHomeField.getText().equals(prev.getVisualVmHome());
    }
}

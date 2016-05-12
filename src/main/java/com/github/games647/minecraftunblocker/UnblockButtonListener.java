package com.github.games647.minecraftunblocker;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class UnblockButtonListener implements ActionListener {

    private static final String UNBLOCK_SUFFIX = "-Unblock";

    private final JComboBox<String> list;
    private final File versionsFolder;
    private final Component parentComp;

    public UnblockButtonListener(JComboBox<String> list, File versionsFolder, Component parentComp) {
        this.list = list;
        this.versionsFolder = versionsFolder;
        this.parentComp = parentComp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String selectedVersion = (String) list.getSelectedItem();
        File sourceFolder = new File(versionsFolder, selectedVersion);
        if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
            JOptionPane.showMessageDialog(parentComp, "Folder not found");
        } else {
            unblock(selectedVersion, sourceFolder);
        }
    }

    private void unblock(String selectedVersion, File sourceFolder) throws HeadlessException {
        File destinationDir = new File(versionsFolder, selectedVersion + UNBLOCK_SUFFIX);
        destinationDir.mkdir();

        Path sourceJsonFile = new File(sourceFolder, selectedVersion + ".json").toPath();
        Path targetJsonFile = new File(destinationDir, selectedVersion + UNBLOCK_SUFFIX + ".json").toPath();

        Path sourceJarFile = new File(sourceFolder, selectedVersion + ".jar").toPath();
        Path targetJarFile = new File(destinationDir, selectedVersion + UNBLOCK_SUFFIX + ".jar").toPath();
        try {
            Files.copy(sourceJarFile, targetJarFile);
            Files.copy(sourceJsonFile, targetJsonFile);

            JSONObject json = (JSONObject) JSONValue.parseWithException(new FileReader(targetJsonFile.toFile()));
            json.put("id", selectedVersion + UNBLOCK_SUFFIX);
            JSONArray libraries = (JSONArray) json.get("libraries");
            removeMojangNetty(libraries);

            Files.write(targetJsonFile, Arrays.asList(json.toJSONString()), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(parentComp, "Sucessfully created an unblocked version. \n"
                    + "Now restart your launcher and select the version with the suffix -Unblock");
        } catch (IOException | ParseException ex) {
            Logger.getLogger(MinecraftUnblocker.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(parentComp, "Error " + ex.getMessage());
        }
    }

    private void removeMojangNetty(JSONArray libraries) {
        for (Object libraryObj : libraries) {
            JSONObject library = (JSONObject) libraryObj;
            String name = (String) library.get("name");
            //found the mojangs custom netty implementation for block the servers
            if (name.startsWith("com.mojang:netty")) {
                libraries.remove(libraryObj);
                break;
            }
        }
    }
}

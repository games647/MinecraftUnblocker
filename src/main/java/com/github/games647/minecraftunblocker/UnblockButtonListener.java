package com.github.games647.minecraftunblocker;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnblockButtonListener implements ActionListener {

    private static final String UNBLOCK_SUFFIX = "-Unblock";

    private final JComboBox<String> list;
    private final Path versionsFolder;
    private final Component parentComp;

    public UnblockButtonListener(JComboBox<String> list, Path versionsFolder, Component parentComp) {
        this.list = list;
        this.versionsFolder = versionsFolder;
        this.parentComp = parentComp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String selectedVersion = (String) list.getSelectedItem();
        Path sourceFolder = versionsFolder.resolve(selectedVersion);
        if (!Files.exists(sourceFolder) || !Files.isDirectory(sourceFolder)) {
            JOptionPane.showMessageDialog(parentComp, "Folder not found");
        } else {
            unblock(selectedVersion, sourceFolder);
        }
    }

    private void unblock(String selectedVersion, Path sourceFolder) throws HeadlessException {
        Path destinationDir = versionsFolder.resolve(selectedVersion + UNBLOCK_SUFFIX);

        Path sourceJsonFile = sourceFolder.resolve(selectedVersion + ".json");
        Path targetJsonFile = destinationDir.resolve(selectedVersion + UNBLOCK_SUFFIX + ".json");

        Path sourceJarFile = sourceFolder.resolve(selectedVersion + ".jar");
        Path targetJarFile = destinationDir.resolve(selectedVersion + UNBLOCK_SUFFIX + ".jar");
        try {
            Files.createDirectory(destinationDir);

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
            //found the Mojang's custom netty implementation for block the servers
            if (name.startsWith("com.mojang:netty")) {
                libraries.remove(libraryObj);
                break;
            }
        }
    }
}

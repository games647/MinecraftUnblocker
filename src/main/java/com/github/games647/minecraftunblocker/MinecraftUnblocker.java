package com.github.games647.minecraftunblocker;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class MinecraftUnblocker {

    public static void main(String[] args) {
        File minecraftFolder = getWorkingDirectory();
        final File versionsFolder = new File(minecraftFolder, "versions/");

        List<String> selectItems = new ArrayList<>();
        selectItems.add("SELECT");
        Stream.of(versionsFolder.list()).forEach((fileName) -> selectItems.add(fileName));
        versionsFolder.listFiles();

        JFrame frame = new JFrame();
        FlowLayout experimentLayout = new FlowLayout();
        frame.setLayout(experimentLayout);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JLabel("Select the version you want to unblock"));
        final JComboBox<String> list = new JComboBox(selectItems.toArray());
        frame.add(list);

        JButton button = new JButton("Unblock");
        frame.add(button);

        frame.pack();
        frame.setVisible(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String selectedVersion = (String) list.getSelectedItem();
                File sourceFolder = new File(versionsFolder, selectedVersion);
                if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
                    JOptionPane.showMessageDialog(frame, "Folder not found");
                } else {
                    File destinationDir = new File(versionsFolder, selectedVersion + "-Unblock");
                    destinationDir.mkdir();

                    Path sourceJsonFile = new File(sourceFolder, selectedVersion + ".json").toPath();
                    Path targetJsonFile = new File(destinationDir, selectedVersion + "-Unblock" + ".json").toPath();

                    Path sourceJarFile = new File(sourceFolder, selectedVersion + ".jar").toPath();
                    Path targetJarFile = new File(destinationDir, selectedVersion + "-Unblock" + ".jar").toPath();
                    try {
                        Files.copy(sourceJarFile, targetJarFile);
                        Files.copy(sourceJsonFile, targetJsonFile);

                        JSONObject json = (JSONObject) JSONValue
                                .parseWithException(new FileReader(targetJsonFile.toFile()));
                        json.put("id", selectedVersion + "-Unblock");
                        JSONArray libraries = (JSONArray) json.get("libraries");
                        for (Object libraryObj : libraries) {
                            JSONObject library = (JSONObject) libraryObj;
                            String name = (String) library.get("name");
                            if (name.startsWith("com.mojang:netty")) {
                                libraries.remove(libraryObj);
                                break;
                            }
                        }

                        Files.write(targetJsonFile, Arrays.asList(json.toJSONString()), StandardCharsets.UTF_8);
                        JOptionPane.showMessageDialog(frame, "Sucessfully created an unblocked version. \n"
                                + "Now restart your launcher and select the version with the suffix -Unblock");
                    } catch (IOException | ParseException ex) {
                        Logger.getLogger(MinecraftUnblocker.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(frame, "Error " + ex.getMessage());
                    }
                }
            }
        });
    }

    public static File getWorkingDirectory() {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        switch (OS.getPlatform()){
            case LINUX:
            case SOLARIS:
                workingDirectory = new File(userHome, ".minecraft/");
                break;
            case WINDOWS:
                String applicationData = System.getenv("APPDATA");
                String folder = applicationData != null ? applicationData : userHome;

                workingDirectory = new File(folder, ".minecraft/");
                break;
            case MACOS:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft");
                break;
            default:
                workingDirectory = new File(userHome, "minecraft/");
        }
        
        return workingDirectory;
    }
}

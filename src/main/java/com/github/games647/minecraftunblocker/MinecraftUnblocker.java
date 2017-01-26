package com.github.games647.minecraftunblocker;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinecraftUnblocker {

    public static void main(String[] args) {
        Path versionsFolder = getMinecraftFolder().resolve("versions");

        List<String> selectItems = new ArrayList<>();
        //show a default item
        selectItems.add("SELECT");

        //add all versions to the list by getting the folders
        try {
            Files.walk(versionsFolder, 1)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .forEach(selectItems::add);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.WARNING, "Cannot find versions sub folders");
            return;
        }

        //start a new window
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //components
        frame.add(new JLabel("Select the version you want to unblock"));
        JComboBox<String> list = new JComboBox(selectItems.toArray());
        frame.add(list);

        JButton button = new JButton("Unblock");
        button.addActionListener(new UnblockButtonListener(list, versionsFolder, frame));
        frame.add(button);

        //finish init
        frame.pack();
        frame.setVisible(true);
    }

    public static Path getMinecraftFolder() {
        String userHome = System.getProperty("user.home", ".");
        Path workingDirectory;
        switch (OS.getPlatform()){
            case LINUX:
            case SOLARIS:
                workingDirectory = Paths.get(userHome, ".minecraft/");
                break;
            case MACOS:
                workingDirectory = Paths.get(userHome, "Library/Application Support/minecraft");
                break;
            case WINDOWS:
                String applicationData = System.getenv("APPDATA");
                String folder = applicationData != null ? applicationData : userHome;

                workingDirectory = Paths.get(folder, ".minecraft/");
                break;
            default:
                workingDirectory = Paths.get(userHome, "minecraft/");
        }

        return workingDirectory;
    }
}

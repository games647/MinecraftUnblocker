package com.github.games647.minecraftunblocker;

import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MinecraftUnblocker {

    public static void main(String[] args) {
        File versionsFolder = new File(getMinecraftFolder(), "versions");

        List<String> selectItems = new ArrayList<>();
        //show a default item
        selectItems.add("SELECT");
        //add all versions to the list by getting the folders
        Stream.of(versionsFolder.list()).forEach((fileName) -> selectItems.add(fileName));

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

    public static File getMinecraftFolder() {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        switch (OS.getPlatform()){
            case LINUX:
            case SOLARIS:
                workingDirectory = new File(userHome, ".minecraft/");
                break;
            case MACOS:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft");
                break;
            case WINDOWS:
                String applicationData = System.getenv("APPDATA");
                String folder = applicationData != null ? applicationData : userHome;

                workingDirectory = new File(folder, ".minecraft/");
                break;
            default:
                workingDirectory = new File(userHome, "minecraft/");
        }

        return workingDirectory;
    }
}

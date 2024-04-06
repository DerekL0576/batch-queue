package com.searesoft;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        if (System.console() == null) {
            String jar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            if (jar.endsWith(".jar")) {
                // JOptionPane.showMessageDialog(null, "Please run this application from the console.");
                new ProcessBuilder("cmd.exe", "/c", "start", "java.exe", "-jar", jar).start();
                return;
            }
        }

        String path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        boolean[] terminated = {false};
        new Thread(() -> {
            while (!terminated[0]) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }

                File folder = new File(path);
                File[] files = folder.listFiles((dir, name) -> name.endsWith(".bat"));
                if (files != null) {
                    for (File file : files) {
                        processBatch(file);
                        break;
                    }
                }
            }
            System.out.println("Application terminated");
        }).start();

        System.out.println("Batch Queue v1.0.1");

        System.out.println("Watching folder: " + path);
        System.out.println("Enter help (h) for help");
        System.out.print("cmd->");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (!terminated[0]) {
            try {
                String str = reader.readLine();
                switch (str) {
                    case "quit", "q" -> terminated[0] = true;
                    case "scripts", "s" -> {
                        new ProcessBuilder("explorer", path).start();
                        System.out.println("Opened explorer to folder: " + path);
                        System.out.print("cmd->");
                    }
                    case "help", "h" -> {
                        int align = 20;
                        System.out.println("________\nAvailable commands\n________");
                        System.out.println(middlePad("help (h)", "Show this help", align));
                        System.out.println(middlePad("scripts (s)", "Browse scripts folder", align));
                        System.out.println(middlePad("quit (q)", "Exit the server application", align));
                        System.out.print("________\ncmd->");
                    }
                    default -> System.out.print("cmd->");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Waiting for active batch to complete");
    }

    private static void processBatch(File file) {
        try {
            output("Processing: " + file.getAbsoluteFile());
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "/wait", "cmd", "/c", "\"" + file.getAbsoluteFile() + "\"");
            Process process = pb.start();
            int exitCode = process.waitFor();
            output("Exit code: " + exitCode);
        } catch (Exception ex) {
            output(ex.getMessage());
        } finally {
            file.delete();
            output("Deleted: " + file.getAbsoluteFile());
        }
    }

    private static String middlePad(String str1, String str2, int column) {
        int count = column - str1.length();
        if (count < 1) return str1 + str2;
        return str1 + " ".repeat(count) + str2;
    }

    private static void output(String str) {
        System.out.println("\r" + str);
        System.out.print("cmd->");
    }
}
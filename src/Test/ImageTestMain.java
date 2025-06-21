package Test;

import java.io.File;

public class ImageTestMain {
    public static void main(String[] args) {
        System.out.println("=== Testing Image Path Resolution ===");
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        
        // Test loading an image from user_data
        String samplePath = "/user_data/images/602e4edc-f79f-45f9-a1b2-2f164b864f43_Python.png";
        String projectRoot = System.getProperty("user.dir");
        String relativePath = samplePath.substring(1); // Remove leading slash
        String filePath = projectRoot + File.separator + relativePath.replace("/", File.separator);
        
        System.out.println("Sample image path: " + samplePath);
        System.out.println("Resolved file path: " + filePath);
        
        File testFile = new File(filePath);
        System.out.println("File exists: " + testFile.exists());
        System.out.println("File absolute path: " + testFile.getAbsolutePath());
        System.out.println("File size: " + (testFile.exists() ? testFile.length() + " bytes" : "N/A"));
        
        // List all files in user_data/images
        File imageDir = new File(projectRoot + File.separator + "user_data" + File.separator + "images");
        if (imageDir.exists() && imageDir.isDirectory()) {
            System.out.println("\nFiles in user_data/images:");
            File[] files = imageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    System.out.println("  - " + file.getName() + " (" + file.length() + " bytes)");
                }
            }
        } else {
            System.out.println("user_data/images directory not found!");
        }
        
        System.out.println("=========================");
    }
}

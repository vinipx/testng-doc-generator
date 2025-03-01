package io.vinipx.testngdoc;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class to run the TestNGDocGenerator
 */
public class RunDocGenerator {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar testng-doc-generator.jar <source-directory> [<additional-source-directory>...] [--package <package-name>...]");
            System.out.println("Options:");
            System.out.println("  --package <package-name>  Specify a package to scan for TestNG classes");
            System.exit(1);
        }
        
        try {
            TestNGDocGenerator generator = new TestNGDocGenerator()
                .useDarkMode(true)
                .displayTagsChart()
                .setReportTitle("TestNG Documentation v1.2.2")
                .setReportHeader("Generated on " + java.time.LocalDate.now() + " with custom settings");
            
            // Parse arguments to separate source directories and packages
            List<String> sourceDirectories = new ArrayList<>();
            List<String> packages = new ArrayList<>();
            
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--package") && i + 1 < args.length) {
                    packages.add(args[++i]);
                } else {
                    sourceDirectories.add(args[i]);
                }
            }
            
            // Convert lists to arrays
            String[] sourceDirectoriesArray = sourceDirectories.toArray(new String[0]);
            String[] packagesArray = packages.toArray(new String[0]);
            
            if (packages.isEmpty()) {
                // No packages specified, use only source directories
                if (sourceDirectories.size() == 1) {
                    // Single source directory mode
                    String sourceDirectory = sourceDirectories.get(0);
                    System.out.println("Generating documentation from: " + sourceDirectory);
                    generator.generateDocumentationFromSource(sourceDirectory);
                } else {
                    // Multiple source directories mode
                    System.out.println("Generating documentation from multiple source directories:");
                    for (String dir : sourceDirectories) {
                        System.out.println(" - " + dir);
                    }
                    generator.generateDocumentationFromMultipleSources(sourceDirectoriesArray);
                }
            } else {
                // Packages specified, use both source directories and packages
                System.out.println("Generating documentation from source directories and packages:");
                
                if (!sourceDirectories.isEmpty()) {
                    System.out.println("Source directories:");
                    for (String dir : sourceDirectories) {
                        System.out.println(" - " + dir);
                    }
                }
                
                System.out.println("Packages:");
                for (String pkg : packages) {
                    System.out.println(" - " + pkg);
                }
                
                generator.generateDocumentationFromSourcesAndPackages(sourceDirectoriesArray, packagesArray);
            }
            
            System.out.println("Documentation generated successfully!");
        } catch (Exception e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

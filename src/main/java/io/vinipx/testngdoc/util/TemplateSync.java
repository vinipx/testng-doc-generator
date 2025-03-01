package io.vinipx.testngdoc.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for synchronizing templates between the library and projects using it.
 */
public class TemplateSync {
    
    private static final String TEMPLATE_DIR = "templates";
    
    /**
     * Synchronizes templates from the library to a project's template directory.
     * This ensures that the project's templates are up-to-date with the library's templates.
     * 
     * @param targetDir The target directory to synchronize templates to
     * @return True if templates were successfully synchronized
     */
    public static boolean syncTemplates(String targetDir) {
        try {
            Path targetPath = Paths.get(targetDir, TEMPLATE_DIR);
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }
            
            // Copy index.ftl template
            boolean indexCopied = copyTemplateFromResource("index.ftl", targetPath);
            
            // Copy class.ftl template
            boolean classCopied = copyTemplateFromResource("class.ftl", targetPath);
            
            return indexCopied && classCopied;
        } catch (IOException e) {
            System.err.println("Failed to synchronize templates: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Copies a template from the library's resources to a target directory.
     * 
     * @param templateName The name of the template to copy
     * @param targetDir The target directory to copy the template to
     * @return True if the template was successfully copied
     * @throws IOException If an I/O error occurs
     */
    private static boolean copyTemplateFromResource(String templateName, Path targetDir) throws IOException {
        // Try to load the template from the classpath
        InputStream templateStream = TemplateSync.class.getClassLoader().getResourceAsStream("templates/" + templateName);
        
        if (templateStream != null) {
            // Copy the template to the target directory
            Files.copy(templateStream, targetDir.resolve(templateName), StandardCopyOption.REPLACE_EXISTING);
            templateStream.close();
            return true;
        } else {
            System.err.println("Template not found in resources: " + templateName);
            return false;
        }
    }
    
    /**
     * Checks if the project's templates are synchronized with the library's templates.
     * 
     * @param projectDir The project directory to check
     * @return True if templates are synchronized
     */
    public static boolean areTemplatesSynchronized(String projectDir) {
        try {
            Path projectTemplatePath = Paths.get(projectDir, TEMPLATE_DIR);
            if (!Files.exists(projectTemplatePath)) {
                return false;
            }
            
            // Check if index.ftl and class.ftl exist
            boolean indexExists = Files.exists(projectTemplatePath.resolve("index.ftl"));
            boolean classExists = Files.exists(projectTemplatePath.resolve("class.ftl"));
            
            return indexExists && classExists;
        } catch (Exception e) {
            System.err.println("Failed to check template synchronization: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Creates a backup of the project's templates before synchronization.
     * 
     * @param projectDir The project directory
     * @return True if backup was successful
     */
    public static boolean backupTemplates(String projectDir) {
        try {
            Path projectTemplatePath = Paths.get(projectDir, TEMPLATE_DIR);
            if (!Files.exists(projectTemplatePath)) {
                return true; // Nothing to backup
            }
            
            // Create backup directory
            Path backupDir = Paths.get(projectDir, TEMPLATE_DIR + "_backup");
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }
            
            // Copy templates to backup directory
            Files.list(projectTemplatePath)
                .filter(path -> path.toString().endsWith(".ftl"))
                .forEach(path -> {
                    try {
                        Files.copy(path, backupDir.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.err.println("Failed to backup template: " + path.getFileName());
                    }
                });
            
            return true;
        } catch (IOException e) {
            System.err.println("Failed to backup templates: " + e.getMessage());
            return false;
        }
    }
}

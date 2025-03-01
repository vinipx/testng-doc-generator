package com.testngdoc.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;
import java.io.IOException;
import freemarker.template.TemplateException;

/**
 * Demo class that shows how to use all features of the TestNG Documentation Generator
 */
public class DemoAllFeatures {
    public static void main(String[] args) {
        try {
            // Create a new instance of the generator
            TestNGDocGenerator generator = new TestNGDocGenerator();
            
            // --- FEATURE: Dark Mode ---
            // Enable dark mode for better readability in low-light environments
            generator.useDarkMode();
            // Alternative: Conditionally enable dark mode
            // generator.useDarkMode(isDarkModePreferred());
            
            // --- FEATURE: Tag Statistics Chart ---
            // Enable the pie chart showing distribution of tags
            generator.displayTagsChart();
            // Alternative: Conditionally enable tag chart
            // generator.displayTagsChart(shouldShowCharts());
            
            // --- FEATURE: Custom Report Title and Header ---
            // Set a custom title for the documentation
            generator.setReportTitle("Project X Test Documentation");
            // Add a subtitle or generation timestamp
            generator.setReportHeader("Generated on " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            
            // Generate documentation
            // Note how all configuration methods can be chained together
            // generator.useDarkMode()
            //         .displayTagsChart()
            //         .setReportTitle("Project X Test Documentation")
            //         .setReportHeader("Generated: " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()))
            //         .generateDocumentation("src/test/java/com/testngdoc/sample");
            
            // Target your test directory
            generator.generateDocumentation("src/test/java/com/testngdoc/sample");
            
            System.out.println("Documentation generated successfully!");
        } catch (IOException | TemplateException e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Helper method examples (not used in this demo)
    private static boolean isDarkModePreferred() {
        // Could check system preferences, time of day, or user settings
        return true;
    }
    
    private static boolean shouldShowCharts() {
        // Could check configuration settings
        return true;
    }
}

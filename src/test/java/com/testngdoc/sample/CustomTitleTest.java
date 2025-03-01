package com.testngdoc.sample;

import io.vinipx.testngdoc.TestNGDocGenerator;
import java.io.IOException;
import freemarker.template.TemplateException;

/**
 * Test class to verify that custom title and header are correctly applied
 * when using TestNGDocGenerator directly.
 */
public class CustomTitleTest {
    
    public static void main(String[] args) {
        try {
            // Create TestNGDocGenerator with custom title and header
            TestNGDocGenerator generator = new TestNGDocGenerator()
                .useDarkMode(true)
                .displayTagsChart()
                .setReportTitle("My Custom Title")
                .setReportHeader("My Custom Header - " + java.time.LocalDate.now());
            
            // Generate documentation
            generator.generateDocumentationFromSource("src/test/java");
            
            System.out.println("Documentation generated successfully with custom title and header!");
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}

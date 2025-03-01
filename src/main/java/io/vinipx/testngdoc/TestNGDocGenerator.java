package io.vinipx.testngdoc;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.vinipx.testngdoc.annotations.Docs;
import io.vinipx.testngdoc.util.TemplateSync;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class TestNGDocGenerator {

    private static String OUTPUT_DIR = "testng-docs";
    private static final String TEMPLATE_DIR = "templates";
    // Map for custom pattern replacements
    private Map<String, String> patternReplacements = new HashMap<>();
    private boolean displayTagsChart = false;
    private boolean darkMode = false;
    private String reportTitle = "TestNG Documentation";
    private String reportHeader = null;
    // Method filtering options
    private List<String> includeMethodPatterns = new ArrayList<>();
    private List<String> excludeMethodPatterns = new ArrayList<>();
    private List<String> includeTagPatterns = new ArrayList<>();
    private List<String> excludeTagPatterns = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar testng-doc-generator.jar <package-to-scan>");
            System.exit(1);
        }

        String packageToScan = args[0];
        TestNGDocGenerator generator = new TestNGDocGenerator();
        try {
            generator.generateDocumentation(packageToScan);
        } catch (Exception e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generate documentation for all TestNG classes in the specified package
     * 
     * @param packageToScan Package to scan for TestNG classes
     * @throws IOException If an I/O error occurs
     * @throws TemplateException If a template error occurs
     */
    public void generateDocumentation(String packageToScan) throws IOException, TemplateException {
        // Create output directory if it doesn't exist
        createOutputDirectory();
        
        // Initialize FreeMarker
        Configuration cfg = initializeFreemarker();
        
        // Validate templates
        validateTemplates(cfg);
        
        // Scan for TestNG classes
        List<TestClassInfo> testClasses = scanForTestClasses(packageToScan);
        
        // Generate documentation
        generateClassDocumentation(testClasses, cfg);
        generateIndexPage(testClasses, cfg);
        
        System.out.println("Documentation generated in: " + OUTPUT_DIR);
    }
    
    /**
     * Generate documentation for a specific test class with a custom output directory
     * @param testClasses Array of test classes to document
     * @param outputDir Custom output directory
     */
    public void generateDocumentation(Class<?>[] testClasses, String outputDir) {
        try {
            // Set output directory
            OUTPUT_DIR = outputDir;
            
            // Create output directory if it doesn't exist
            createOutputDirectory();
            
            // Initialize FreeMarker
            Configuration cfg = initializeFreemarker();
            
            List<TestClassInfo> classInfos = new ArrayList<>();
            
            for (Class<?> testClass : testClasses) {
                // Create test class info
                List<TestMethodInfo> testMethods = new ArrayList<>();
                
                // Check each method for @Test annotation
                for (Method method : testClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Test.class)) {
                        String methodDescription = extractMethodLogic(testClass, method.getName());
                        TestMethodInfo methodInfo = new TestMethodInfo(method.getName(), methodDescription);
                        extractTagsFromMethod(method, methodInfo);
                        testMethods.add(methodInfo);
                    }
                }
                
                if (testMethods.isEmpty()) {
                    System.out.println("No test methods found in class: " + testClass.getName());
                    continue;
                }
                
                TestClassInfo classInfo = new TestClassInfo(
                        testClass.getSimpleName(),
                        testClass.getPackage().getName(),
                        testMethods
                );
                classInfo.setPercentage("100.0");
                
                classInfos.add(classInfo);
            }
            
            // Generate documentation
            generateClassDocumentation(classInfos, cfg);
            generateIndexPage(classInfos, cfg);
            
            System.out.println("Documentation generated in: " + OUTPUT_DIR);
            
        } catch (Exception e) {
            System.err.println("Error generating documentation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate documentation for all TestNG classes in the specified source directory
     * 
     * @param sourceDirectory Directory containing Java source files
     * @throws IOException If an I/O error occurs
     * @throws TemplateException If a template error occurs
     */
    public void generateDocumentationFromSource(String sourceDirectory) throws IOException, TemplateException {
        // Create output directory if it doesn't exist
        createOutputDirectory();
        
        // Initialize FreeMarker
        Configuration cfg = initializeFreemarker();
        
        // Scan for TestNG classes
        List<TestClassInfo> testClasses = scanForTestClassesFromSource(sourceDirectory);
        
        // Generate documentation
        generateClassDocumentation(testClasses, cfg);
        generateIndexPage(testClasses, cfg);
        
        System.out.println("Documentation generated in: " + OUTPUT_DIR);
    }

    /**
     * Generate documentation for TestNG test classes in multiple source directories
     * 
     * @param sourceDirectories Array of directories containing Java source files
     * @throws IOException If an I/O error occurs
     * @throws TemplateException If a template error occurs
     */
    public void generateDocumentationFromMultipleSources(String... sourceDirectories) throws IOException, TemplateException {
        // Create output directory if it doesn't exist
        createOutputDirectory();
        
        // Initialize FreeMarker
        Configuration cfg = initializeFreemarker();
        
        // Scan for TestNG classes in all source directories
        List<TestClassInfo> allTestClasses = new ArrayList<>();
        
        for (String sourceDirectory : sourceDirectories) {
            List<TestClassInfo> testClasses = scanForTestClassesFromSource(sourceDirectory);
            allTestClasses.addAll(testClasses);
        }
        
        // Recalculate percentages based on the total number of test methods
        int totalTestMethods = allTestClasses.stream()
                .mapToInt(classInfo -> classInfo.getTestMethods().size())
                .sum();
                
        if (totalTestMethods > 0) {
            for (TestClassInfo classInfo : allTestClasses) {
                double percentage = (double) classInfo.getTestMethods().size() / totalTestMethods * 100;
                classInfo.setPercentage(String.format("%.1f", percentage));
            }
        }
        
        // Generate documentation
        generateClassDocumentation(allTestClasses, cfg);
        generateIndexPage(allTestClasses, cfg);
        
        System.out.println("Documentation generated in: " + OUTPUT_DIR);
    }
    
    /**
     * Generate documentation for TestNG test classes from a combination of source directories and packages
     * 
     * @param sourceDirectories Array of directories containing Java source files
     * @param packages Array of packages to scan for TestNG classes
     * @throws IOException If an I/O error occurs
     * @throws TemplateException If a template error occurs
     */
    public void generateDocumentationFromSourcesAndPackages(String[] sourceDirectories, String[] packages) 
            throws IOException, TemplateException {
        // Create output directory if it doesn't exist
        createOutputDirectory();
        
        // Initialize FreeMarker
        Configuration cfg = initializeFreemarker();
        
        // Scan for TestNG classes in all source directories and packages
        List<TestClassInfo> allTestClasses = new ArrayList<>();
        
        // First, scan source directories
        if (sourceDirectories != null && sourceDirectories.length > 0) {
            for (String sourceDirectory : sourceDirectories) {
                List<TestClassInfo> testClasses = scanForTestClassesFromSource(sourceDirectory);
                allTestClasses.addAll(testClasses);
            }
        }
        
        // Then, scan packages
        if (packages != null && packages.length > 0) {
            for (String packageName : packages) {
                List<TestClassInfo> testClasses = scanForTestClasses(packageName);
                allTestClasses.addAll(testClasses);
            }
        }
        
        // Recalculate percentages based on the total number of test methods
        int totalTestMethods = allTestClasses.stream()
                .mapToInt(classInfo -> classInfo.getTestMethods().size())
                .sum();
                
        if (totalTestMethods > 0) {
            for (TestClassInfo classInfo : allTestClasses) {
                double percentage = (double) classInfo.getTestMethods().size() / totalTestMethods * 100;
                classInfo.setPercentage(String.format("%.1f", percentage));
            }
        }
        
        // Generate documentation
        generateClassDocumentation(allTestClasses, cfg);
        generateIndexPage(allTestClasses, cfg);
        
        System.out.println("Documentation generated in: " + OUTPUT_DIR);
    }

    private void createOutputDirectory() throws IOException {
        Path outputPath = Paths.get(OUTPUT_DIR);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
    }

    private Configuration initializeFreemarker() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        
        // First try to use templates from the classpath resources (highest priority)
        try {
            // Check if templates exist in classpath resources
            InputStream classTemplateStream = getClass().getClassLoader().getResourceAsStream("templates/class.ftl");
            InputStream indexTemplateStream = getClass().getClassLoader().getResourceAsStream("templates/index.ftl");
            
            if (classTemplateStream != null && indexTemplateStream != null) {
                // Templates exist in classpath, use them
                classTemplateStream.close();
                indexTemplateStream.close();
                cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
                System.out.println("Using templates from classpath resources");
                return cfg;
            }
        } catch (Exception e) {
            System.out.println("Could not load templates from classpath: " + e.getMessage());
        }
        
        // If not found in classpath, try to use templates from the file system
        // Check both the output directory's templates folder and the current directory's templates folder
        
        // First check the specified template directory (TEMPLATE_DIR)
        Path templatePath = Paths.get(TEMPLATE_DIR);
        if (Files.exists(templatePath)) {
            // Template directory exists, use it
            cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_DIR));
            System.out.println("Using templates from directory: " + templatePath.toAbsolutePath());
            return cfg;
        }
        
        // Then check for templates in the output directory
        Path outputTemplatePath = Paths.get(OUTPUT_DIR, "templates");
        if (Files.exists(outputTemplatePath)) {
            // Template directory exists in output directory, use it
            cfg.setDirectoryForTemplateLoading(outputTemplatePath.toFile());
            System.out.println("Using templates from output directory: " + outputTemplatePath.toAbsolutePath());
            return cfg;
        }
        
        // If no templates found, create them in both locations
        try {
            // Create templates in the template directory first
            if (!Files.exists(templatePath)) {
                Files.createDirectories(templatePath);
                createDefaultTemplates(templatePath);
                System.out.println("Created default templates in directory: " + templatePath.toAbsolutePath());
            }
            
            // Also create templates in the output directory for backward compatibility
            if (!Files.exists(outputTemplatePath)) {
                Files.createDirectories(outputTemplatePath);
                createDefaultTemplates(outputTemplatePath);
                System.out.println("Created default templates in output directory: " + outputTemplatePath.toAbsolutePath());
            }
            
            // Use the templates from the template directory
            cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_DIR));
        } catch (IOException e) {
            System.out.println("Could not create template directories: " + e.getMessage());
            // Last resort: use classpath resources with default templates
            cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
            System.out.println("Using default templates from classpath resources");
        }
        
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    private void createDefaultTemplates(Path templatePath) throws IOException {
        // Create class template
        String classTemplate = 
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>${className} - ${reportTitle}</title>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <style>\n" +
            "        :root {\n" +
            "            --primary-color: #4a6da7;\n" +
            "            --secondary-color: #304878;\n" +
            "            --accent-color: #5d8fdb;\n" +
            "            --background-color: #f8f9fa;\n" +
            "            --card-bg-color: #ffffff;\n" +
            "            --text-color: #333333;\n" +
            "            --border-color: #e1e4e8;\n" +
            "            --success-color: #28a745;\n" +
            "            --warning-color: #ffc107;\n" +
            "            --error-color: #dc3545;\n" +
            "        }\n" +
            "        * {\n" +
            "            box-sizing: border-box;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "        }\n" +
            "        body {\n" +
            "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
            "            line-height: 1.6;\n" +
            "            color: var(--text-color);\n" +
            "            background-color: var(--background-color);\n" +
            "            padding: 0;\n" +
            "            margin: 0;\n" +
            "        }\n" +
            "        .container {\n" +
            "            max-width: 1200px;\n" +
            "            margin: 0 auto;\n" +
            "            padding: 20px;\n" +
            "        }\n" +
            "        header {\n" +
            "            background-color: var(--primary-color);\n" +
            "            color: white;\n" +
            "            padding: 20px 0;\n" +
            "            margin-bottom: 30px;\n" +
            "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
            "        }\n" +
            "        header .container {\n" +
            "            display: flex;\n" +
            "            justify-content: space-between;\n" +
            "            align-items: center;\n" +
            "        }\n" +
            "        h1 {\n" +
            "            color: white;\n" +
            "            font-size: 2.2rem;\n" +
            "            font-weight: 600;\n" +
            "        }\n" +
            "        h2 {\n" +
            "            color: var(--secondary-color);\n" +
            "            font-size: 1.8rem;\n" +
            "            margin: 25px 0 15px 0;\n" +
            "            padding-bottom: 10px;\n" +
            "            border-bottom: 2px solid var(--accent-color);\n" +
            "        }\n" +
            "        .nav {\n" +
            "            margin-bottom: 20px;\n" +
            "        }\n" +
            "        .nav a {\n" +
            "            display: inline-block;\n" +
            "            padding: 8px 16px;\n" +
            "            background-color: var(--primary-color);\n" +
            "            color: white;\n" +
            "            text-decoration: none;\n" +
            "            border-radius: 4px;\n" +
            "            transition: background-color 0.3s ease;\n" +
            "        }\n" +
            "        .nav a:hover {\n" +
            "            background-color: var(--secondary-color);\n" +
            "        }\n" +
            "        .info-panel {\n" +
            "            background-color: var(--card-bg-color);\n" +
            "            border-radius: 8px;\n" +
            "            padding: 20px;\n" +
            "            margin-bottom: 30px;\n" +
            "            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);\n" +
            "        }\n" +
            "        .info-panel p {\n" +
            "            margin: 10px 0;\n" +
            "            font-size: 1.1rem;\n" +
            "        }\n" +
            "        .info-panel strong {\n" +
            "            color: var(--secondary-color);\n" +
            "        }\n" +
            "        .method {\n" +
            "            background-color: var(--card-bg-color);\n" +
            "            border-radius: 8px;\n" +
            "            padding: 20px;\n" +
            "            margin-bottom: 20px;\n" +
            "            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);\n" +
            "            transition: transform 0.2s ease, box-shadow 0.2s ease;\n" +
            "        }\n" +
            "        .method:hover {\n" +
            "            transform: translateY(-3px);\n" +
            "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\n" +
            "        }\n" +
            "        .method-name {\n" +
            "            font-weight: 600;\n" +
            "            color: var(--primary-color);\n" +
            "            font-size: 1.3rem;\n" +
            "            margin-bottom: 10px;\n" +
            "            padding-bottom: 8px;\n" +
            "            border-bottom: 1px solid var(--border-color);\n" +
            "        }\n" +
            "        .method-description {\n" +
            "            margin-top: 15px;\n" +
            "        }\n" +
            "        pre {\n" +
            "            background-color: #f6f8fa;\n" +
            "            padding: 15px;\n" +
            "            border-radius: 6px;\n" +
            "            overflow-x: auto;\n" +
            "            line-height: 1.5;\n" +
            "            font-family: 'Consolas', 'Monaco', monospace;\n" +
            "            font-size: 0.9rem;\n" +
            "            border: 1px solid #e1e4e8;\n" +
            "        }\n" +
            "        @media (max-width: 768px) {\n" +
            "            .container {\n" +
            "                padding: 15px;\n" +
            "            }\n" +
            "            h1 {\n" +
            "                font-size: 1.8rem;\n" +
            "            }\n" +
            "            h2 {\n" +
            "                font-size: 1.5rem;\n" +
            "            }\n" +
            "            .method {\n" +
            "                padding: 15px;\n" +
            "            }\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <header>\n" +
            "        <div class=\"container\">\n" +
            "            <h1>${reportTitle}</h1>\n" +
            "            <#if reportHeader??>\n" +
            "                <h2>${reportHeader}</h2>\n" +
            "            </#if>\n" +
            "            <div class=\"nav\">\n" +
            "                <a href=\"index.html\">Back to Index</a>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </header>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"info-panel\">\n" +
            "            <p><strong>Package:</strong> ${packageName}</p>\n" +
            "            <p><strong>Number of Test Methods:</strong> ${testMethods?size}</p>\n" +
            "            <p><strong>Percentage of Total:</strong> ${percentage}%</p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <h2>Test Methods</h2>\n" +
            "        <#list testMethods as method>\n" +
            "        <div class=\"method\">\n" +
            "            <div class=\"method-name\">${method.name}</div>\n" +
            "            <div class=\"method-description\">\n" +
            "                <pre>${method.description}</pre>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        </#list>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
        
        Files.write(templatePath.resolve("class.ftl"), classTemplate.getBytes());
        
        // Create index template
        String indexTemplate = 
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>${reportTitle}</title>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <style>\n" +
            "        :root {\n" +
            "            --primary-color: #4a6da7;\n" +
            "            --secondary-color: #304878;\n" +
            "            --accent-color: #5d8fdb;\n" +
            "            --background-color: #f8f9fa;\n" +
            "            --card-bg-color: #ffffff;\n" +
            "            --text-color: #333333;\n" +
            "            --border-color: #e1e4e8;\n" +
            "            --success-color: #28a745;\n" +
            "            --warning-color: #ffc107;\n" +
            "            --error-color: #dc3545;\n" +
            "        }\n" +
            "        * {\n" +
            "            box-sizing: border-box;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "        }\n" +
            "        body {\n" +
            "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
            "            line-height: 1.6;\n" +
            "            color: var(--text-color);\n" +
            "            background-color: var(--background-color);\n" +
            "            padding: 0;\n" +
            "            margin: 0;\n" +
            "        }\n" +
            "        .container {\n" +
            "            max-width: 1200px;\n" +
            "            margin: 0 auto;\n" +
            "            padding: 20px;\n" +
            "        }\n" +
            "        header {\n" +
            "            background-color: var(--primary-color);\n" +
            "            color: white;\n" +
            "            padding: 20px 0;\n" +
            "            margin-bottom: 30px;\n" +
            "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
            "        }\n" +
            "        header .container {\n" +
            "            display: flex;\n" +
            "            justify-content: space-between;\n" +
            "            align-items: center;\n" +
            "        }\n" +
            "        h1 {\n" +
            "            color: white;\n" +
            "            font-size: 2.2rem;\n" +
            "            font-weight: 600;\n" +
            "        }\n" +
            "        h2 {\n" +
            "            color: var(--secondary-color);\n" +
            "            font-size: 1.8rem;\n" +
            "            margin: 25px 0 15px 0;\n" +
            "            padding-bottom: 10px;\n" +
            "            border-bottom: 2px solid var(--accent-color);\n" +
            "        }\n" +
            "        .summary {\n" +
            "            background-color: var(--card-bg-color);\n" +
            "            border-radius: 8px;\n" +
            "            padding: 20px;\n" +
            "            margin-bottom: 30px;\n" +
            "            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);\n" +
            "        }\n" +
            "        .summary p {\n" +
            "            margin: 10px 0;\n" +
            "            font-size: 1.1rem;\n" +
            "        }\n" +
            "        .summary strong {\n" +
            "            color: var(--secondary-color);\n" +
            "        }\n" +
            "        table {\n" +
            "            width: 100%;\n" +
            "            border-collapse: separate;\n" +
            "            border-spacing: 0;\n" +
            "            margin: 20px 0;\n" +
            "            background-color: var(--card-bg-color);\n" +
            "            border-radius: 8px;\n" +
            "            overflow: hidden;\n" +
            "            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);\n" +
            "        }\n" +
            "        th, td {\n" +
            "            padding: 12px 15px;\n" +
            "            text-align: left;\n" +
            "            border-bottom: 1px solid var(--border-color);\n" +
            "        }\n" +
            "        th {\n" +
            "            background-color: var(--primary-color);\n" +
            "            color: white;\n" +
            "            font-weight: 600;\n" +
            "            text-transform: uppercase;\n" +
            "            font-size: 0.9rem;\n" +
            "            letter-spacing: 0.5px;\n" +
            "        }\n" +
            "        tr:nth-child(even) {\n" +
            "            background-color: rgba(0, 0, 0, 0.02);\n" +
            "        }\n" +
            "        tr:hover {\n" +
            "            background-color: rgba(0, 0, 0, 0.05);\n" +
            "        }\n" +
            "        td:last-child, th:last-child {\n" +
            "            text-align: center;\n" +
            "        }\n" +
            "        a {\n" +
            "            color: var(--accent-color);\n" +
            "            text-decoration: none;\n" +
            "            font-weight: 500;\n" +
            "            transition: color 0.3s ease;\n" +
            "        }\n" +
            "        a:hover {\n" +
            "            color: var(--secondary-color);\n" +
            "            text-decoration: underline;\n" +
            "        }\n" +
            "        @media (max-width: 768px) {\n" +
            "            .container {\n" +
            "                padding: 15px;\n" +
            "            }\n" +
            "            h1 {\n" +
            "                font-size: 1.8rem;\n" +
            "            }\n" +
            "            h2 {\n" +
            "                font-size: 1.5rem;\n" +
            "            }\n" +
            "            th, td {\n" +
            "                padding: 8px 10px;\n" +
            "            }\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <header>\n" +
            "        <div class=\"container\">\n" +
            "            <h1>${reportTitle}</h1>\n" +
            "            <#if reportHeader??>\n" +
            "                <h2>${reportHeader}</h2>\n" +
            "            </#if>\n" +
            "        </div>\n" +
            "    </header>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"summary\">\n" +
            "            <h2>Summary</h2>\n" +
            "            <p><strong>Total Test Classes:</strong> ${testClasses?size}</p>\n" +
            "            <p><strong>Total Test Methods:</strong> ${totalMethods}</p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <h2>Test Classes</h2>\n" +
            "        <table>\n" +
            "            <tr>\n" +
            "                <th>Class Name</th>\n" +
            "                <th>Package</th>\n" +
            "                <th>Test Methods</th>\n" +
            "                <th>Percentage</th>\n" +
            "            </tr>\n" +
            "            <#list testClasses as class>\n" +
            "            <tr>\n" +
            "                <td><a href=\"${class.className}.html\">${class.className}</a></td>\n" +
            "                <td>${class.packageName}</td>\n" +
            "                <td>${class.testMethods?size}</td>\n" +
            "                <td>${class.percentage}%</td>\n" +
            "            </tr>\n" +
            "            </#list>\n" +
            "        </table>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
        
        Files.write(templatePath.resolve("index.ftl"), indexTemplate.getBytes());
    }

    private List<TestClassInfo> scanForTestClasses(String packageToScan) {
        System.out.println("Scanning for TestNG classes in package: " + packageToScan);
        
        List<TestClassInfo> testClasses = new ArrayList<>();
        int totalTestMethods = 0;
        
        try {
            // Get the ClassLoader
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            System.out.println("Using ClassLoader: " + classLoader);
            
            // Get all classes in the package
            String path = packageToScan.replace('.', '/');
            System.out.println("Looking for classes in path: " + path);
            Set<Class<?>> allClasses = new HashSet<>();
            
            // Try to find classes directly
            try {
                System.out.println("Attempting to get classes using getClasses method...");
                Class<?>[] classes = getClasses(packageToScan);
                System.out.println("getClasses returned " + classes.length + " classes");
                for (Class<?> clazz : classes) {
                    System.out.println("Found class: " + clazz.getName());
                }
                allClasses.addAll(Arrays.asList(classes));
            } catch (Exception e) {
                System.out.println("Warning: Could not load classes directly: " + e.getMessage());
                e.printStackTrace();
            }
            
            // If no classes found, try using Reflections library
            if (allClasses.isEmpty()) {
                System.out.println("No classes found directly, trying Reflections library...");
                try {
                    Reflections reflections = new Reflections(new ConfigurationBuilder()
                            .setUrls(ClasspathHelper.forPackage(packageToScan))
                            .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()));
                    
                    Set<Class<?>> reflectionClasses = reflections.getSubTypesOf(Object.class);
                    System.out.println("Reflections found " + reflectionClasses.size() + " classes");
                    for (Class<?> clazz : reflectionClasses) {
                        System.out.println("Found class via Reflections: " + clazz.getName());
                    }
                    allClasses.addAll(reflectionClasses);
                } catch (Exception e) {
                    System.out.println("Warning: Could not load classes using Reflections: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Found " + allClasses.size() + " classes in package");
            
            for (Class<?> clazz : allClasses) {
                List<TestMethodInfo> testMethods = new ArrayList<>();
                
                // Check each method for @Test annotation
                System.out.println("Checking methods in class: " + clazz.getName());
                for (Method method : clazz.getDeclaredMethods()) {
                    System.out.println("  Checking method: " + method.getName());
                    if (method.isAnnotationPresent(Test.class)) {
                        System.out.println("  Method " + method.getName() + " has @Test annotation");
                        String methodDescription = extractMethodLogic(clazz, method.getName());
                        TestMethodInfo methodInfo = new TestMethodInfo(method.getName(), methodDescription);
                        extractTagsFromMethod(method, methodInfo);
                        testMethods.add(methodInfo);
                    } else {
                        System.out.println("  Method " + method.getName() + " does not have @Test annotation");
                    }
                }
                
                if (!testMethods.isEmpty()) {
                    TestClassInfo classInfo = new TestClassInfo(
                            clazz.getSimpleName(),
                            clazz.getPackage().getName(),
                            testMethods
                    );
                    testClasses.add(classInfo);
                    totalTestMethods += testMethods.size();
                    System.out.println("Added class " + clazz.getName() + " with " + testMethods.size() + " test methods");
                } else {
                    System.out.println("Class " + clazz.getName() + " has no test methods, skipping");
                }
            }
            
            // Calculate percentages
            if (totalTestMethods > 0) {
                for (TestClassInfo classInfo : testClasses) {
                    double percentage = (double) classInfo.getTestMethods().size() / totalTestMethods * 100;
                    classInfo.setPercentage(String.format("%.1f", percentage));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error scanning for test classes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return testClasses;
    }
    
    /**
     * Scan for TestNG test classes in the specified source directory
     * 
     * @param sourceDirectory Directory containing Java source files
     * @return List of TestClassInfo objects
     */
    private List<TestClassInfo> scanForTestClassesFromSource(String sourceDirectory) {
        System.out.println("Scanning for TestNG classes in directory: " + sourceDirectory);
        
        List<TestClassInfo> testClasses = new ArrayList<>();
        
        try {
            // Find all Java files in the source directory
            File sourceDir = new File(sourceDirectory);
            if (!sourceDir.exists() || !sourceDir.isDirectory()) {
                System.err.println("Source directory does not exist or is not a directory: " + sourceDirectory);
                return testClasses;
            }
            
            List<File> javaFiles = findJavaFiles(sourceDir);
            System.out.println("Found " + javaFiles.size() + " Java files");
            
            // Process each Java file
            for (File javaFile : javaFiles) {
                System.out.println("Processing Java file: " + javaFile.getAbsolutePath());
                
                try {
                    // Parse the Java file
                    CompilationUnit cu = StaticJavaParser.parse(javaFile);
                    
                    // Extract package name
                    String packageName = "";
                    if (cu.getPackageDeclaration().isPresent()) {
                        packageName = cu.getPackageDeclaration().get().getNameAsString();
                        System.out.println("Package name: " + packageName);
                    } else {
                        System.out.println("No package declaration found");
                    }
                    
                    // Extract class name
                    List<ClassOrInterfaceDeclaration> classDeclarations = cu.findAll(ClassOrInterfaceDeclaration.class);
                    System.out.println("Found " + classDeclarations.size() + " class declarations");
                    
                    for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
                        String className = classDeclaration.getNameAsString();
                        System.out.println("Processing class: " + className);
                        
                        // Check if this is a test class (has methods with @Test annotation)
                        TestMethodVisitor methodVisitor = new TestMethodVisitor();
                        classDeclaration.accept(methodVisitor, null);
                        
                        List<TestMethodInfo> testMethods = methodVisitor.getTestMethods();
                        System.out.println("Found " + testMethods.size() + " test methods in class " + className);
                        
                        if (!testMethods.isEmpty()) {
                            TestClassInfo classInfo = new TestClassInfo(className, packageName, testMethods);
                            testClasses.add(classInfo);
                            System.out.println("Added test class: " + className + " with " + testMethods.size() + " test methods");
                        } else {
                            System.out.println("No test methods found in class: " + className);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing Java file " + javaFile.getAbsolutePath() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Calculate percentages
            int totalTestMethods = 0;
            for (TestClassInfo classInfo : testClasses) {
                totalTestMethods += classInfo.getTestMethods().size();
            }
            
            if (totalTestMethods > 0) {
                for (TestClassInfo classInfo : testClasses) {
                    double percentage = (double) classInfo.getTestMethods().size() / totalTestMethods * 100;
                    classInfo.setPercentage(String.format("%.1f", percentage));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error scanning for test classes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return testClasses;
    }

    private List<File> findJavaFiles(File sourceDir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(findJavaFiles(file));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }

    /**
     * Get all classes in a package
     * @param packageName The package name to scan
     * @return An array of classes in the package
     */
    private Class<?>[] getClasses(String packageName) {
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(packageName))
                    .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()));
            
            // First try to get classes annotated with @Test at class level
            Set<Class<?>> testAnnotatedClasses = reflections.getTypesAnnotatedWith(org.testng.annotations.Test.class);
            
            // If no classes found with @Test annotation at class level, get all classes in the package
            if (testAnnotatedClasses.isEmpty()) {
                Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
                return allClasses.toArray(new Class<?>[0]);
            }
            
            return testAnnotatedClasses.toArray(new Class<?>[0]);
        } catch (Exception e) {
            System.err.println("Error getting classes from package " + packageName + ": " + e.getMessage());
            return new Class<?>[0];
        }
    }

    private String extractMethodLogic(Class<?> clazz, String methodName) {
        try {
            // Try to find the source file
            String className = clazz.getSimpleName();
            String packageName = clazz.getPackage().getName();
            String packagePath = packageName.replace('.', '/');
            
            // Look for the source file in common source directories
            List<String> possibleSourceDirs = Arrays.asList(
                    "src/main/java",
                    "src/test/java",
                    "src"
            );
            
            Path sourcePath = null;
            for (String sourceDir : possibleSourceDirs) {
                Path path = Paths.get(sourceDir, packagePath, className + ".java");
                if (Files.exists(path)) {
                    sourcePath = path;
                    break;
                }
            }
            
            if (sourcePath == null) {
                // If source file not found in standard locations, try to find it using the class location
                URL classUrl = clazz.getResource(className + ".class");
                if (classUrl != null) {
                    String classPath = classUrl.getPath();
                    // Convert from class file to potential source file location
                    String sourcePotentialPath = classPath.replace("target/classes", "src/main/java")
                            .replace("target/test-classes", "src/test/java")
                            .replace(".class", ".java");
                    
                    Path potentialSourcePath = Paths.get(sourcePotentialPath);
                    if (Files.exists(potentialSourcePath)) {
                        sourcePath = potentialSourcePath;
                    }
                }
            }
            
            if (sourcePath != null) {
                return extractMethodLogicFromFile(sourcePath, methodName);
            }
            
            // If we can't find the source file, try to extract method information from the class itself
            StringBuilder methodInfo = new StringBuilder();
            try {
                Method method = clazz.getDeclaredMethod(methodName);
                methodInfo.append("Method: ").append(methodName).append("\n");
                methodInfo.append("Return type: ").append(method.getReturnType().getSimpleName()).append("\n");
                methodInfo.append("Parameters: ");
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 0) {
                    methodInfo.append("None");
                } else {
                    for (int i = 0; i < paramTypes.length; i++) {
                        if (i > 0) methodInfo.append(", ");
                        methodInfo.append(paramTypes[i].getSimpleName());
                    }
                }
                methodInfo.append("\n");
                
                // Add annotations
                methodInfo.append("Annotations: ");
                java.lang.annotation.Annotation[] annotations = method.getAnnotations();
                if (annotations.length == 0) {
                    methodInfo.append("None");
                } else {
                    for (int i = 0; i < annotations.length; i++) {
                        if (i > 0) methodInfo.append(", ");
                        methodInfo.append(annotations[i].annotationType().getSimpleName());
                    }
                }
            } catch (Exception e) {
                methodInfo.append("Could not extract method information: ").append(e.getMessage());
            }
            
            return methodInfo.toString();
        } catch (Exception e) {
            return "Error extracting method logic: " + e.getMessage();
        }
    }

    private String extractMethodLogicFromFile(Path sourcePath, String methodName) throws IOException {
        String source = new String(Files.readAllBytes(sourcePath));
        final StringBuilder methodLogic = new StringBuilder();
        
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(source).getResult().orElseThrow();
        
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration md, Void arg) {
                if (md.getNameAsString().equals(methodName)) {
                    if (md.getBody().isPresent()) {
                        // Instead of copying the raw code, generate a human-readable explanation
                        String rawCode = md.getBody().get().toString();
                        methodLogic.append(generateHumanReadableExplanation(rawCode, methodName));
                    }
                }
                super.visit(md, arg);
            }
        }, null);
        
        String logic = methodLogic.toString();
        
        return logic.isEmpty() ? "No method body found" : logic;
    }
    
    /**
     * Extract tags from the @Docs annotation on a method using reflection
     * @param method The method to check for annotations
     * @param methodInfo The TestMethodInfo to update with tags
     */
    private void extractTagsFromMethod(Method method, TestMethodInfo methodInfo) {
        try {
            // Try to get the annotation by name to handle cases where the class might not be available
            java.lang.annotation.Annotation[] annotations = method.getAnnotations();
            for (java.lang.annotation.Annotation annotation : annotations) {
                String annotationName = annotation.annotationType().getName();
                
                // Check if this is our Docs annotation
                if (annotationName.equals("io.vinipx.testngdoc.annotations.Docs")) {
                    // Use reflection to get the tags value
                    try {
                        Method tagsMethod = annotation.annotationType().getMethod("tags");
                        String[] tags = (String[]) tagsMethod.invoke(annotation);
                        
                        // Add each tag to the method info
                        for (String tag : tags) {
                            methodInfo.addTag(tag);
                        }
                    } catch (Exception e) {
                        System.err.println("Error extracting tags from annotation: " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting tags from method " + method.getName() + ": " + e.getMessage());
        }
    }

    private class TestMethodVisitor extends VoidVisitorAdapter<Void> {
        private final List<TestMethodInfo> testMethods = new ArrayList<>();

        public List<TestMethodInfo> getTestMethods() {
            return testMethods;
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            System.out.println("  Examining method: " + n.getNameAsString());
            System.out.println("  Annotations: " + n.getAnnotations());
            
            // Only process methods with TestNG annotations
            if (n.getAnnotations() != null && n.getAnnotations().stream()
                    .anyMatch(a -> {
                        String name = a.getNameAsString();
                        System.out.println("    Found annotation: " + name);
                        return name.equals("Test") || name.equals("org.testng.annotations.Test");
                    })) {
                
                System.out.println("  Method " + n.getNameAsString() + " is a TestNG test method");
                
                // Initialize test method info
                TestMethodInfo methodInfo = new TestMethodInfo();
                String methodName = n.getNameAsString();
                methodInfo.setName(methodName);
                
                // Check for documentation tags in @Docs annotation
                n.getAnnotations().stream()
                    .filter(a -> {
                        String name = a.getNameAsString();
                        return name.equals("Docs") || name.equals("io.vinipx.testngdoc.annotations.Docs");
                    })
                    .findFirst()
                    .ifPresent(docsAnnotation -> {
                        System.out.println("  Found @Docs annotation on method " + methodName);
                        docsAnnotation.getChildNodes().stream()
                            .filter(node -> node instanceof MemberValuePair)
                            .map(node -> (MemberValuePair) node)
                            .filter(pair -> pair.getNameAsString().equals("tags"))
                            .findFirst()
                            .ifPresent(tagsPair -> {
                                System.out.println("  Found tags in @Docs annotation");
                                if (tagsPair.getValue() instanceof ArrayInitializerExpr) {
                                    ArrayInitializerExpr arrayExpr = (ArrayInitializerExpr) tagsPair.getValue();
                                    arrayExpr.getValues().stream()
                                        .filter(value -> value instanceof StringLiteralExpr)
                                        .map(value -> {
                                            String tag = ((StringLiteralExpr) value).getValue();
                                            System.out.println("    Adding tag: " + tag);
                                            return tag;
                                        })
                                        .forEach(methodInfo::addTag);
                                }
                            });
                    });
                
                // Get method body to extract JavaDoc comment and method body
                if (n.getBody().isPresent()) {
                    String methodBody = n.getBody().get().toString();
                    methodInfo.setDescription(generateHumanReadableExplanation(methodBody, methodName));
                }
                
                testMethods.add(methodInfo);
                System.out.println("  Added test method: " + methodName + " with " + methodInfo.getTags().size() + " tags");
            }
            super.visit(n, arg);
        }
    }

    /**
     * Generates a human-readable explanation of the test method logic
     * @param rawCode The raw code of the test method
     * @param methodName The name of the test method
     * @return A human-readable explanation of the test method logic
     */
    String generateHumanReadableExplanation(String rawCode, String methodName) {
        // Remove braces and trim
        String cleanCode = rawCode.replaceAll("^\\{\\s*", "").replaceAll("\\s*\\}$", "").trim();
        
        // Split the code into lines
        String[] lines = cleanCode.split("\\n");
        
        StringBuilder explanation = new StringBuilder();
        
        // Extract comments as they often contain useful information
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("//")) {
                // Add comments directly as they are often already human-readable
                explanation.append(line.substring(2).trim()).append("\n");
            }
        }
        
        // If there were no comments, or we want to add more information
        if (explanation.length() == 0 || true) {
            // Check for test case ID pattern (e.g., TC01_, TC02_, etc.)
            String testCaseId = null;
            String originalMethodName = methodName; // Store the original method name
            java.util.regex.Pattern tcPattern = java.util.regex.Pattern.compile("^(TC\\d+)_(.*)$");
            java.util.regex.Matcher matcher = tcPattern.matcher(methodName);
            
            if (matcher.find()) {
                testCaseId = matcher.group(1);
                methodName = matcher.group(2); // Use the rest of the method name without the TC prefix
            }
            
            // Convert camelCase method name to readable format
            String readableMethodName = methodName.replace("test", "").replaceAll("([A-Z])", " $1").toLowerCase().trim();
            
            // Handle Gherkin-style method names (given/when/then)
            if (readableMethodName.contains("given") && readableMethodName.contains("when") && readableMethodName.contains("then")) {
                // Replace the standard format with a more structured Gherkin format
                readableMethodName = readableMethodName
                    .replaceAll("given", "\nGiven ")
                    .replaceAll("when", "\nWhen ")
                    .replaceAll("then", "\nThen ")
                    .replaceAll("test", "")
                    .replaceAll("_", " ") // Replace underscores with spaces
                    .trim();
                
                explanation = new StringBuilder();
                // Add original method name as title for reference
                explanation.append("Method: ").append(originalMethodName).append("\n\n");
                
                if (testCaseId != null) {
                    explanation.append(testCaseId).append("\n");
                }
                explanation.append(readableMethodName).append("\n\n");
            } else {
                // Add a general description based on the method name
                explanation.append("This test ");
                
                // If a test case ID was found, include it
                if (testCaseId != null) {
                    explanation.append("(").append(testCaseId).append(") ");
                }
                
                // For non-Gherkin style, also replace underscores with spaces
                readableMethodName = readableMethodName.replaceAll("_", " ");
                explanation.append(readableMethodName).append(".\n\n");
            }
            
            // Look for assertions to understand what's being tested
            for (String line : lines) {
                line = line.trim();
                if (line.contains("assert") || line.contains("Assert.")) {
                    String assertDescription = translateAssertToHumanReadable(line);
                    if (!assertDescription.isEmpty()) {
                        explanation.append("- ").append(assertDescription).append("\n");
                    }
                }
            }
        }
        
        // Apply pattern replacements to make the explanation more readable
        String result = explanation.toString();
        for (Map.Entry<String, String> entry : patternReplacements.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        
        return result;
    }
    
    /**
     * Translates an assertion statement to human-readable text
     * @param assertLine The assertion line of code
     * @return A human-readable explanation of the assertion
     */
    private String translateAssertToHumanReadable(String assertLine) {
        assertLine = assertLine.trim();
        
        // Handle different types of assertions
        if (assertLine.contains("assertEquals")) {
            return "Verifies that " + extractAssertionDetails(assertLine, "assertEquals");
        } else if (assertLine.contains("assertTrue")) {
            return "Confirms that " + extractAssertionDetails(assertLine, "assertTrue");
        } else if (assertLine.contains("assertFalse")) {
            return "Ensures that " + extractAssertionDetails(assertLine, "assertFalse");
        } else if (assertLine.contains("assertNotNull")) {
            return "Validates that " + extractAssertionDetails(assertLine, "assertNotNull");
        } else if (assertLine.contains("assertNull")) {
            return "Checks that " + extractAssertionDetails(assertLine, "assertNull");
        }
        
        return "";
    }
    
    /**
     * Extracts details from an assertion statement
     * @param assertLine The assertion line of code
     * @param assertType The type of assertion
     * @return Details extracted from the assertion
     */
    private String extractAssertionDetails(String assertLine, String assertType) {
        // Extract the message if it exists (often contains good explanation)
        int messageStart = assertLine.indexOf('"');
        if (messageStart != -1) {
            int messageEnd = assertLine.indexOf('"', messageStart + 1);
            if (messageEnd != -1) {
                return assertLine.substring(messageStart + 1, messageEnd);
            }
        }
        
        // If no message, try to extract the variables being compared
        int paramsStart = assertLine.indexOf('(');
        if (paramsStart != -1) {
            int paramsEnd = assertLine.lastIndexOf(')');
            if (paramsEnd != -1) {
                String params = assertLine.substring(paramsStart + 1, paramsEnd);
                return "the test condition is validated: " + params;
            }
        }
        
        return "the test condition is validated";
    }

    /**
     * Validates that the templates contain all required variables
     * @param cfg The FreeMarker configuration
     * @throws IOException If template loading fails
     */
    private void validateTemplates(Configuration cfg) throws IOException {
        try {
            // Get the templates
            Template indexTemplate = cfg.getTemplate("index.ftl");
            Template classTemplate = cfg.getTemplate("class.ftl");
            
            // Check index template
            String indexContent = loadTemplateContent("index.ftl", cfg);
            List<String> requiredIndexVars = Arrays.asList(
                "reportTitle", "darkMode", "displayTagsChart", "testClasses", "totalMethods"
            );
            
            // Check class template
            String classContent = loadTemplateContent("class.ftl", cfg);
            List<String> requiredClassVars = Arrays.asList(
                "className", "packageName", "testMethods", "percentage", "reportTitle", "darkMode"
            );
            
            boolean indexValid = validateTemplateVariables(indexContent, requiredIndexVars, "index.ftl");
            boolean classValid = validateTemplateVariables(classContent, requiredClassVars, "class.ftl");
            
            if (!indexValid || !classValid) {
                System.err.println("WARNING: Templates are missing required variables. Documentation may not render correctly.");
                System.err.println("Consider using the default templates or updating your custom templates.");
            }
        } catch (Exception e) {
            System.err.println("Could not validate templates: " + e.getMessage());
        }
    }
    
    /**
     * Loads template content as a string
     * @param templateName The template name
     * @param cfg The FreeMarker configuration
     * @return The template content as a string
     */
    private String loadTemplateContent(String templateName, Configuration cfg) {
        try {
            // Try to load from classpath first
            InputStream is = getClass().getClassLoader().getResourceAsStream("templates/" + templateName);
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
            
            // Try to load from file system
            Path templatePath = Paths.get(TEMPLATE_DIR, templateName);
            if (Files.exists(templatePath)) {
                return Files.readString(templatePath);
            }
        } catch (Exception e) {
            System.err.println("Could not load template content: " + e.getMessage());
        }
        return "";
    }
    
    /**
     * Validates that a template contains all required variables
     * @param templateContent The template content
     * @param requiredVars List of required variables
     * @param templateName The template name for logging
     * @return True if all required variables are present
     */
    private boolean validateTemplateVariables(String templateContent, List<String> requiredVars, String templateName) {
        boolean valid = true;
        for (String var : requiredVars) {
            // Simple check for ${varName} pattern
            if (!templateContent.contains("${" + var + "}") && 
                !templateContent.contains("<#if " + var) && 
                !templateContent.contains(var + "??")) {
                System.err.println("WARNING: Template " + templateName + " is missing required variable: " + var);
                valid = false;
            }
        }
        return valid;
    }

    private void generateClassDocumentation(List<TestClassInfo> testClasses, Configuration cfg) 
            throws IOException, TemplateException {
        Template template = cfg.getTemplate("class.ftl");
        
        for (TestClassInfo testClass : testClasses) {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("className", testClass.getClassName());
            dataModel.put("packageName", testClass.getPackageName());
            dataModel.put("testMethods", testClass.getTestMethods());
            dataModel.put("percentage", testClass.getPercentage());
            dataModel.put("darkMode", darkMode);
            dataModel.put("reportTitle", reportTitle);
            dataModel.put("reportHeader", reportHeader);
            
            try (Writer out = new FileWriter(new File(OUTPUT_DIR, testClass.getClassName() + ".html"))) {
                template.process(dataModel, out);
            }
        }
        
        // Copy CSS to ensure styles are properly loaded when used as a dependency
        try {
            Path cssDir = Paths.get(OUTPUT_DIR, "css");
            if (!Files.exists(cssDir)) {
                Files.createDirectories(cssDir);
            }
            
            // Create a comprehensive CSS file with all styles
            String cssContent = generateComprehensiveCSS();
            Files.write(cssDir.resolve("styles.css"), cssContent.getBytes());
        } catch (IOException e) {
            System.out.println("Warning: Could not create CSS file: " + e.getMessage());
        }
    }

    private void generateIndexPage(List<TestClassInfo> testClasses, Configuration cfg) 
            throws IOException, TemplateException {
        Template template = cfg.getTemplate("index.ftl");
        
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("testClasses", testClasses);
        int totalMethods = testClasses.stream()
                .mapToInt(tc -> tc.getTestMethods().size())
                .sum();
        dataModel.put("totalMethods", totalMethods);
        dataModel.put("displayTagsChart", displayTagsChart);
        dataModel.put("darkMode", darkMode);
        dataModel.put("reportTitle", reportTitle);
        dataModel.put("reportHeader", reportHeader);
        
        // Only collect tag statistics if the chart is enabled
        if (displayTagsChart) {
            // Collect tag statistics
            Map<String, Integer> tagStats = new HashMap<>();
            
            for (TestClassInfo testClass : testClasses) {
                for (TestMethodInfo method : testClass.getTestMethods()) {
                    for (String tag : method.getTags()) {
                        // Group similar tags by their prefix (e.g., "Feature: X" and "Feature: Y" are grouped as "Feature")
                        String tagCategory = tag;
                        if (tag.contains(":")) {
                            tagCategory = tag.substring(0, tag.indexOf(":")).trim();
                        }
                        
                        tagStats.put(tagCategory, tagStats.getOrDefault(tagCategory, 0) + 1);
                    }
                }
            }
            
            // Add tag statistics to the data model
            dataModel.put("tagStats", tagStats);
            
            // Calculate percentages for each tag category
            Map<String, Double> tagPercentages = new HashMap<>();
            for (Map.Entry<String, Integer> entry : tagStats.entrySet()) {
                double percentage = (double) entry.getValue() / totalMethods * 100;
                tagPercentages.put(entry.getKey(), Math.round(percentage * 10) / 10.0); // Round to 1 decimal place
            }
            dataModel.put("tagPercentages", tagPercentages);
        }
        
        try (Writer out = new FileWriter(new File(OUTPUT_DIR, "index.html"))) {
            template.process(dataModel, out);
        }
    }

    /**
     * Regenerates only the index page with current settings
     * Useful when you want to update settings without regenerating all documentation
     * @return this TestNGDocGenerator instance for method chaining
     * @throws IOException if an I/O error occurs
     * @throws TemplateException if a template error occurs
     */
    public TestNGDocGenerator generateIndexPage() throws IOException, TemplateException {
        // Check if we have already generated documentation
        File indexFile = new File(OUTPUT_DIR, "index.html");
        if (!indexFile.exists()) {
            System.out.println("No existing documentation found. Please generate documentation first.");
            return this;
        }
        
        // Initialize FreeMarker
        Configuration cfg = initializeFreemarker();
        
        // Load existing test classes if available
        List<TestClassInfo> testClasses = new ArrayList<>();
        File[] classFiles = new File(OUTPUT_DIR).listFiles((dir, name) -> 
            name.endsWith(".html") && !name.equals("index.html"));
        
        if (classFiles != null) {
            for (File file : classFiles) {
                String className = file.getName().replace(".html", "");
                // Create a minimal TestClassInfo object
                TestClassInfo classInfo = new TestClassInfo(className, "", new ArrayList<>());
                testClasses.add(classInfo);
            }
        }
        
        // Generate the index page
        generateIndexPage(testClasses, cfg);
        
        return this;
    }

    /**
     * Generates a comprehensive CSS file with all necessary styles for the documentation
     * @return String containing the complete CSS content
     */
    private String generateComprehensiveCSS() {
        return "/* TestNG Documentation Generator Styles */\n" +
            ":root {\n" +
            "    --primary-color: #3f51b5;\n" +
            "    --secondary-color: #303f9f;\n" +
            "    --accent-color: #7986cb;\n" +
            "    --background-color: #fafafa;\n" +
            "    --card-bg-color: #ffffff;\n" +
            "    --text-color: #424242;\n" +
            "    --border-color: #e0e0e0;\n" +
            "    --success-color: #4caf50;\n" +
            "    --warning-color: #ff9800;\n" +
            "    --error-color: #f44336;\n" +
            "}\n" +
            "/* Dark mode variables - Modern style */\n" +
            ".dark-mode {\n" +
            "    --primary-color: #5c6bc0;\n" +
            "    --secondary-color: #3949ab;\n" +
            "    --accent-color: #9fa8da;\n" +
            "    --background-color: #121212;\n" +
            "    --card-bg-color: #1e1e1e;\n" +
            "    --text-color: #e0e0e0;\n" +
            "    --border-color: #333333;\n" +
            "    --success-color: #66bb6a;\n" +
            "    --warning-color: #ffa726;\n" +
            "    --error-color: #ef5350;\n" +
            "}\n" +
            "* {\n" +
            "    box-sizing: border-box;\n" +
            "    margin: 0;\n" +
            "    padding: 0;\n" +
            "}\n" +
            "@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');\n" +
            "body {\n" +
            "    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;\n" +
            "    line-height: 1.6;\n" +
            "    color: var(--text-color);\n" +
            "    background-color: var(--background-color);\n" +
            "    padding: 0;\n" +
            "    margin: 0;\n" +
            "    font-weight: 400;\n" +
            "    -webkit-font-smoothing: antialiased;\n" +
            "    -moz-osx-font-smoothing: grayscale;\n" +
            "}\n" +
            ".dark-mode body {\n" +
            "    font-weight: 300;\n" +
            "}\n" +
            ".container {\n" +
            "    max-width: 1200px;\n" +
            "    margin: 0 auto;\n" +
            "    padding: 20px;\n" +
            "}\n" +
            "header {\n" +
            "    background-color: var(--primary-color);\n" +
            "    color: white;\n" +
            "    padding: 24px 0;\n" +
            "    margin-bottom: 40px;\n" +
            "    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);\n" +
            "}\n" +
            "h1 {\n" +
            "    font-size: 2.2rem;\n" +
            "    margin-bottom: 10px;\n" +
            "    color: white;\n" +
            "    font-weight: 600;\n" +
            "    letter-spacing: -0.5px;\n" +
            "}\n" +
            "h2 {\n" +
            "    font-size: 1.8rem;\n" +
            "    margin: 30px 0 20px;\n" +
            "    color: var(--primary-color);\n" +
            "    border-bottom: 2px solid var(--border-color);\n" +
            "    padding-bottom: 10px;\n" +
            "    font-weight: 500;\n" +
            "}\n" +
            "h3 {\n" +
            "    font-size: 1.4rem;\n" +
            "    margin: 20px 0 12px;\n" +
            "    color: var(--primary-color);\n" +
            "    font-weight: 500;\n" +
            "}\n" +
            "a {\n" +
            "    color: var(--primary-color);\n" +
            "    text-decoration: none;\n" +
            "    transition: color 0.2s ease, border-bottom 0.2s ease;\n" +
            "    border-bottom: 1px solid transparent;\n" +
            "}\n" +
            "a:hover {\n" +
            "    text-decoration: none;\n" +
            "    color: var(--accent-color);\n" +
            "    border-bottom: 1px solid var(--accent-color);\n" +
            "}\n" +
            ".summary-container {\n" +
            "    display: flex;\n" +
            "    flex-wrap: wrap;\n" +
            "    gap: 24px;\n" +
            "    margin-bottom: 40px;\n" +
            "}\n" +
            ".summary {\n" +
            "    background-color: var(--card-bg-color);\n" +
            "    border-radius: 12px;\n" +
            "    padding: 24px;\n" +
            "    margin-bottom: 30px;\n" +
            "    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);\n" +
            "    border: none;\n" +
            "    flex: 1;\n" +
            "    min-width: 300px;\n" +
            "}\n" +
            ".summary:hover {\n" +
            "    transform: translateY(-2px);\n" +
            "    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);\n" +
            "}\n" +
            "table {\n" +
            "    width: 100%;\n" +
            "    border-collapse: collapse;\n" +
            "    border-spacing: 0;\n" +
            "    margin: 24px 0;\n" +
            "    background-color: var(--card-bg-color);\n" +
            "    border-radius: 12px;\n" +
            "    overflow: hidden;\n" +
            "    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);\n" +
            "}\n" +
            "th {\n" +
            "    background-color: var(--secondary-color);\n" +
            "    color: white;\n" +
            "    text-align: left;\n" +
            "    padding: 16px 20px;\n" +
            "    font-weight: 500;\n" +
            "    font-size: 0.95rem;\n" +
            "    text-transform: uppercase;\n" +
            "    letter-spacing: 0.5px;\n" +
            "}\n" +
            "td {\n" +
            "    padding: 14px 20px;\n" +
            "    border-top: 1px solid var(--border-color);\n" +
            "}\n" +
            "tr:hover {\n" +
            "    background-color: rgba(0, 0, 0, 0.02);\n" +
            "}\n" +
            ".dark-mode tr:hover {\n" +
            "    background-color: rgba(255, 255, 255, 0.03);\n" +
            "}\n" +
            "td:last-child, th:last-child {\n" +
            "    text-align: center;\n" +
            "}\n" +
            "a {\n" +
            "    color: var(--accent-color);\n" +
            "    text-decoration: none;\n" +
            "    font-weight: 500;\n" +
            "    transition: color 0.3s ease;\n" +
            "}\n" +
            "a:hover {\n" +
            "    color: var(--secondary-color);\n" +
            "    text-decoration: underline;\n" +
            "}\n" +
            ".chart-container {\n" +
            "    background-color: var(--card-bg-color);\n" +
            "    border-radius: 12px;\n" +
            "    padding: 24px;\n" +
            "    margin-bottom: 30px;\n" +
            "    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);\n" +
            "    border: none;\n" +
            "    flex: 1;\n" +
            "    min-width: 300px;\n" +
            "    max-width: 500px;\n" +
            "    height: 100%;\n" +
            "    transition: transform 0.2s ease, box-shadow 0.2s ease;\n" +
            "}\n" +
            ".chart-container:hover {\n" +
            "    transform: translateY(-2px);\n" +
            "    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);\n" +
            "}\n" +
            ".method {\n" +
            "    background-color: var(--card-bg-color);\n" +
            "    border: none;\n" +
            "    border-radius: 12px;\n" +
            "    padding: 24px;\n" +
            "    margin-bottom: 24px;\n" +
            "    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);\n" +
            "    transition: transform 0.2s ease, box-shadow 0.2s ease;\n" +
            "}\n" +
            ".method:hover {\n" +
            "    transform: translateY(-2px);\n" +
            "    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);\n" +
            "}\n" +
            ".method-name {\n" +
            "    font-weight: 500;\n" +
            "    color: var(--primary-color);\n" +
            "    font-size: 1.2rem;\n" +
            "    margin-bottom: 16px;\n" +
            "    padding-bottom: 12px;\n" +
            "    border-bottom: 1px solid var(--border-color);\n" +
            "}\n" +
            ".method-description {\n" +
            "    color: var(--text-color);\n" +
            "    font-size: 0.95rem;\n" +
            "    line-height: 1.7;\n" +
            "}\n" +
            ".header-note {\n" +
            "    font-size: 1.1rem;\n" +
            "    margin-top: 5px;\n" +
            "    opacity: 0.9;\n" +
            "    font-weight: 300;\n" +
            "}\n" +
            ".chart-title {\n" +
            "    margin-top: 0;\n" +
            "    margin-bottom: 16px;\n" +
            "    font-size: 1.2rem;\n" +
            "    text-align: center;\n" +
            "}\n" +
            "@media (max-width: 768px) {\n" +
            "    .container {\n" +
            "        padding: 16px;\n" +
            "    }\n" +
            "    h1 {\n" +
            "        font-size: 1.8rem;\n" +
            "    }\n" +
            "    h2 {\n" +
            "        font-size: 1.5rem;\n" +
            "    }\n" +
            "    table {\n" +
            "        display: block;\n" +
            "        overflow-x: auto;\n" +
            "    }\n" +
            "    th, td {\n" +
            "        padding: 8px 10px;\n" +
            "    }\n" +
            "    .summary-container {\n" +
            "        flex-direction: column;\n" +
            "    }\n" +
            "    .chart-container {\n" +
            "        max-width: 100%;\n" +
            "    }\n" +
            "}\n";
    }

    /**
     * Synchronizes templates from the library to the project's template directory.
     * This ensures that the project's templates are up-to-date with the library's templates.
     * 
     * @param projectDir The project directory to synchronize templates to
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator synchronizeTemplates(String projectDir) {
        try {
            System.out.println("Synchronizing templates to project directory: " + projectDir);
            
            // Backup existing templates
            if (TemplateSync.backupTemplates(projectDir)) {
                System.out.println("Existing templates backed up to " + projectDir + "/templates_backup");
            }
            
            // Synchronize templates
            if (TemplateSync.syncTemplates(projectDir)) {
                System.out.println("Templates successfully synchronized");
            } else {
                System.err.println("Failed to synchronize templates");
            }
        } catch (Exception e) {
            System.err.println("Error synchronizing templates: " + e.getMessage());
        }
        
        return this;
    }
    
    /**
     * Checks if the project's templates are synchronized with the library's templates.
     * 
     * @param projectDir The project directory to check
     * @return True if templates are synchronized
     */
    public boolean areTemplatesSynchronized(String projectDir) {
        return TemplateSync.areTemplatesSynchronized(projectDir);
    }

    // Inner classes for storing test information
    public static class TestClassInfo {
        private final String className;
        private final String packageName;
        private final List<TestMethodInfo> testMethods;
        private String percentage;

        public TestClassInfo(String className, String packageName, List<TestMethodInfo> testMethods) {
            this.className = className;
            this.packageName = packageName;
            this.testMethods = testMethods;
            this.percentage = "0.00";
        }

        public String getClassName() {
            return className;
        }

        public String getPackageName() {
            return packageName;
        }

        public List<TestMethodInfo> getTestMethods() {
            return testMethods;
        }

        public String getPercentage() {
            return percentage;
        }

        public void setPercentage(String percentage) {
            this.percentage = percentage;
        }
    }

    public static class TestMethodInfo {
        private String name;
        private String description;
        private List<String> tags = new ArrayList<>();

        public TestMethodInfo(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        // Default constructor for when we build the object incrementally
        public TestMethodInfo() {
        }

        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public List<String> getTags() {
            return tags;
        }
        
        public void setTags(List<String> tags) {
            this.tags = tags;
        }
        
        public void addTag(String tag) {
            this.tags.add(tag);
        }
    }

    /**
     * Filter test methods based on include/exclude patterns
     * 
     * @param methods List of test methods to filter
     * @return Filtered list of test methods
     */
    private List<TestMethodInfo> filterTestMethods(List<TestMethodInfo> methods) {
        if (methods == null || methods.isEmpty()) {
            return methods;
        }
        
        // If no include/exclude patterns are specified, return all methods
        if (includeMethodPatterns.isEmpty() && excludeMethodPatterns.isEmpty() && 
            includeTagPatterns.isEmpty() && excludeTagPatterns.isEmpty()) {
            return methods;
        }
        
        List<TestMethodInfo> filteredMethods = new ArrayList<>();
        
        for (TestMethodInfo method : methods) {
            boolean include = true;
            
            // Check method name patterns
            if (!includeMethodPatterns.isEmpty()) {
                boolean matchesInclude = false;
                for (String pattern : includeMethodPatterns) {
                    if (method.getName().matches(pattern)) {
                        matchesInclude = true;
                        break;
                    }
                }
                include = matchesInclude;
            }
            
            if (include && !excludeMethodPatterns.isEmpty()) {
                for (String pattern : excludeMethodPatterns) {
                    if (method.getName().matches(pattern)) {
                        include = false;
                        break;
                    }
                }
            }
            
            // Check tag patterns
            if (include && !includeTagPatterns.isEmpty() && !method.getTags().isEmpty()) {
                boolean matchesIncludeTag = false;
                for (String tag : method.getTags()) {
                    for (String pattern : includeTagPatterns) {
                        if (tag.matches(pattern)) {
                            matchesIncludeTag = true;
                            break;
                        }
                    }
                    if (matchesIncludeTag) break;
                }
                include = matchesIncludeTag;
            }
            
            if (include && !excludeTagPatterns.isEmpty() && !method.getTags().isEmpty()) {
                for (String tag : method.getTags()) {
                    for (String pattern : excludeTagPatterns) {
                        if (tag.matches(pattern)) {
                            include = false;
                            break;
                        }
                    }
                    if (!include) break;
                }
            }
            
            if (include) {
                filteredMethods.add(method);
            }
        }
        
        return filteredMethods;
    }

    /**
     * Enable dark mode for the documentation
     * 
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator useDarkMode() {
        this.darkMode = true;
        return this;
    }
    
    /**
     * Set dark mode for the documentation
     * 
     * @param enabled Whether dark mode should be enabled or disabled
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator useDarkMode(boolean enabled) {
        this.darkMode = enabled;
        return this;
    }

    /**
     * Get the template directory, creating it if it doesn't exist
     */
    private File getTemplateDirectory() {
        File templateDir = new File(TEMPLATE_DIR);
        if (!templateDir.exists()) {
            System.out.println("Template directory does not exist. Creating: " + templateDir.getAbsolutePath());
            boolean created = templateDir.mkdirs();
            if (!created) {
                System.err.println("Failed to create template directory: " + templateDir.getAbsolutePath());
            }
        }
        return templateDir;
    }

    /**
     * Ensures that template files exist, creating them from classpath resources if they don't
     */
    private void ensureTemplateFilesExist() {
        // Create templates in the main template directory
        ensureTemplateFilesExistInDirectory(new File(TEMPLATE_DIR));
        
        // Also create templates in the output directory for backward compatibility
        ensureTemplateFilesExistInDirectory(new File(OUTPUT_DIR, "templates"));
    }
    
    /**
     * Ensures that template files exist in the specified directory, creating them from classpath resources if they don't
     */
    private void ensureTemplateFilesExistInDirectory(File templateDir) {
        if (!templateDir.exists()) {
            System.out.println("Template directory does not exist. Creating: " + templateDir.getAbsolutePath());
            boolean created = templateDir.mkdirs();
            if (!created) {
                System.err.println("Failed to create template directory: " + templateDir.getAbsolutePath());
                return;
            }
        }
        
        // List of template files to check/create
        String[] templateFiles = {"index.ftl", "class.ftl"};
        
        for (String templateFile : templateFiles) {
            File file = new File(templateDir, templateFile);
            
            if (!file.exists()) {
                System.out.println("Template file does not exist. Creating: " + file.getAbsolutePath());
                
                try (InputStream is = getClass().getResourceAsStream("/templates/" + templateFile);
                     FileOutputStream fos = new FileOutputStream(file)) {
                    
                    if (is == null) {
                        System.err.println("Could not find template resource: /templates/" + templateFile);
                        continue;
                    }
                    
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    
                    System.out.println("Created template file: " + file.getAbsolutePath());
                } catch (IOException e) {
                    System.err.println("Error creating template file: " + file.getAbsolutePath() + " - " + e.getMessage());
                }
            }
        }
    }

    /**
     * Set the report title
     * @param title The title to use for the report
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator setReportTitle(String title) {
        this.reportTitle = title;
        return this;
    }

    /**
     * Set the report header
     * @param header The header to use for the report
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator setReportHeader(String header) {
        this.reportHeader = header;
        return this;
    }

    /**
     * Enable or disable the display of the tags chart
     * @param enabled Whether the tags chart should be displayed
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator displayTagsChart(boolean enabled) {
        this.displayTagsChart = enabled;
        return this;
    }
    
    /**
     * Enable the display of the tags chart
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator displayTagsChart() {
        return displayTagsChart(true);
    }

    /**
     * Add a method pattern to include in the documentation
     * @param pattern The pattern to include
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator includeMethodPattern(String pattern) {
        this.includeMethodPatterns.add(pattern);
        return this;
    }

    /**
     * Add a method pattern to exclude from the documentation
     * @param pattern The pattern to exclude
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator excludeMethodPattern(String pattern) {
        this.excludeMethodPatterns.add(pattern);
        return this;
    }

    /**
     * Add a tag pattern to include in the documentation
     * @param pattern The pattern to include
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator includeTagPattern(String pattern) {
        this.includeTagPatterns.add(pattern);
        return this;
    }

    /**
     * Add a tag pattern to exclude from the documentation
     * @param pattern The pattern to exclude
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator excludeTagPattern(String pattern) {
        this.excludeTagPatterns.add(pattern);
        return this;
    }

    /**
     * Set the output directory for the documentation
     * @param outputDir The output directory to use
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator setOutputDirectory(String outputDir) {
        this.OUTPUT_DIR = outputDir;
        return this;
    }
}

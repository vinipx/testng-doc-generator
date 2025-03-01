package io.vinipx.testngdoc;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
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
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                return cfg;
            }
        } catch (Exception e) {
            System.out.println("Could not load templates from classpath: " + e.getMessage());
        }
        
        // If not found in classpath, try to use templates from the file system
        Path templatePath = Paths.get(TEMPLATE_DIR);
        if (!Files.exists(templatePath)) {
            try {
                Files.createDirectories(templatePath);
                createDefaultTemplates(templatePath);
                cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_DIR));
            } catch (IOException e) {
                System.out.println("Could not create template directory: " + e.getMessage());
                // Last resort: use classpath resources with default templates
                cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
            }
        } else {
            // Template directory exists, use it
            cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_DIR));
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
            
            // Get all classes in the package
            String path = packageToScan.replace('.', '/');
            Set<Class<?>> allClasses = new HashSet<>();
            
            // Try to find classes directly
            try {
                Class<?>[] classes = getClasses(packageToScan);
                allClasses.addAll(Arrays.asList(classes));
            } catch (Exception e) {
                System.out.println("Warning: Could not load classes directly: " + e.getMessage());
            }
            
            // If no classes found, try using Reflections library
            if (allClasses.isEmpty()) {
                Reflections reflections = new Reflections(new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(packageToScan))
                        .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()));
                
                allClasses.addAll(reflections.getSubTypesOf(Object.class));
            }
            
            System.out.println("Found " + allClasses.size() + " classes in package");
            
            for (Class<?> clazz : allClasses) {
                List<TestMethodInfo> testMethods = new ArrayList<>();
                
                // Check each method for @Test annotation
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Test.class)) {
                        String methodDescription = extractMethodLogic(clazz, method.getName());
                        TestMethodInfo methodInfo = new TestMethodInfo(method.getName(), methodDescription);
                        extractTagsFromMethod(method, methodInfo);
                        testMethods.add(methodInfo);
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
        int totalTestMethods = 0;
        
        try {
            // Find all Java files in the source directory
            List<Path> javaFiles = Files.walk(Paths.get(sourceDirectory))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());
            
            System.out.println("Found " + javaFiles.size() + " Java files");
            
            for (Path javaFile : javaFiles) {
                try {
                    // Parse the Java file
                    JavaParser javaParser = new JavaParser();
                    CompilationUnit cu = javaParser.parse(javaFile).getResult().orElse(null);
                    
                    if (cu == null) {
                        System.out.println("Could not parse file: " + javaFile);
                        continue;
                    }
                    
                    // Extract package name
                    String packageName = cu.getPackageDeclaration()
                            .map(pd -> pd.getName().asString())
                            .orElse("default");
                    
                    // Extract class name from file name
                    String fileName = javaFile.getFileName().toString();
                    String className = fileName.substring(0, fileName.length() - 5); // Remove .java
                    
                    // Check if this file contains TestNG test methods
                    List<TestMethodInfo> testMethods = new ArrayList<>();
                    
                    // Visit all method declarations
                    cu.accept(new TestMethodVisitor(packageName, className, testMethods), null);
                    
                    if (!testMethods.isEmpty()) {
                        TestClassInfo classInfo = new TestClassInfo(
                                className,
                                packageName,
                                testMethods
                        );
                        testClasses.add(classInfo);
                        totalTestMethods += testMethods.size();
                    }
                } catch (Exception e) {
                    System.err.println("Error processing file " + javaFile + ": " + e.getMessage());
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
     * Get all classes in a package
     * @param packageName The package name to scan
     * @return An array of classes in the package
     */
    private Class<?>[] getClasses(String packageName) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()));
        
        Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(org.testng.annotations.Test.class);
        return allClasses.toArray(new Class<?>[0]);
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
        private final String packageName;
        private final String className;
        private final List<TestMethodInfo> testMethods;

        public TestMethodVisitor(String packageName, String className, List<TestMethodInfo> testMethods) {
            this.packageName = packageName;
            this.className = className;
            this.testMethods = testMethods;
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            // Only process methods with TestNG annotations
            if (n.getAnnotations() != null && n.getAnnotations().stream()
                    .anyMatch(a -> a.getNameAsString().equals("Test") || 
                               a.getNameAsString().equals("org.testng.annotations.Test"))) {
                
                // Initialize test method info
                TestMethodInfo methodInfo = new TestMethodInfo();
                String methodName = n.getNameAsString();
                methodInfo.setName(methodName);
                
                // Check for documentation tags in @Docs annotation
                n.getAnnotations().stream()
                    .filter(a -> a.getNameAsString().equals("Docs") || 
                                a.getNameAsString().equals("io.vinipx.testngdoc.annotations.Docs"))
                    .findFirst()
                    .ifPresent(docsAnnotation -> {
                        docsAnnotation.getChildNodes().stream()
                            .filter(node -> node instanceof MemberValuePair)
                            .map(node -> (MemberValuePair) node)
                            .filter(pair -> pair.getNameAsString().equals("tags"))
                            .findFirst()
                            .ifPresent(tagsPair -> {
                                if (tagsPair.getValue() instanceof ArrayInitializerExpr) {
                                    ArrayInitializerExpr arrayExpr = (ArrayInitializerExpr) tagsPair.getValue();
                                    arrayExpr.getValues().stream()
                                        .filter(value -> value instanceof StringLiteralExpr)
                                        .map(value -> ((StringLiteralExpr) value).getValue())
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
     * Set a custom pattern replacement map for improving readability of test documentation.
     * Keys in the map represent patterns to find, and values represent their replacements.
     * 
     * @param patternReplacements Map with pattern replacements (pattern -&gt; replacement)
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator setPatternReplacements(Map<String, String> patternReplacements) {
        this.patternReplacements = patternReplacements;
        return this;
    }
    
    /**
     * Add a single pattern replacement for improving readability of test documentation.
     * 
     * @param pattern The pattern to find
     * @param replacement The replacement text
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator addPatternReplacement(String pattern, String replacement) {
        this.patternReplacements.put(pattern, replacement);
        return this;
    }
    
    /**
     * Clear all pattern replacements.
     * 
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator clearPatternReplacements() {
        this.patternReplacements.clear();
        return this;
    }
    
    /**
     * Get the current pattern replacement map.
     * 
     * @return The current pattern replacement map
     */
    public Map<String, String> getPatternReplacements() {
        return new HashMap<>(this.patternReplacements);
    }

    /**
     * Enables dark mode for the generated documentation.
     *
     * @return the TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator useDarkMode() {
        return useDarkMode(true);
    }

    /**
     * Sets the dark mode setting for the generated documentation.
     *
     * @param enabled whether dark mode should be enabled
     * @return the TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator useDarkMode(boolean enabled) {
        this.darkMode = enabled;
        return this;
    }

    /**
     * Set whether to display a pie chart of tag statistics on the index page
     * @param display True to display the chart, false to hide it
     * @return This TestNGDocGenerator instance for chaining
     */
    public TestNGDocGenerator displayTagsChart(boolean display) {
        this.displayTagsChart = display;
        return this;
    }

    /**
     * Enable the tag statistics chart on the index page
     * @return This TestNGDocGenerator instance for chaining
     */
    public TestNGDocGenerator displayTagsChart() {
        return displayTagsChart(true);
    }

    /**
     * Sets a custom title for the documentation report.
     * This will replace the default "TestNG Documentation" title.
     *
     * @param title the custom report title
     * @return the TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator setReportTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.reportTitle = title.trim();
        }
        return this;
    }

    /**
     * Sets a custom header text to be displayed below the title in the documentation report.
     *
     * @param header the custom header text
     * @return the TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator setReportHeader(String header) {
        this.reportHeader = header;
        return this;
    }

    /**
     * Set the output directory for the generated documentation
     * 
     * @param outputDir The directory where documentation will be generated
     * @return This TestNGDocGenerator instance for method chaining
     */
    public TestNGDocGenerator setOutputDirectory(String outputDir) {
        OUTPUT_DIR = outputDir;
        return this;
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
}

package com.testngdoc;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.testng.annotations.Test;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TestNGDocGenerator {

    private static final String OUTPUT_DIR = "testng-docs";
    private static final String TEMPLATE_DIR = "templates";

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

    public void generateDocumentation(String packageToScan) throws IOException, TemplateException {
        System.out.println("Scanning package: " + packageToScan);
        
        // Create output directory
        Path outputPath = Paths.get(OUTPUT_DIR);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        // Initialize Freemarker
        Configuration cfg = initializeFreemarker();
        
        // Scan for TestNG classes
        List<TestClassInfo> testClasses = scanForTestClasses(packageToScan);
        
        // Generate documentation
        generateClassDocumentation(testClasses, cfg);
        generateIndexPage(testClasses, cfg);
        
        System.out.println("Documentation generated successfully in directory: " + outputPath.toAbsolutePath());
    }

    private Configuration initializeFreemarker() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        
        // Create template directory if it doesn't exist
        Path templatePath = Paths.get(TEMPLATE_DIR);
        if (!Files.exists(templatePath)) {
            Files.createDirectories(templatePath);
            createDefaultTemplates(templatePath);
        }
        
        cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_DIR));
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    private void createDefaultTemplates(Path templatePath) throws IOException {
        // Create class template
        String classTemplate = 
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>${className} - TestNG Documentation</title>\n" +
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
            "            <h1>${className}</h1>\n" +
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
            "    <title>TestNG Documentation</title>\n" +
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
            "            <h1>TestNG Documentation</h1>\n" +
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
                        testMethods.add(new TestMethodInfo(method.getName(), methodDescription));
                    }
                }
                
                if (!testMethods.isEmpty()) {
                    testClasses.add(new TestClassInfo(
                            clazz.getSimpleName(),
                            clazz.getPackage().getName(),
                            testMethods
                    ));
                    totalTestMethods += testMethods.size();
                }
            }
            
            // Calculate percentages
            if (totalTestMethods > 0) {
                for (TestClassInfo testClass : testClasses) {
                    double percentage = (double) testClass.getTestMethods().size() / totalTestMethods * 100;
                    testClass.setPercentage(String.format("%.2f", percentage));
                }
            }
        } catch (Exception e) {
            System.err.println("Error scanning for test classes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return testClasses;
    }
    
    /**
     * Scans all classes accessible from the context class loader which belong to the given package.
     */
    private Class<?>[] getClasses(String packageName) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        java.util.Enumeration<java.net.URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        
        while (resources.hasMoreElements()) {
            java.net.URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        
        return classes.toArray(new Class[0]);
    }
    
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     */
    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        classes.add(Class.forName(className));
                    } catch (NoClassDefFoundError e) {
                        // Skip classes that can't be loaded
                        System.out.println("Warning: Could not load class " + className + ": " + e.getMessage());
                    }
                }
            }
        }
        return classes;
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
     * Generates a human-readable explanation of the test method logic
     * @param rawCode The raw code of the test method
     * @param methodName The name of the test method
     * @return A human-readable explanation of the test method logic
     */
    private String generateHumanReadableExplanation(String rawCode, String methodName) {
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
            // Add a general description based on the method name
            explanation.append("This test ");
            
            // Convert camelCase method name to readable format
            String readableMethodName = methodName.replace("test", "").replaceAll("([A-Z])", " $1").toLowerCase().trim();
            explanation.append(readableMethodName).append(".\n\n");
            
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
        
        return explanation.toString();
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
        dataModel.put("totalMethods", testClasses.stream()
                .mapToInt(tc -> tc.getTestMethods().size())
                .sum());
        
        try (Writer out = new FileWriter(new File(OUTPUT_DIR, "index.html"))) {
            template.process(dataModel, out);
        }
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
        private final String name;
        private final String description;

        public TestMethodInfo(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}

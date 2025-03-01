import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple standalone program to generate documentation for test methods
 */
public class RunDocGenerator {
    
    private static final String OUTPUT_DIR = "testng-docs";
    
    public static void main(String[] args) {
        try {
            // Create output directory
            Path outputPath = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            
            // Process test files
            processTestFile("src/test/java/com/testngdoc/sample/TCPrefixTests.java");
            processTestFile("src/test/java/com/testngdoc/sample/UnderscoreTests.java");
            
            // Generate index.html
            generateIndexHtml();
            
            System.out.println("Documentation generated in: " + OUTPUT_DIR);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void processTestFile(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        String content = new String(Files.readAllBytes(path));
        
        // Extract class name
        String className = path.getFileName().toString().replace(".java", "");
        
        // Extract package name
        Pattern packagePattern = Pattern.compile("package\\s+([\\w.]+);");
        Matcher packageMatcher = packagePattern.matcher(content);
        String packageName = packageMatcher.find() ? packageMatcher.group(1) : "default";
        
        // Find test methods
        Pattern methodPattern = Pattern.compile("@Test[\\s\\w\\(\\)]*\\s+public\\s+void\\s+(\\w+)\\s*\\(");
        Matcher methodMatcher = methodPattern.matcher(content);
        
        List<Map<String, String>> testMethods = new ArrayList<>();
        
        while (methodMatcher.find()) {
            String methodName = methodMatcher.group(1);
            String description = generateDescription(methodName);
            
            Map<String, String> method = new HashMap<>();
            method.put("name", methodName);
            method.put("description", description);
            testMethods.add(method);
        }
        
        // Generate HTML file
        generateClassHtml(className, packageName, testMethods);
    }
    
    private static String generateDescription(String methodName) {
        StringBuilder explanation = new StringBuilder();
        
        // Check for test case ID pattern (e.g., TC01_, TC02_, etc.)
        String testCaseId = null;
        Pattern tcPattern = Pattern.compile("^(TC\\d+)_(.*)$");
        Matcher matcher = tcPattern.matcher(methodName);
        
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
            
            if (testCaseId != null) {
                explanation.append(testCaseId).append("\n");
            }
            explanation.append(readableMethodName);
        } else {
            // For non-Gherkin style, also replace underscores with spaces
            readableMethodName = readableMethodName.replaceAll("_", " ");
            
            explanation.append("This test ");
            if (testCaseId != null) {
                explanation.append("(").append(testCaseId).append(") ");
            }
            explanation.append(readableMethodName).append(".");
        }
        
        return explanation.toString();
    }
    
    private static void generateClassHtml(String className, String packageName, List<Map<String, String>> testMethods) throws Exception {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>").append(className).append(" - TestNG Documentation</title>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; line-height: 1.6; }\n");
        html.append("        .container { max-width: 1200px; margin: 0 auto; }\n");
        html.append("        h1, h2 { color: #333; }\n");
        html.append("        .class-info { background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin-bottom: 20px; }\n");
        html.append("        .test-method { background-color: #fff; border: 1px solid #ddd; padding: 15px; margin-bottom: 15px; border-radius: 5px; }\n");
        html.append("        .method-name { font-weight: bold; font-size: 1.1em; margin-bottom: 10px; color: #0066cc; }\n");
        html.append("        .method-description { white-space: pre-wrap; }\n");
        html.append("        .back-link { margin-top: 20px; }\n");
        html.append("        a { color: #0066cc; text-decoration: none; }\n");
        html.append("        a:hover { text-decoration: underline; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <h1>").append(className).append("</h1>\n");
        html.append("        <div class=\"class-info\">\n");
        html.append("            <p><strong>Package:</strong> ").append(packageName).append("</p>\n");
        html.append("            <p><strong>Test Methods:</strong> ").append(testMethods.size()).append("</p>\n");
        html.append("        </div>\n");
        html.append("        <h2>Test Methods</h2>\n");
        
        for (Map<String, String> method : testMethods) {
            html.append("        <div class=\"test-method\">\n");
            html.append("            <div class=\"method-name\">").append(method.get("name")).append("</div>\n");
            html.append("            <div class=\"method-description\">").append(method.get("description")).append("</div>\n");
            html.append("        </div>\n");
        }
        
        html.append("        <div class=\"back-link\"><a href=\"index.html\">Back to Index</a></div>\n");
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>");
        
        Files.write(Paths.get(OUTPUT_DIR, className + ".html"), html.toString().getBytes());
    }
    
    private static void generateIndexHtml() throws Exception {
        File[] files = new File(OUTPUT_DIR).listFiles((dir, name) -> name.endsWith(".html") && !name.equals("index.html"));
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>TestNG Documentation</title>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; line-height: 1.6; }\n");
        html.append("        .container { max-width: 1200px; margin: 0 auto; }\n");
        html.append("        h1, h2 { color: #333; }\n");
        html.append("        table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n");
        html.append("        th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; }\n");
        html.append("        th { background-color: #f5f5f5; }\n");
        html.append("        tr:hover { background-color: #f9f9f9; }\n");
        html.append("        a { color: #0066cc; text-decoration: none; }\n");
        html.append("        a:hover { text-decoration: underline; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <h1>TestNG Documentation</h1>\n");
        html.append("        <div class=\"summary\">\n");
        html.append("            <p><strong>Total Test Classes:</strong> ").append(files != null ? files.length : 0).append("</p>\n");
        html.append("        </div>\n");
        html.append("        <h2>Test Classes</h2>\n");
        html.append("        <table>\n");
        html.append("            <tr>\n");
        html.append("                <th>Class Name</th>\n");
        html.append("            </tr>\n");
        
        if (files != null) {
            for (File file : files) {
                String className = file.getName().replace(".html", "");
                html.append("            <tr>\n");
                html.append("                <td><a href=\"").append(file.getName()).append("\">").append(className).append("</a></td>\n");
                html.append("            </tr>\n");
            }
        }
        
        html.append("        </table>\n");
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>");
        
        Files.write(Paths.get(OUTPUT_DIR, "index.html"), html.toString().getBytes());
    }
}

<!DOCTYPE html>
<html>
<head>
    <title>${className} - ${reportTitle}</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        :root {
            <#if darkMode>
            /* Dark Mode Colors */
            --primary-color: #ff6b6b;
            --secondary-color: #ffffff;
            --accent-color: #a5a5a5;
            --background-color: #121212;
            --card-bg-color: #1e1e1e;
            --text-color: #e1e1e1;
            --border-color: #333333;
            --success-color: #4ade80;
            --warning-color: #facc15;
            --error-color: #f87171;
            <#else>
            /* Light Mode Colors */
            --primary-color: #d52b1e;
            --secondary-color: #000000;
            --accent-color: #757575;
            --background-color: #ffffff;
            --card-bg-color: #ffffff;
            --text-color: #000000;
            --border-color: #e6e6e6;
            --success-color: #2d9d3a;
            --warning-color: #ffb400;
            --error-color: #d52b1e;
            </#if>
        }
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }
        body {
            font-family: 'Arial', 'Helvetica Neue', sans-serif;
            line-height: 1.5;
            color: var(--text-color);
            background-color: var(--background-color);
            padding: 15px;
            max-width: 1000px;
            margin: 0 auto;
            font-size: 14px;
        }
        header {
            background-color: var(--primary-color);
            color: white;
            padding: 15px;
            border-radius: 0;
            margin-bottom: 15px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        h1 {
            font-size: 1.6rem;
            margin-bottom: 5px;
            font-weight: 700;
        }
        h2 {
            font-size: 1.3rem;
            color: var(--secondary-color);
            margin: 15px 0 10px 0;
            font-weight: 600;
        }
        .report-subheader {
            font-size: 1.1rem;
            color: <#if darkMode>rgba(255, 255, 255, 0.8)<#else>rgba(0, 0, 0, 0.6)</#if>;
            margin: 5px 0 0 0;
            font-weight: 400;
            font-style: italic;
        }
        .class-info {
            background-color: var(--card-bg-color);
            padding: 15px;
            border-radius: 0;
            margin-bottom: 15px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            border-left: 3px solid var(--primary-color);
        }
        .method {
            background-color: var(--card-bg-color);
            padding: 15px;
            border-radius: 0;
            margin-bottom: 15px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            transition: transform 0.2s ease;
            border-left: 3px solid var(--accent-color);
        }
        .method:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .method h3 {
            font-size: 1.1rem;
            color: var(--secondary-color);
            margin-bottom: 10px;
            font-weight: 600;
        }
        .method h3 div {
            margin-bottom: 5px;
            line-height: 1.4;
        }
        .method h3 strong {
            color: var(--primary-color);
            font-weight: 700;
            margin-right: 5px;
        }
        .method-details {
            margin-top: 10px;
            font-size: 13px;
        }
        .method-details p {
            margin-bottom: 5px;
        }
        .method-details pre {
            background-color: <#if darkMode>#2a2a2a<#else>#f5f5f5</#if>;
            color: <#if darkMode>#e0e0e0<#else>#333333</#if>;
            padding: 10px;
            border-radius: 4px;
            overflow-x: auto;
            line-height: 1.5;
            font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
            border: 1px solid <#if darkMode>#444444<#else>#e0e0e0</#if>;
        }
        .tag {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 2px;
            font-size: 12px;
            font-weight: 600;
            margin-right: 5px;
            margin-bottom: 5px;
            color: white;
        }
        .tag.success {
            background-color: var(--success-color);
        }
        .tag.warning {
            background-color: var(--warning-color);
        }
        .tag.error {
            background-color: var(--error-color);
        }
        .tag.feature {
            background-color: #4285F4;
        }
        .tag.capability {
            background-color: #34A853;
        }
        .tag.api {
            background-color: #FBBC05;
            color: #333;
        }
        .tag.ui {
            background-color: #EA4335;
        }
        .tag.other {
            background-color: #9E9E9E;
        }
        .tags-container {
            margin-top: 5px;
            margin-bottom: 10px;
        }
        .method-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 5px;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 15px;
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 600;
        }
        .back-link:hover {
            text-decoration: underline;
        }
        .percentage {
            font-weight: 600;
            padding: 2px 6px;
            border-radius: 2px;
            color: white;
        }
        .percentage.high {
            background-color: var(--success-color);
        }
        .percentage.medium {
            background-color: var(--warning-color);
        }
        .percentage.low {
            background-color: var(--error-color);
        }
        .nav {
            margin-bottom: 15px;
        }
        .nav a {
            display: inline-block;
            padding: 8px 12px;
            background-color: var(--primary-color);
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.2s;
            font-size: 13px;
        }
        .nav a:hover {
            background-color: var(--secondary-color);
        }
        @media (max-width: 768px) {
            body {
                padding: 10px;
                font-size: 13px;
            }
            h1 {
                font-size: 1.4rem;
            }
            h2 {
                font-size: 1.2rem;
            }
            .method h3 {
                font-size: 1rem;
            }
        }
    </style>
</head>
<body>
    <header>
        <h1>${className}</h1>
        <#if reportHeader??>
        <h2 class="report-subheader">${reportHeader}</h2>
        </#if>
    </header>
    
    <div class="nav">
        <a href="index.html">Back to Index</a>
    </div>
    
    <div class="class-info">
        <div class="info-item">
            <span class="info-label">Package:</span> ${packageName}
        </div>
        <div class="info-item">
            <span class="info-label">Number of Test Methods:</span> ${testMethods?size}
        </div>
        <div class="info-item">
            <#assign percentValue = percentage?number>
            <#assign percentageClass = "">
            <#if percentValue gt 50>
                <#assign percentageClass = "high">
            <#elseif percentValue gt 25>
                <#assign percentageClass = "medium">
            <#else>
                <#assign percentageClass = "low">
            </#if>
            <span class="info-label">Percentage of Total:</span> <span class="percentage ${percentageClass}">${percentage}%</span>
        </div>
    </div>
    
    <h2>Test Methods</h2>
    <#list testMethods as method>
    <div class="method">
        <#-- Process method name to extract Gherkin-style components -->
        <#assign methodName = method.name>
        <#-- Remove "Test" suffix if present -->
        <#if methodName?ends_with("Test")>
            <#assign methodName = methodName?substring(0, methodName?length - 4)>
        </#if>
        
        <#-- Check if the method name contains Gherkin keywords -->
        <#if methodName?contains("given") || methodName?contains("when") || methodName?contains("then")>
            <#-- Format as Gherkin -->
            <h3>
                <#-- Process for "given" -->
                <#if methodName?contains("given")>
                    <#assign givenIndex = methodName?index_of("given")>
                    <#assign givenText = methodName?substring(givenIndex + 5)>
                    
                    <#-- Extract text up to the next keyword if present -->
                    <#if givenText?contains("when")>
                        <#assign whenIndex = givenText?index_of("when")>
                        <#assign givenText = givenText?substring(0, whenIndex)>
                    <#elseif givenText?contains("then")>
                        <#assign thenIndex = givenText?index_of("then")>
                        <#assign givenText = givenText?substring(0, thenIndex)>
                    </#if>
                    
                    <#-- Format camelCase to space-separated words -->
                    <#assign formattedGivenText = "">
                    <#list givenText?matches("[A-Z][a-z0-9]*|[a-z0-9]+") as word>
                        <#assign formattedGivenText = formattedGivenText + word + " ">
                    </#list>
                    <#if formattedGivenText?length gt 0>
                        <#assign formattedGivenText = formattedGivenText?substring(0, formattedGivenText?length-1)>
                    </#if>
                    
                    <div><strong>Given</strong> ${formattedGivenText?replace("_", " ")?capitalize}</div>
                </#if>
                
                <#-- Process for "when" -->
                <#if methodName?contains("when")>
                    <#assign whenIndex = methodName?index_of("when")>
                    <#assign whenText = methodName?substring(whenIndex + 4)>
                    
                    <#-- Extract text up to the next keyword if present -->
                    <#if whenText?contains("then")>
                        <#assign thenIndex = whenText?index_of("then")>
                        <#assign whenText = whenText?substring(0, thenIndex)>
                    </#if>
                    
                    <#-- Format camelCase to space-separated words -->
                    <#assign formattedWhenText = "">
                    <#list whenText?matches("[A-Z][a-z0-9]*|[a-z0-9]+") as word>
                        <#assign formattedWhenText = formattedWhenText + word + " ">
                    </#list>
                    <#if formattedWhenText?length gt 0>
                        <#assign formattedWhenText = formattedWhenText?substring(0, formattedWhenText?length-1)>
                    </#if>
                    
                    <div><strong>When</strong> ${formattedWhenText?replace("_", " ")?capitalize}</div>
                </#if>
                
                <#-- Process for "then" -->
                <#if methodName?contains("then")>
                    <#assign thenIndex = methodName?index_of("then")>
                    <#assign thenText = methodName?substring(thenIndex + 4)>
                    
                    <#-- Format camelCase to space-separated words -->
                    <#assign formattedThenText = "">
                    <#list thenText?matches("[A-Z][a-z0-9]*|[a-z0-9]+") as word>
                        <#assign formattedThenText = formattedThenText + word + " ">
                    </#list>
                    <#if formattedThenText?length gt 0>
                        <#assign formattedThenText = formattedThenText?substring(0, formattedThenText?length-1)>
                    </#if>
                    
                    <div><strong>Then</strong> ${formattedThenText?replace("_", " ")?capitalize}</div>
                </#if>
            </h3>
        <#else>
            <#-- Regular formatting for non-Gherkin method names -->
            <h3>${method.name}</h3>
        </#if>
        
        <div class="method-details">
            <#if method.tags?has_content>
            <div class="tags-container">
                <div class="method-tags">
                    <#list method.tags as tag>
                        <#assign tagClass = "other">
                        <#if tag?lower_case?contains("feature")>
                            <#assign tagClass = "feature">
                        <#elseif tag?lower_case?contains("capability")>
                            <#assign tagClass = "capability">
                        <#elseif tag?lower_case?contains("api")>
                            <#assign tagClass = "api">
                        <#elseif tag?lower_case?contains("ui")>
                            <#assign tagClass = "ui">
                        </#if>
                        <span class="tag ${tagClass}">${tag}</span>
                    </#list>
                </div>
            </div>
            </#if>
            <pre>${method.description}</pre>
        </div>
    </div>
    </#list>
</body>
</html>
<!DOCTYPE html>
<html>
<head>
    <title>${className} - ${reportTitle}</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        :root {
            <#if darkMode>
            --primary-color: #3a5a8c;
            --secondary-color: #2c4a7c;
            --accent-color: #4d7ab8;
            --background-color: #1e1e1e;
            --card-bg-color: #2d2d2d;
            --text-color: #e0e0e0;
            --border-color: #444444;
            --success-color: #28a745;
            --warning-color: #ffc107;
            --error-color: #dc3545;
            --code-bg-color: #2a2a2a;
            <#else>
            --primary-color: #4a6da7;
            --secondary-color: #304878;
            --accent-color: #5d8fdb;
            --background-color: #f8f9fa;
            --card-bg-color: #ffffff;
            --text-color: #333333;
            --border-color: #e1e4e8;
            --success-color: #28a745;
            --warning-color: #ffc107;
            --error-color: #dc3545;
            --code-bg-color: #f6f8fa;
            </#if>
        }
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: var(--text-color);
            background-color: var(--background-color);
            padding: 0;
            margin: 0;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        header {
            background-color: var(--primary-color);
            color: white;
            padding: 20px 0;
            margin-bottom: 30px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        h1 {
            font-size: 2.2rem;
            margin-bottom: 10px;
            color: white;
        }
        .header-note {
            font-size: 0.9rem;
            font-style: italic;
            color: rgba(255, 255, 255, 0.8);
            margin-top: 5px;
        }
        h2 {
            font-size: 1.8rem;
            margin: 25px 0 15px;
            color: var(--primary-color);
            border-bottom: 2px solid var(--border-color);
            padding-bottom: 10px;
        }
        a {
            color: var(--accent-color);
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        .nav {
            margin-top: 10px;
        }
        .nav a {
            color: white;
            opacity: 0.9;
            font-size: 0.9rem;
        }
        .nav a:hover {
            opacity: 1;
        }
        .info-panel {
            background-color: var(--card-bg-color);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
            border: 1px solid var(--border-color);
        }
        .info-panel p {
            margin-bottom: 10px;
        }
        .method {
            background-color: var(--card-bg-color);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
            border: 1px solid var(--border-color);
        }
        .method-name {
            font-weight: 600;
            color: var(--primary-color);
            font-size: 1.3rem;
            margin-bottom: 10px;
            padding-bottom: 8px;
            border-bottom: 1px solid var(--border-color);
        }
        .tags-container {
            margin: 10px 0;
        }
        .tag {
            display: inline-block;
            background-color: var(--accent-color);
            color: white;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 0.8rem;
            margin-right: 5px;
            margin-bottom: 5px;
        }
        .method-description {
            margin-top: 15px;
        }
        pre {
            background-color: var(--code-bg-color);
            color: var(--text-color);
            padding: 15px;
            border-radius: 6px;
            overflow-x: auto;
            line-height: 1.5;
            font-family: 'Consolas', 'Monaco', monospace;
            font-size: 0.9rem;
            border: 1px solid var(--border-color);
        }
        @media (max-width: 768px) {
            .container {
                padding: 15px;
            }
            h1 {
                font-size: 1.8rem;
            }
            h2 {
                font-size: 1.5rem;
            }
            .method {
                padding: 15px;
            }
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <h1>${className}</h1>
            <#if reportHeader??>
                <div class="header-note">${reportHeader}</div>
            </#if>
            <div class="nav">
                <a href="index.html">Back to Index</a>
            </div>
        </div>
    </header>
    <div class="container">
        <div class="info-panel">
            <p><strong>Package:</strong> ${packageName}</p>
            <p><strong>Number of Test Methods:</strong> ${testMethods?size}</p>
            <p><strong>Percentage of Total:</strong> ${percentage}%</p>
        </div>
        
        <h2>Test Methods</h2>
        <#list testMethods as method>
        <div class="method">
            <div class="method-name">${method.name}</div>
            <#if method.tags?? && method.tags?size gt 0>
            <div class="tags-container">
                <#list method.tags as tag>
                <span class="tag">${tag}</span>
                </#list>
            </div>
            </#if>
            <div class="method-description">
                <pre>${method.description}</pre>
            </div>
        </div>
        </#list>
    </div>
</body>
</html>

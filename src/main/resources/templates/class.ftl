<!DOCTYPE html>
<html>
<head>
    <title>${className} - ${reportTitle}</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        :root {
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
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        header .container {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        h1 {
            color: white;
            font-size: 2.2rem;
            font-weight: 600;
        }
        h2 {
            color: var(--secondary-color);
            font-size: 1.8rem;
            margin: 25px 0 15px 0;
            padding-bottom: 10px;
            border-bottom: 2px solid var(--accent-color);
        }
        .nav {
            margin-bottom: 20px;
        }
        .nav a {
            display: inline-block;
            padding: 8px 16px;
            background-color: var(--primary-color);
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s ease;
        }
        .nav a:hover {
            background-color: var(--secondary-color);
        }
        .info-panel {
            background-color: var(--card-bg-color);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }
        .info-panel p {
            margin: 10px 0;
            font-size: 1.1rem;
        }
        .info-panel strong {
            color: var(--secondary-color);
        }
        .method {
            background-color: var(--card-bg-color);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .method:hover {
            transform: translateY(-3px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .method-name {
            font-weight: 600;
            color: var(--primary-color);
            font-size: 1.3rem;
            margin-bottom: 10px;
            padding-bottom: 8px;
            border-bottom: 1px solid var(--border-color);
        }
        .method-description {
            margin-top: 15px;
        }
        .method-tags {
            margin-top: 10px;
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
        }
        .tag {
            display: inline-block;
            background-color: var(--accent-color);
            color: white;
            padding: 4px 10px;
            border-radius: 16px;
            font-size: 0.85rem;
            font-weight: 500;
        }
        pre {
            background-color: #f6f8fa;
            padding: 15px;
            border-radius: 6px;
            overflow-x: auto;
            line-height: 1.5;
            font-family: 'Consolas', 'Monaco', monospace;
            font-size: 0.9rem;
            border: 1px solid #e1e4e8;
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
        .dark-mode {
            --background-color: #333333;
            --text-color: #ffffff;
            --card-bg-color: #444444;
            --primary-color: #5d8fdb;
            --secondary-color: #4a6da7;
            --accent-color: #304878;
            --border-color: #555555;
            --success-color: #28a745;
            --warning-color: #ffc107;
            --error-color: #dc3545;
        }
        .dark-mode .info-panel {
            background-color: var(--card-bg-color);
            color: var(--text-color);
        }
        .dark-mode .method {
            background-color: var(--card-bg-color);
            color: var(--text-color);
        }
        .dark-mode .method-name {
            color: var(--primary-color);
        }
        .dark-mode .method-description {
            color: var(--text-color);
        }
        .dark-mode .method-tags {
            color: var(--text-color);
        }
        .dark-mode .tag {
            background-color: var(--accent-color);
            color: var(--text-color);
        }
        .dark-mode pre {
            background-color: #444444;
            color: var(--text-color);
            border: 1px solid #555555;
        }
    </style>
</head>
<body<#if darkMode> class="dark-mode"</#if>>
    <header>
        <div class="container">
            <h1>${className}</h1>
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
            <div class="method-description">
                <pre>${method.description}</pre>
            </div>
            <#if method.tags?? && method.tags?size gt 0>
            <div class="method-tags">
                <#list method.tags as tag>
                <span class="tag">${tag}</span>
                </#list>
            </div>
            </#if>
        </div>
        </#list>
    </div>
</body>
</html>
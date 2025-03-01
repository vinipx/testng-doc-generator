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
            border: 1px solid var(--border-color);
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 30px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }
        .info-panel p {
            margin-bottom: 10px;
        }
        .info-panel p:last-child {
            margin-bottom: 0;
        }
        .method {
            background-color: var(--card-bg-color);
            border: 1px solid var(--border-color);
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }
        .method-name {
            font-weight: 600;
            font-size: 1.2rem;
            color: var(--primary-color);
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 1px solid var(--border-color);
        }
        .method-description {
            color: var(--text-color);
        }
        .method-description pre {
            white-space: pre-wrap;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            font-size: 1rem;
            line-height: 1.6;
            background-color: transparent;
            padding: 0;
            margin: 0;
            border: none;
        }
        .tag {
            display: inline-block;
            padding: 4px 8px;
            margin: 2px;
            background-color: var(--accent-color);
            color: white;
            border-radius: 4px;
            font-size: 0.85rem;
        }
        .tags-container {
            margin-top: 10px;
            margin-bottom: 15px;
        }
        footer {
            text-align: center;
            margin-top: 40px;
            padding: 20px 0;
            color: #666;
            font-size: 0.9rem;
            border-top: 1px solid #e1e4e8;
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

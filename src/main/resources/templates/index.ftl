<!DOCTYPE html>
<html>
<head>
    <title>${reportTitle}</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* Import Inter font */
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');
        
        /* CSS Variables */
        :root {
            --primary-color: #3f51b5;
            --secondary-color: #303f9f;
            --accent-color: #7986cb;
            --background-color: #fafafa;
            --card-bg-color: #ffffff;
            --text-color: #424242;
            --border-color: #e0e0e0;
            --success-color: #4caf50;
            --warning-color: #ff9800;
            --error-color: #f44336;
        }
        
        /* Dark mode variables */
        .dark-mode {
            --primary-color: #5c6bc0;
            --secondary-color: #3949ab;
            --accent-color: #9fa8da;
            --background-color: #121212;
            --card-bg-color: #1e1e1e;
            --text-color: #e0e0e0;
            --border-color: #333333;
            --success-color: #66bb6a;
            --warning-color: #ffa726;
            --error-color: #ef5350;
        }
        
        /* Base styles */
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }
        
        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
            line-height: 1.6;
            color: var(--text-color);
            background-color: var(--background-color);
            padding: 0;
            margin: 0;
            font-weight: 400;
            -webkit-font-smoothing: antialiased;
            -moz-osx-font-smoothing: grayscale;
        }
        
        .dark-mode body {
            font-weight: 300;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        header {
            background-color: var(--primary-color);
            color: white;
            padding: 24px 0;
            margin-bottom: 40px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        }
        
        h1 {
            font-size: 2.2rem;
            margin-bottom: 10px;
            color: white;
            font-weight: 600;
            letter-spacing: -0.5px;
        }
        
        h2 {
            font-size: 1.8rem;
            margin: 30px 0 20px;
            color: var(--primary-color);
            border-bottom: 2px solid var(--border-color);
            padding-bottom: 10px;
            font-weight: 500;
        }
        
        h3 {
            font-size: 1.4rem;
            margin: 20px 0 12px;
            color: var(--primary-color);
            font-weight: 500;
        }
        
        a {
            color: var(--primary-color);
            text-decoration: none;
            transition: color 0.2s ease, border-bottom 0.2s ease;
            border-bottom: 1px solid transparent;
        }
        
        a:hover {
            text-decoration: none;
            color: var(--accent-color);
            border-bottom: 1px solid var(--accent-color);
        }
        
        .summary-container {
            display: flex;
            flex-wrap: wrap;
            gap: 24px;
            margin-bottom: 40px;
        }
        
        .summary {
            background-color: var(--card-bg-color);
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
            border: none;
            flex: 1;
            min-width: 300px;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        
        .summary:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 24px 0;
            background-color: var(--card-bg-color);
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
        }
        
        th {
            background-color: var(--secondary-color);
            color: white;
            text-align: left;
            padding: 16px 20px;
            font-weight: 500;
            font-size: 0.95rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        td {
            padding: 14px 20px;
            border-top: 1px solid var(--border-color);
        }
        
        tr:hover {
            background-color: rgba(0, 0, 0, 0.02);
        }
        
        .dark-mode tr:hover {
            background-color: rgba(255, 255, 255, 0.03);
        }
        
        .chart-container {
            background-color: var(--card-bg-color);
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
            border: none;
            flex: 1;
            min-width: 300px;
            max-width: 500px;
            height: 100%;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        
        .chart-container:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
        }
        
        .method {
            background-color: var(--card-bg-color);
            border: none;
            border-radius: 12px;
            padding: 24px;
            margin-bottom: 24px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        
        .method:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
        }
        
        .chart-title {
            text-align: center;
            margin-bottom: 20px;
            color: var(--primary-color);
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
            table {
                display: block;
                overflow-x: auto;
            }
            th, td {
                padding: 8px 10px;
            }
            .chart-container {
                max-width: 100%;
            }
        }
    </style>
    <#if displayTagsChart>
    <#assign tagCounts = {}>
    <#list testClasses as class>
        <#list class.testMethods as method>
            <#list method.tags as tag>
                <#if tagCounts[tag]??>
                    <#assign tagCounts = tagCounts + {tag: tagCounts[tag] + 1}>
                <#else>
                    <#assign tagCounts = tagCounts + {tag: 1}>
                </#if>
            </#list>
        </#list>
    </#list>
    </#if>
</head>
<body<#if darkMode> class="dark-mode"</#if>>
    <header>
        <div class="container">
            <h1>${reportTitle}</h1>
            <#if reportHeader??>
            <div class="header-note">${reportHeader}</div>
            </#if>
        </div>
    </header>
    <div class="container">
        <div class="summary-container">
            <div class="summary">
                <h3>Summary</h3>
                <p><strong>Total Test Classes:</strong> ${testClasses?size}</p>
                <p><strong>Total Test Methods:</strong> ${totalMethods}</p>
            </div>
            
            <#if displayTagsChart && svgChart??>
            <div class="chart-container">
                <h3 class="chart-title">Test Tags Distribution</h3>
                ${svgChart}
            </div>
            </#if>
        </div>
        
        <h2>Test Classes</h2>
        <table>
            <tr>
                <th>Class Name</th>
                <th>Package</th>
                <th>Test Methods</th>
                <th>Percentage</th>
            </tr>
            <#list testClasses as class>
            <tr>
                <td><a href="${class.className}.html">${class.className}</a></td>
                <td>${class.packageName}</td>
                <td>${class.testMethods?size}</td>
                <td>${class.percentage}%</td>
            </tr>
            </#list>
        </table>
    </div>
</body>
</html>
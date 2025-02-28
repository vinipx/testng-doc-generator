<!DOCTYPE html>
<html>
<head>
    <title>TestNG Documentation</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        :root {
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
        .summary {
            background-color: var(--card-bg-color);
            border-radius: 0;
            padding: 15px;
            margin-bottom: 15px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            border-left: 3px solid var(--primary-color);
        }
        .summary-item {
            margin-bottom: 8px;
            font-size: 14px;
        }
        .summary-label {
            font-weight: 600;
            color: var(--secondary-color);
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
            background-color: var(--card-bg-color);
            border-radius: 0;
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            font-size: 13px;
        }
        th {
            background-color: var(--secondary-color);
            color: white;
            text-align: left;
            padding: 10px;
            font-weight: 600;
            font-size: 12px;
            text-transform: uppercase;
        }
        td {
            padding: 10px;
            border-bottom: 1px solid var(--border-color);
        }
        tr:last-child td {
            border-bottom: none;
        }
        tr:nth-child(even) {
            background-color: rgba(0, 0, 0, 0.02);
        }
        tr:hover {
            background-color: rgba(0, 0, 0, 0.02);
        }
        a {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 600;
        }
        a:hover {
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
            th, td {
                padding: 8px;
            }
            .summary {
                padding: 12px;
            }
        }
    </style>
</head>
<body>
    <header>
        <h1>TestNG Documentation</h1>
    </header>
    
    <div class="summary">
        <h2>Summary</h2>
        <div class="summary-item">
            <span class="summary-label">Total Test Classes:</span> ${testClasses?size}
        </div>
        <div class="summary-item">
            <span class="summary-label">Total Test Methods:</span> ${totalMethods}
        </div>
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
            <td>
                <#assign percentValue = class.percentage?number>
                <#if percentValue gt 50>
                    <span class="percentage high">${class.percentage}%</span>
                <#elseif percentValue gt 25>
                    <span class="percentage medium">${class.percentage}%</span>
                <#else>
                    <span class="percentage low">${class.percentage}%</span>
                </#if>
            </td>
        </tr>
        </#list>
    </table>
</body>
</html>
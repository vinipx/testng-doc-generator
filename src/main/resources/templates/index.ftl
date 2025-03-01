<!DOCTYPE html>
<html>
<head>
    <title>${reportTitle}</title>
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
        h3 {
            font-size: 1.4rem;
            margin: 20px 0 10px;
            color: var(--primary-color);
        }
        p {
            margin-bottom: 15px;
        }
        a {
            color: var(--accent-color);
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        .summary {
            background-color: var(--card-bg-color);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
            border: 1px solid var(--border-color);
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
            background-color: var(--card-bg-color);
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
        }
        th {
            background-color: var(--secondary-color);
            color: white;
            text-align: left;
            padding: 12px 15px;
        }
        td {
            padding: 10px 15px;
            border-top: 1px solid var(--border-color);
        }
        tr:hover {
            background-color: rgba(0, 0, 0, 0.05);
        }
        .chart-container {
            margin: 30px 0;
            background-color: var(--card-bg-color);
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
            border: 1px solid var(--border-color);
        }
        .chart-title {
            text-align: center;
            margin-bottom: 20px;
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
        }
    </style>
    <#if displayTagsChart>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    </#if>
</head>
<body>
    <header>
        <div class="container">
            <h1>${reportTitle}</h1>
            <#if reportHeader??>
                <div class="header-note">${reportHeader}</div>
            </#if>
        </div>
    </header>
    <div class="container">
        <div class="summary">
            <h2>Summary</h2>
            <p><strong>Total Test Classes:</strong> ${testClasses?size}</p>
            <p><strong>Total Test Methods:</strong> ${totalMethods}</p>
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
        
        <#if displayTagsChart && tagPercentages?? && tagPercentages?size gt 0>
        <div class="chart-container">
            <h3 class="chart-title">Test Tags Distribution</h3>
            <canvas id="tagsChart" width="400" height="400"></canvas>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                var ctx = document.getElementById('tagsChart').getContext('2d');
                var tagsChart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                        labels: [<#list tagPercentages?keys as tag>'${tag}',</#list>],
                        datasets: [{
                            data: [<#list tagPercentages?values as value>${value},</#list>],
                            backgroundColor: [
                                '#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b',
                                '#5a5c69', '#858796', '#6f42c1', '#20c9a6', '#f8f9fc',
                                '#3a5a8c', '#2c4a7c', '#4d7ab8', '#28a745', '#ffc107'
                            ]
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'right',
                                labels: {
                                    color: '<#if darkMode>#e0e0e0<#else>#333333</#if>'
                                }
                            }
                        }
                    }
                });
            });
        </script>
        </#if>
    </div>
</body>
</html>

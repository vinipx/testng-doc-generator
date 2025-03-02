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
        .summary-container {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-between;
        }
        .summary {
            flex-basis: 40%;
        }
        .chart-container {
            flex-basis: 55%;
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
            .summary-container {
                flex-direction: column;
            }
            .summary {
                margin-bottom: 20px;
            }
            .chart-container {
                margin-top: 0;
            }
        }
    </style>
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
        <div class="summary-container">
            <div class="summary">
                <h2>Summary</h2>
                <p><strong>Total Test Classes:</strong> ${testClasses?size}</p>
                <p><strong>Total Test Methods:</strong> ${totalMethods}</p>
            </div>
            
            <#if displayTagsChart && tagPercentages?? && tagPercentages?size gt 0>
            <div class="chart-container">
                <h3 class="chart-title">Test Tags Distribution</h3>
                <canvas id="tagsChart" width="400" height="300"></canvas>
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
        
        <#if displayTagsChart && tagPercentages?? && tagPercentages?size gt 0>
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
                                <#if darkMode>
                                '#5c6bc0', '#7986cb', '#9fa8da', '#c5cae9', '#8c9eff', '#536dfe', '#3d5afe', '#304ffe',
                                '#6200ea', '#651fff', '#7c4dff', '#b388ff', '#d500f9', '#e040fb', '#ea80fc', '#f50057'
                                <#else>
                                '#3f51b5', '#5c6bc0', '#7986cb', '#9fa8da', '#3949ab', '#303f9f', '#283593', '#1a237e',
                                '#8c9eff', '#536dfe', '#3d5afe', '#304ffe', '#6200ea', '#651fff', '#7c4dff', '#b388ff'
                                </#if>
                            ],
                            borderColor: [
                                <#if darkMode>
                                '#121212', '#121212', '#121212', '#121212', '#121212', '#121212', '#121212', '#121212',
                                '#121212', '#121212', '#121212', '#121212', '#121212', '#121212', '#121212', '#121212'
                                <#else>
                                '#ffffff', '#ffffff', '#ffffff', '#ffffff', '#ffffff', '#ffffff', '#ffffff', '#ffffff',
                                '#ffffff', '#ffffff', '#ffffff', '#ffffff', '#ffffff', '#ffffff', '#ffffff', '#ffffff'
                                </#if>
                            ],
                            borderWidth: 2
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: true,
                        plugins: {
                            legend: {
                                position: 'right',
                                labels: {
                                    color: '<#if darkMode>#e0e0e0<#else>#424242</#if>',
                                    font: {
                                        family: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
                                        size: 12
                                    },
                                    padding: 15
                                }
                            },
                            tooltip: {
                                backgroundColor: '<#if darkMode>#2d2d2d<#else>#ffffff</#if>',
                                titleColor: '<#if darkMode>#e0e0e0<#else>#424242</#if>',
                                bodyColor: '<#if darkMode>#e0e0e0<#else>#424242</#if>',
                                borderColor: '<#if darkMode>#444444<#else>#e0e0e0</#if>',
                                borderWidth: 1,
                                padding: 12,
                                cornerRadius: 8,
                                titleFont: {
                                    family: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
                                    size: 14,
                                    weight: 'bold'
                                },
                                bodyFont: {
                                    family: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
                                    size: 13
                                },
                                displayColors: true,
                                boxPadding: 5
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
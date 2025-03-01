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
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        header .container {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }
        h1 {
            color: white;
            font-size: 2.2rem;
            font-weight: 600;
            text-align: center;
        }
        h2 {
            color: var(--secondary-color);
            font-size: 1.8rem;
            margin: 25px 0 15px 0;
            padding-bottom: 10px;
            border-bottom: 2px solid var(--accent-color);
        }
        .summary {
            background-color: var(--card-bg-color);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }
        .summary p {
            margin: 10px 0;
            font-size: 1.1rem;
        }
        .summary strong {
            color: var(--secondary-color);
        }
        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            margin: 20px 0;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid var(--border-color);
        }
        th {
            background-color: var(--primary-color);
            color: white;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.9rem;
            letter-spacing: 0.5px;
        }
        tr:nth-child(even) {
            background-color: rgba(0, 0, 0, 0.02);
        }
        tr:hover {
            background-color: rgba(0, 0, 0, 0.05);
        }
        td:last-child, th:last-child {
            text-align: center;
        }
        a {
            color: var(--accent-color);
            text-decoration: none;
            font-weight: 500;
            transition: color 0.3s ease;
        }
        a:hover {
            color: var(--secondary-color);
            text-decoration: underline;
        }
        .chart-container {
            width: 100%;
            max-width: 600px;
            margin: 30px auto;
            background-color: var(--card-bg-color);
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }
        .chart-title {
            text-align: center;
            margin-bottom: 20px;
            color: var(--secondary-color);
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
                <h2 style="color: white; border-bottom: none;">${reportHeader}</h2>
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
            <canvas id="tagsChart"></canvas>
        </div>
        
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const ctx = document.getElementById('tagsChart').getContext('2d');
                
                // Extract data from the template
                const labels = [
                    <#list tagPercentages?keys as tag>
                    "${tag}"<#if tag_has_next>,</#if>
                    </#list>
                ];
                
                const data = [
                    <#list tagPercentages?keys as tag>
                    ${tagPercentages[tag]}<#if tag_has_next>,</#if>
                    </#list>
                ];
                
                // Generate random colors
                const backgroundColors = labels.map(() => {
                    const r = Math.floor(Math.random() * 200) + 55;
                    const g = Math.floor(Math.random() * 200) + 55;
                    const b = Math.floor(Math.random() * 200) + 55;
                    return `rgba(${r}, ${g}, ${b}, 0.7)`;
                });
                
                const borderColors = backgroundColors.map(color => {
                    return color.replace('0.7', '1');
                });
                
                // Create the chart
                new Chart(ctx, {
                    type: 'pie',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: data,
                            backgroundColor: backgroundColors,
                            borderColor: borderColors,
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                position: 'right',
                                labels: {
                                    color: '<#if darkMode>#e0e0e0<#else>#333333</#if>'
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const label = context.label || '';
                                        const value = context.raw || 0;
                                        return `${label}: ${value}%`;
                                    }
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

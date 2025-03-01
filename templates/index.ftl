<!DOCTYPE html>
<html>
<head>
    <title>${reportTitle}</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
        .summary-container {
            display: flex;
            gap: 20px;
            margin-bottom: 20px;
        }
        .summary {
            background-color: var(--card-bg-color);
            padding: 15px;
            border-radius: 0;
            margin-bottom: 0;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            border-left: 3px solid var(--primary-color);
            flex: 1;
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
        .charts-container {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            margin-bottom: 20px;
        }
        .chart-card {
            background-color: var(--card-bg-color);
            padding: 10px;
            border-radius: 0;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            border-left: 3px solid var(--accent-color);
            flex: 1;
            min-width: 200px;
            max-width: 400px;
        }
        .chart-card h3 {
            font-size: 1rem;
            margin-bottom: 8px;
        }
        .chart-container {
            position: relative;
            height: 150px;
            width: 100%;
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
        <h1>${reportTitle}</h1>
        <#if reportHeader??>
        <h2 class="report-subheader">${reportHeader}</h2>
        </#if>
    </header>
    
    <div class="summary-container">
        <div class="summary">
            <h2>Summary</h2>
            <div class="summary-item">
                <span class="summary-label">Total Test Classes:</span> ${testClasses?size}
            </div>
            <div class="summary-item">
                <span class="summary-label">Total Test Methods:</span> ${totalMethods}
            </div>
        </div>
        
        <#if displayTagsChart && tagStats?has_content>
        <div class="chart-card">
            <h3>Test Tags Distribution</h3>
            <div class="chart-container">
                <canvas id="tagsChart"></canvas>
            </div>
        </div>
        </#if>
    </div>
    
    <#if displayTagsChart && tagStats?has_content>
    <script>
        // Create tag statistics chart
        document.addEventListener('DOMContentLoaded', function() {
            const ctx = document.getElementById('tagsChart').getContext('2d');
            
            // Generate random colors for each tag
            function generateColors(count) {
                const colors = [];
                const baseColors = [
                    'rgba(66, 133, 244, 0.8)',   // Blue
                    'rgba(52, 168, 83, 0.8)',    // Green
                    'rgba(251, 188, 5, 0.8)',    // Yellow
                    'rgba(234, 67, 53, 0.8)',    // Red
                    'rgba(158, 158, 158, 0.8)',  // Gray
                    'rgba(103, 58, 183, 0.8)',   // Purple
                    'rgba(0, 188, 212, 0.8)',    // Cyan
                    'rgba(255, 152, 0, 0.8)',    // Orange
                    'rgba(233, 30, 99, 0.8)',    // Pink
                    'rgba(0, 150, 136, 0.8)',    // Teal
                ];
                
                for (let i = 0; i < count; i++) {
                    colors.push(baseColors[i % baseColors.length]);
                }
                
                return colors;
            }
            
            // Get tag data
            const labels = [
                <#list tagStats?keys as tag>
                '${tag}',
                </#list>
            ];
            
            const data = [
                <#list tagStats?keys as tag>
                ${tagStats[tag]},
                </#list>
            ];
            
            // Create chart
            new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        data: data,
                        backgroundColor: generateColors(labels.length),
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'right'
                        },
                        tooltip: {
                            callbacks: {
                                label: function(tooltipItem) {
                                    const dataset = tooltipItem.dataset;
                                    const index = tooltipItem.dataIndex;
                                    const value = dataset.data[index] || 0;
                                    const label = tooltipItem.chart.data.labels[index];
                                    const total = dataset.data.reduce((a, b) => a + b, 0);
                                    const percentage = Math.round((value / total) * 1000) / 10;
                                    return `${'$'}{label}: ${'$'}{value} (${'$'}{percentage}%)`;
                                }
                            }
                        }
                    }
                }
            });
        });
    </script>
    </#if>
    
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
# PowerShell script to parse JMH JSON results into readable markdown

param(
    [string]$InputFile = "benchmark-results.json",
    [string]$OutputFile = "benchmark-results.md"
)

Write-Host "Parsing JMH results from $InputFile..." -ForegroundColor Cyan

if (-not (Test-Path $InputFile)) {
    Write-Host "Error: $InputFile not found!" -ForegroundColor Red
    Write-Host "Run the benchmarks first to generate results." -ForegroundColor Yellow
    exit 1
}

# Read JSON
$results = Get-Content $InputFile | ConvertFrom-Json

# Create markdown output
$markdown = @"
# JMH Benchmark Results
**Generated**: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

---

## Summary

Total benchmarks: $($results.Count)

---

## Detailed Results

"@

# Group by benchmark class
$grouped = $results | Group-Object { $_.benchmark.Split('.')[3] }

foreach ($group in $grouped) {
    $className = $group.Name
    $markdown += "`n### $className`n`n"
    $markdown += "| Benchmark | Parameters | Mode | Score | Error | Units |`n"
    $markdown += "|-----------|------------|------|-------|-------|-------|`n"
    
    foreach ($result in $group.Group | Sort-Object benchmark) {
        $benchmarkName = $result.benchmark.Split('.')[-1]
        
        # Extract parameters
        $params = ""
        if ($result.params) {
            $paramList = @()
            $result.params.PSObject.Properties | ForEach-Object {
                $paramList += "$($_.Name)=$($_.Value)"
            }
            $params = $paramList -join ", "
        }
        
        # Get primary metric
        $score = [math]::Round($result.primaryMetric.score, 3)
        $errorMargin = ""
        if ($result.primaryMetric.scoreError) {
            $errorMargin = "± $([math]::Round($result.primaryMetric.scoreError, 3))"
        }
        $unit = $result.primaryMetric.scoreUnit
        $mode = $result.mode
        
        $markdown += "| $benchmarkName | $params | $mode | $score | $errorMargin | $unit |`n"
    }
}

$markdown += "`n---`n`n"
$markdown += "## Performance Insights`n`n"
$markdown += "*Add your analysis here after reviewing the results*`n"

# Save to file
$markdown | Out-File -FilePath $OutputFile -Encoding UTF8

Write-Host "Results saved to $OutputFile" -ForegroundColor Green
Write-Host "You can review the formatted results in: $OutputFile" -ForegroundColor Cyan

# Script to run ALL JMH Benchmarks

Write-Host "===========================================================" -ForegroundColor Cyan
Write-Host "  Running ALL JMH Benchmarks (Cart, Order, ProductModelDM) " -ForegroundColor Cyan
Write-Host "===========================================================" -ForegroundColor Cyan
Write-Host ""

# Clean up any existing JMH lock file from previous runs
$lockFile = Join-Path $env:TEMP "jmh.lock"
if (Test-Path $lockFile) {
    Write-Host "Cleaning up previous JMH lock file..." -ForegroundColor Yellow
    Remove-Item -Path $lockFile -Force -ErrorAction SilentlyContinue
}


# Step 1: Copy dependencies to target/dependency folder
Write-Host "[1/2] Copying dependencies..." -ForegroundColor Yellow
mvn dependency:copy-dependencies -DincludeScope=test -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to copy dependencies" -ForegroundColor Red
    exit 1
}

Write-Host "Dependencies copied successfully" -ForegroundColor Green
Write-Host ""

# Step 2: Run ALL benchmarks (no specific class specified)
Write-Host "[2/2] Executing ALL benchmarks..." -ForegroundColor Yellow
Write-Host "This will run:" -ForegroundColor Gray
Write-Host "  - CartBenchmark (6 variations)" -ForegroundColor Gray
Write-Host "  - OrderProcessingBenchmark (18 variations)" -ForegroundColor Gray
Write-Host "  - ProductModelDMBenchmark (54 variations)" -ForegroundColor Gray
Write-Host "  Total: 78 benchmark variations" -ForegroundColor Gray
Write-Host ""
Write-Host "Estimated time: ~10-15 minutes" -ForegroundColor Yellow
Write-Host ""

# Run without specifying a class to execute ALL benchmarks
java -cp "target/test-classes;target/classes;target/dependency/*" com.vaporant.benchmark.BenchmarkRunner

if ($LASTEXITCODE -eq 0) {
    Write-Host "" 
    Write-Host "===========================================================" -ForegroundColor Green
    Write-Host "  ALL Benchmarks completed successfully!" -ForegroundColor Green
    Write-Host "===========================================================" -ForegroundColor Green
    Write-Host ""
    
    # Parse results into readable markdown
    Write-Host "Generating readable report..." -ForegroundColor Yellow
    & "$PSScriptRoot\parse_benchmark_results.ps1" -InputFile "benchmark-results.json" -OutputFile "benchmark-results.md"
    
    Write-Host ""
    Write-Host "Results available in:" -ForegroundColor Cyan
    Write-Host "  - benchmark-results.json (raw JMH data)" -ForegroundColor Gray
    Write-Host "  - benchmark-results.md (formatted report)" -ForegroundColor Gray
} else {
    Write-Host ""
    Write-Host "===========================================================" -ForegroundColor Red
    Write-Host "  Benchmarks failed" -ForegroundColor Red
    Write-Host "===========================================================" -ForegroundColor Red
    exit 1
}

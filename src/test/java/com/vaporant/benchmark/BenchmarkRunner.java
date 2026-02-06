package com.vaporant.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Simple runner class to execute JMH benchmarks WITHOUT forking
 * 
 * IMPORTANT: forks(0) disables forking to work with Maven exec plugin
 * This means benchmarks run in the same JVM (less isolated but functional)
 * 
 * Usage from command line:
 * mvn test-compile exec:java
 * -Dexec.mainClass="com.vaporant.benchmark.BenchmarkRunner"
 * -Dexec.classpathScope=test
 * 
 * Or run specific benchmark:
 * mvn test-compile exec:java
 * -Dexec.mainClass="com.vaporant.benchmark.BenchmarkRunner"
 * -Dexec.args="CartBenchmark" -Dexec.classpathScope=test
 */
public class BenchmarkRunner {

    public static void main(String[] args) throws RunnerException {
        Options opt;

        if (args.length > 0) {
            // Run specific benchmark
            opt = new OptionsBuilder()
                    .include(args[0])
                    .forks(0) // CRITICAL: No forking - run in same JVM (required for Maven exec)
                    .jvmArgs("-Djmh.ignoreLock=true") // Bypass lock file check
                    // Save results to files for analysis
                    .resultFormat(org.openjdk.jmh.results.format.ResultFormatType.JSON)
                    .result("benchmark-results.json")
                    .build();
        } else {
            // Run all benchmarks in package
            opt = new OptionsBuilder()
                    .include("com.vaporant.benchmark")
                    .forks(0) // CRITICAL: No forking - run in same JVM (required for Maven exec)
                    .jvmArgs("-Djmh.ignoreLock=true") // Bypass lock file check
                    // Save results to files for analysis
                    .resultFormat(org.openjdk.jmh.results.format.ResultFormatType.JSON)
                    .result("benchmark-results.json")
                    .build();
        }

        new Runner(opt).run();
    }
}

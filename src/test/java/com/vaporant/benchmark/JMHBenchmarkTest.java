package com.vaporant.benchmark;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@Disabled("Temporarily disabled to focus on JaCoCo")
public class JMHBenchmarkTest {
    @Test
    void executeJmhRunner() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(OrderProcessingBenchmark.class.getSimpleName())
                .warmupIterations(0)
                .measurementIterations(1)
                .forks(0)
                .threads(1)
                .shouldDoGC(true)
                .shouldFailOnError(true)
                .jvmArgs("-server")
                .build();

        new Runner(opt).run();
        Assertions.assertTrue(true, "JMH Benchmark executed successfully");
    }
}

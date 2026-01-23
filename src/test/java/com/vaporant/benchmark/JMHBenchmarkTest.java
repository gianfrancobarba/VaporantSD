package com.vaporant.benchmark;

import org.junit.jupiter.api.Test;

public class JMHBenchmarkTest {
    @Test
    public void runBenchmarks() throws Exception {
        // Run benchmarks via the existing runner
        BenchmarkRunner.main(new String[] {});
    }
}

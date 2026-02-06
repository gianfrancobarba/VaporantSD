package com.vaporant.benchmark;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Temporarily disabled to focus on JaCoCo")
public class JMHBenchmarkTest {
    @Test
    public void runBenchmarks() throws Exception {
        // Run benchmarks via the existing runner
        BenchmarkRunner.main(new String[] {});
    }
}

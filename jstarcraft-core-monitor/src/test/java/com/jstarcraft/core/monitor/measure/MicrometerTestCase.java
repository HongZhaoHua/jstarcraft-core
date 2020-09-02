package com.jstarcraft.core.monitor.measure;

import org.junit.Test;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class MicrometerTestCase {

    @Test
    public void test() throws Exception {
        CompositeMeterRegistry complexRegistry = new CompositeMeterRegistry();
        // 选择3种注册表,以覆盖类别,聚合和发布的各种情况
        SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();
        complexRegistry.add(simpleMeterRegistry);
        JmxMeterRegistry jmxRegistry = new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM);
        complexRegistry.add(jmxRegistry);
        PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        complexRegistry.add(prometheusRegistry);
    }

}

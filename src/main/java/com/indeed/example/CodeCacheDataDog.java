package com.indeed.example;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

public class CodeCacheDataDog {
    public static class Sampler implements Runnable {
        final StatsDClient client;
        final MemoryPoolMXBean codeCacheMXBean;

        public Sampler(final String prefix, final String hostname, final int port) {
            this.client = new NonBlockingStatsDClient(prefix, hostname, port);
            final List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
            for (final MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
                if ("Code Cache".equals(memoryPoolMXBean.getName())) {
                    codeCacheMXBean = memoryPoolMXBean;
                    return;
                }
            }

            // Shouldn't get here unless a newer Java release renames Code Cache
            throw new RuntimeException("Did not find Code Cache memory pool.");
        }

        public void run() {
            final MemoryUsage usage = codeCacheMXBean.getUsage();
            client.recordGaugeValue("codeCache.used", (double)usage.getUsed());
            client.recordGaugeValue("codeCache.max", (double)usage.getMax());
        }            
    }

    public static void main(String[] args) throws InterruptedException {
        final String hostname = args[0];
        final int port = Integer.parseInt(args[1]);

        final Sampler sampler = new Sampler("example", hostname, port);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(sampler, 0, 30, TimeUnit.SECONDS);        
    }
}

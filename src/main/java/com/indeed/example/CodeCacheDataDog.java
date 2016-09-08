package com.indeed.example;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

public class CodeCacheDataDog {
    public static void main(String[] args) throws InterruptedException {
	final String hostname = args[0];
	final int port = Integer.parseInt(args[1]);

        final StatsDClient client = new NonBlockingStatsDClient("example", hostname, port);

	MemoryPoolMXBean codeCacheMXBean = null;
	final List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
	for (final MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
	    if ("Code Cache".equals(memoryPoolMXBean.getName())) {
		codeCacheMXBean = memoryPoolMXBean;
		break;
	    }
	}

	while (true) {
	    final MemoryUsage usage = codeCacheMXBean.getUsage();
	    client.recordGaugeValue("codeCache.used", (double)usage.getUsed());
	    client.recordGaugeValue("codeCache.max", (double)usage.getMax());
	    Thread.sleep(30000);
	}
    }

}

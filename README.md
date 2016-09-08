Introduction
============

This is an example of how to upload Java codecache statistics to Datadog.  It uploads two values:
* example.codeCache.used: number of bytes currently used in the codecache
* example.codeCache.max: codecache maximum size

Build Instructions
==================

$ mvn package


Run Instructions
================

You need a Datadog account, and the Datadog agent running on your host.

Run the example like this:

$ mvn exec:java -Dexec.mainClass=com.indeed.example.CodeCacheDataDog -Dexec.args="localhost 8125"

The Datadog agent consists of three parts: the collector, dogstatsd, and forwarder.  See http://docs.datadoghq.com/guides/basic_agent_usage/ for details.
8125 is the default dogstatsd port.  Adjust the above command accordingly if you run dogstatsd on a different port.



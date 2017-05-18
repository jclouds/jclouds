# Apache jclouds Packet (packet.net) provider


## Pre-requisites

If you are using Oracle JDK, you may get this exception
```
Caused by: javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure
```
when calling Packet endpoints. To solve this, you want to install JCE following the [official documentation](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).

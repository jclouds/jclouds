jclouds OkHttp driver
=====================

A driver to use the OkHttp (http://square.github.io/okhttp/) client as an HTTP library in jclouds.

To use the driver, you just need to include the `OkHttpCommandExecutorServiceModule` when creating
the context:

    ContextBuilder.newBuilder("provider")
        .endpoint("endpoint")
        .credentials("identity", "credential")
        .modules(ImmutableSet.of(new OkHttpCommandExecutorServiceModule()))
        .build();

Rackspace Cloud Files UK
========================

The new Rackspace Cloud Files UK multi-region based provider.

This new "rackspace-cloudfiles-uk" provider supercedes the jclouds "cloudfiles-uk" provider, which will eventually be deprecated.

With this multi-region support, a BlobStore can be isolated to a specific region:

     RegionScopedBlobStoreContext ctx = 
     	contextBuilder.buildView(RegionScopedBlobStoreContext.class);
 
     Set<String> regionIds = ctx.configuredRegions();
 
     // isolated to the only UK region
     BlobStore dfwBlobStore = ctx.blobStoreInRegion("LON");

Production ready?
Beta

This API is new to jclouds and hence is in Beta. That means we need people to use it and give us feedback. Based on that feedback, minor changes to the interfaces may happen. This code will replace org.jclouds.openstack.swift.SwiftClient in jclouds 2.0 and it is recommended you adopt it sooner than later.

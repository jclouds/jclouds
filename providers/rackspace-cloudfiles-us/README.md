Rackspace Cloud Files US
========================

The new Rackspace Cloud Files US multi-region based provider.

This new "rackspace-cloudfiles-us" provider supercedes the jclouds "cloudfiles-us" provider, which will eventually be deprecated.

With this multi-region support, each BlobStore can be isolated to a specific region:

     RegionScopedBlobStoreContext ctx = 
     	contextBuilder.buildView(RegionScopedBlobStoreContext.class);
 
     Set<String> regionIds = ctx.configuredRegions();
 
     // isolated to a specific region
     BlobStore dfwBlobStore = ctx.blobStoreInRegion("DFW");
     BlobStore iadBlobStore = ctx.blobStoreInRegion("IAD");

Production ready?
No

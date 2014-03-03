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
No

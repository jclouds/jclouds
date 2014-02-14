Rackspace Cloud Files
==========================

The new Rackspace Cloud Files multi-region based service API.

This new "rackspace-cloudfiles" API supercedes the jclouds "cloudfiles" API, which will eventually be deprecated.

With this multi-region support, each BlobStore can be isolated to a specific region:

     RegionScopedBlobStoreContext ctx = 
     	contextBuilder.buildView(RegionScopedBlobStoreContext.class);
 
     Set<String> regionIds = ctx.configuredRegions();
 
     // isolated to a specific region
     BlobStore dfwBlobStore = ctx.blobStoreInRegion("DFW");
     BlobStore iadBlobStore = ctx.blobStoreInRegion("IAD");

Production ready?
No

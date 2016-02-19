# jclouds ProfitBricks

## Terms
Like any cloud provider, ProfitBricks has its own set of terms in cloud computing. To abstract this into jclouds' Compute interface, these terms were associated:

- Node - composite instance of `Server` and `Storage`
- Image - both *user-uploaded* and *provided* `Images`; and `Snapshots`
- Location - `DataCenters` and `Region` (Las Vegas, Frankfurt, etc.)
- Hardware - number of cores, RAM size and storage size

## Getting Started

```java
ComputeService compute = ContextBuilder.newBuilder( "profitbricks" )
					.credentials( "profitbricks email", "password" )
					.buildView( ComputeServiceContext.class )
					.getComputeService();
```


This works well; however, we won't be able to use jclouds' ability to execute *scripts* on a remote node. This is because, ProfitBricks' default images require users to change passwords upon first log in.

To enable jclouds to execute script, we need to use a custom image. The easiest way to do this is via ProfitBricks snapshot:

-  Go to your [DCD](https://my.profitbricks.com/dashboard/).
-  Provision a server + storage, and connect it to the internet. Upon success, you will receive an email containing the credentials needed to login to your server.
-  Login to your server, and change the password, as requested.

```
~ ssh root@<remote-ip>
...
Changing password for root.
(current) UNIX password: 
Enter new UNIX password: 
Retype new UNIX password: 
~ root@ubuntu:~# exit

```

- Go back to the DCD, and *make a snapshot* of the storage. Put a descriptive name.
- Configure jclouds to use this *snapshot*.

```java 
Template template = compute.templateBuilder()
	.imageNameMatches( "<ideally-unique-snapshot-name>" )
	.options( compute.templateOptions()
				.overrideLoginUser( "root" ) // unless you changed the user
				.overrideLoginPassword( "<changed-password>" ))
	// more options, as you need
	.build();
	
compute.createNodesInGroup( "cluster1", 1, template );
```

## Limitations

- There's no direct way of specifying arbitrary number of cores, RAM size, and storage size via the compute interface, at least until after [JCLOUDS-482](https://issues.apache.org/jira/browse/JCLOUDS-482) is resolved. The adapter uses a predefined list hardware profiles instead.

> Take note that these features are still accessible by *unwraping* the ProfitBricks API, but this'll reduce portability of your code. See [Concepts](https://jclouds.apache.org/start/concepts/).

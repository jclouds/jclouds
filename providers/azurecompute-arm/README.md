jclouds Labs - Azure Compute ARM Provider
============

Build status for azurecomputearm module:
[![Build Status](http://devopsfunjenkins.westus.cloudapp.azure.com:8080/buildStatus/icon?job=jclouds-labs-azurecompute-arm/org.apache.jclouds.labs:azurecomputearm)](http://devopsfunjenkins.westus.cloudapp.azure.com:8080/job/jclouds-labs-azurecompute-arm/org.apache.jclouds.labs$azurecomputearm/)


## Setting Up Test Credentials

### Create a Service Principal

Install and configure Azure CLI following these [steps](http://azure.microsoft.com/en-us/documentation/articles/xplat-cli/).

Using the Azure CLI, run the following commands to create a service principal

```bash
# Set mode to ARM
azure config mode arm

# Enter your Microsoft account credentials when prompted
azure login

# Set current subscription to create a service principal
azure account set <Subscription-id>

# Create an AAD application with your information.
azure ad app create --name <name> --password <password> --home-page <home-page> --identifier-uris <identifier-uris>

# For example: azure ad app create --name "jcloudsarm"  --password abcd --home-page "https://jcloudsarm" --identifier-uris "https://jcloudsarm"

# Output will include a value for `Application Id`, which will be used for the live tests

# Create a Service Principal
azure ad sp create <Application-id>

# Output will include a value for `Object Id`

```

Run the following commands to assign roles to the service principal

```bash
# Assign roles for this service principal
azure role assignment create --objectId <Object-id> -o Contributor -c /subscriptions/<Subscription-id>/

```

Verify service principal

```bash
azure login -u <Application-id> -p <password> --service-principal --tenant <Tenant-id>

```

## Run Live Tests


Use the following to run the live tests

```bash
# ResourceGroupApiLiveTest:

mvn -Dtest=ResourceGroupApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.endpoint="https://management.azure.com" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

# AuthorizationApiLiveTest:

mvn -Dtest=AuthorizationApiLiveTest -Dtest.oauth.identity=<Application-id> -Dtest.oauth.credential=<password> -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" -Dtest.jclouds.oauth.audience="https://management.azure.com/" test

# LocationApiLiveTest:

mvn -Dtest=LocationApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.endpoint="https://management.azure.com" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

# StorageAccountApiLiveTest:

mvn -Dtest=StorageAccountApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.endpoint="https://management.azure.com" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

# VirtualNetworkApiLiveTest:

mvn -Dtest=VirtualNetworkApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.resourcegroup="jcloudstest" -Dtest.azurecompute-arm.endpoint="https://management.azure.com" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

# SubnetApiLiveTest

mvn -Dtest=SubnetApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.resourcegroup="jcloudstest" -Dtest.azurecompute-arm.endpoint="https://management.azure.com" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

# NetworkInterfaceCardApiLiveTest

mvn -Dtest=NetworkInterfaceCardApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.resourcegroup="jcloudstest" -Dtest.azurecompute-arm.endpoint="https://management.azure.com" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

# VirtualMachineApiLiveTest:

mvn -Dtest=VirtualMachineApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.endpoint="https://management.azure.com" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

# VMSizeApiLiveTest:

mvn -Dtest=VMSizeApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.endpoint="https://management.azure.com" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

# PublicIPAddressApiLiveTest

mvn -Dtest=PublicIPAddressApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.endpoint="https://management.azure.com" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

# OSImageApiLiveTest:

mvn -Dtest=OSImageApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.endpoint="https://management.azure.com" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

# DeploymentApiLiveTest:

mvn -Dtest=DeploymentApiLiveTest -Dtest.azurecompute-arm.identity=<Application-id> -Dtest.azurecompute-arm.subscriptionid=<Subscription-id> -Dtest.azurecompute-arm.credential=<password> -Dtest.azurecompute-arm.endpoint="https://management.azure.com/" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token" test

```
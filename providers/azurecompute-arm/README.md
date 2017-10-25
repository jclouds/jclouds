jclouds Labs - Azure Compute ARM Provider
============

Build status for azurecomputearm module:
[![Build Status](https://jclouds.ci.cloudbees.com/buildStatus/icon?job=jclouds-labs/org.apache.jclouds.labs$azurecompute-arm)](https://jclouds.ci.cloudbees.com/job/jclouds-labs/org.apache.jclouds.labs$azurecompute-arm/)

## Setting Up Test Credentials

### Create a Service Principal

Install and configure Azure CLI following these [steps](http://azure.microsoft.com/en-us/documentation/articles/xplat-cli/).

Using the Azure CLI, run the following commands to create a service principal

```bash
# Enter your Microsoft account credentials when prompted
az login

# Set current subscription to create a service principal
az account set --subscription <Subscription-id>

# Create an AD application with your information.
az ad app create --display-name <name> --password <password> --homepage <home-page> --identifier-uris <identifier-uris>

# For example: az ad app create --display-name "jcloudsarm" --password abcd --homepage "https://jcloudsarm" --identifier-uris "https://jcloudsarm"

# Output will include a value for `Application Id`, which will be used for the live tests

# Create a Service Principal
az ad sp create --id <Application-id>

# Output will include a value for `Object Id`, to be used in the next step 
```

Run the following commands to assign roles to the service principal

```bash
# Assign roles for this service principal
az role assignment create --role Contributor --assignee <ObjectId>
```

Look up the the tenant Id

```bash
az account show

# output will be a JSON which will include the `Tenant id`
```

Verify service principal

```bash
az login -u <Application-id> -p <password> --service-principal --tenant <Tenant-id>
```

## Run Live Tests

Use the following to run one live test:

```bash
mvn -Dtest=<name of the live test> \
    -Dtest.azurecompute-arm.identity="<Application-id>" \
    -Dtest.azurecompute-arm.credential="<password>" \
    -Dtest.azurecompute-arm.endpoint="https://management.azure.com/subscriptions/<Subscription-id>" \
    -Dtest.oauth.endpoint="https://login.microsoftonline.com/<Tenant-id>/oauth2/token"
    integration-test -Plive

```

Use the following to run all the live tests:

```bash

mvn clean verify -Plive \
    -Dtest.azurecompute-arm.identity="<Application-id>"" \
    -Dtest.azurecompute-arm.credential="<password>"" \
    -Dtest.azurecompute-arm.endpoint="https://management.azure.com/subscriptions/<Subscription-id>"" \
    -Dtest.oauth.endpoint=https://login.microsoftonline.com/<Tenant-id>/oauth2/token

```

## How to use it

Azure Compute ARM provider works exactly as any other jclouds provider.
Notice that as Azure supports dozens of locations, operations like listImages can be really time-consuming.
To limit the scope of such operations there are some additional properties you may want to use:

```bash
jclouds.azurecompute.arm.publishers
```
which is by default `Canonical,RedHat`

and
```bash
jclouds.regions
```
which is by default `null`. If you want to target only the `north europe` region, you can use

```bash
jclouds.regions="northeurope"
```

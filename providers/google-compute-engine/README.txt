jclouds Google Compute Engine Provider
======


Authenticating into the instances:
--------

User:
If no user is provided in GoogleComputeEngineTemplateOptions when launching an instance by default "jclouds" is used.

Credential:

GCE uses exclusively ssh keys to login into instances.
In order for an instance to be sshable a public key must be installed. Public keys are installed if they are present in the project or instance's metatada.

For an instance to be ssable one of the following must happen:
1 - the project's metadata has an adequately built "sshKeys" entry and a corresponding private key is provided in GoogleComputeEngineTemplateOptions when createNodesInGroup is called.
2 - an instance of GoogleComputeEngineTemplateOptions with an adequate public and private key is provided.

NOTE: if methods 2 is chosen the global project keys will not be installed in the instance.

Please refer to Google's documentation on how to form valid project wide ssh keys metadata entries.

FAQ:
--------

* Q. What is the identity for GCE?

A. the identity is the developer email which can be obtained from the admin GUI. Its usually something in the form: <my account id>@developer.gserviceaccount.com

* Q. What is the credential for GCE

A. the credential is a private key, in pem format. It can be extracted from the p12 keystore that is obtained when creating a "Service Account" (in the GUI: Google apis console > Api Access > Create another client ID > "Service Account"

* Q. How to convert a p12 keystore into a pem format jclouds Google Compute Engine can handle:

A.

1. Convert the p12 file into pem format (it will ask for the keystore password, which is usually "notasecret"):
 openssl pkcs12 -in <my_keystore>.p12 -out <my_keystore>.pem -nodes

2. Extract only the pk and remove passphrase
 openssl rsa -in <my_keystore>.pem -out <my_key>.pem

The last file (<my_key>.pem) should contain the pk that needs to be passed to `ContextBuilder.credential()` for the provider `google-compute-engine`.


Running the live tests:
--------

1. Place the following in your ~/.m2/settings.xml in a profile enabled when live:
```
    <test.google-compute-engine.identity>YOUR_ACCOUNT_NUMBER@developer.gserviceaccount.com</test.google-compute-engine.identity>
    <test.google-compute-engine.credential>-----BEGIN RSA PRIVATE KEY-----
MIICXgIBAAKBgQRRbRqVDtJLN1MO/xJoKqZuphDeBh5jIKueW3aNIiWs1XFcct+h
-- this text is literally from your <my_key>.pem
aH7xmpHSTbbXmQkuuv+z8EKijigprd/FoJpTX1f5/R+4wQ==
-----END RSA PRIVATE KEY-----</test.google-compute-engine.credential>
  </properties>
```

2. mvn clean install -Plive 


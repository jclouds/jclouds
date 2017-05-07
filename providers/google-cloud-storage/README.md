jclouds Google Cloud Storage Provider
======
Make sure both Google Cloud Storage and Google Cloud Storage JSON API are enabled for the project
(check from Developers Console -> Api&auth -> APIs)

FAQ:
--------

* Q. What is the identity for Google Cloud Storage?

A. the identity is the developer email which can be obtained from the admin GUI. Its usually something in the form: [PROJECT_ID](https://cloud.google.com/compute/docs/overview#projectids)@developer.gserviceaccount.com

* Q. What is the credential for Google Cloud Storage

A. the credential is a private key, in pem format. It can be extracted from the p12 keystore that is obtained when creating a "Service Account" (in the GUI: Google apis console > Api Access > Create another client ID > "Service Account"

* Q. How to convert a p12 keystore into a pem format jclouds Google Cloud Storage can handle:

A.

1. Convert the p12 file into pem format (it will ask for the keystore password, which is usually "notasecret"):
 openssl pkcs12 -in <my_keystore>.p12 -out <my_keystore>.pem -nodes

2. Extract only the pk and remove passphrase
 openssl rsa -in <my_keystore>.pem -out <my_key>.pem

The last file (<my_key>.pem) should contain the pk that needs to be passed to `ContextBuilder.credential()` for the provider `google-cloud-storage`.


Running the live tests:
--------

1. Place the following in your ~/.m2/settings.xml in a profile enabled when live:
```
    <test.google-cloud-storage.identity>PROJECT_ID@developer.gserviceaccount.com</test.google-cloud-storage.identity>
    <test.google-cloud-storage.credential>-----BEGIN RSA PRIVATE KEY-----
MIICXgIBAAKBgQRRbRqVDtJLN1MO/xJoKqZuphDeBh5jIKueW3aNIiWs1XFcct+h
-- this text is literally from your <my_key>.pem
aH7xmpHSTbbXmQkuuv+z8EKijigprd/FoJpTX1f5/R+4wQ==
-----END RSA PRIVATE KEY-----</test.google-cloud-storage.credential>
  </properties>
```
Or, if using an existing OAuth Bearer Token for authentication.
```
    <test.google-cloud-storage.identity>PROJECT_ID@developer.gserviceaccount.com</test.google-cloud-storage.identity>
    <test.google-cloud-storage.credential>EXISTING_BEARER_TOKEN</test.google-cloud-storage.credential>
    <test.jclouds.oauth.credential-type>bearerTokenCredentials</test.jclouds.oauth.credential-type>
  </properties>
```

2. mvn clean install -Plive 

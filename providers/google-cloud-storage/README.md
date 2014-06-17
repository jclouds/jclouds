jclouds Google Cloud Storage Provider
===========================================================
Make sure both Google Cloud Storage and Google Cloud Storage JSON API are enabled for the project
(check from Developers Console -> Api&auth -> APIs)

* Q. What is the identity for GCE?

A. the identity is the developer email which can be obtained from the admin GUI. Its usually something in the form: <my account id>@developer.gserviceaccount.com

* Q. What is the credential for GCE

A. the credential is a private key, in pem format. It can be extracted from the p12 keystore that is obtained when creating a "Service Account" (in the GUI: Developers Console(For the project) -> APIs and Auth -> Create New Client ID -> "Service Account"

* Q. How to convert a p12 keystore into a pem format jclouds Google Cloud Storage can handle:

A.

1. Convert the p12 file into pem format (it will ask for the keystore password, which is usually "notasecret"):
 openssl pkcs12 -in <my_keystore>.p12 -out <my_keystore>.pem -nodes

2. Extract only the pk and remove passphrase
 openssl rsa -in <my_keystore>.pem -out <my_key>.pem

The last file (<my_key>.pem) should contain the pk that needs to be passed to `ContextBuilder.credential()` for the provider `google-cloud-storage`.


Running the live tests:
---------------------------------------------------------------

1. Place the following in your ~/.m2/settings.xml in a profile enabled when live:

```
<properties>
    <test.google-cloud-storage.identity>Email address associated with service account</test.google-cloud-storage.identity>
    <!-- this text is literally from your <my_key>.pem -->
    <test.google-cloud-storage.credential>-----BEGIN RSA PRIVATE KEY-----
MIICXgIBAAKBgQRRbRqVDtJLN1MO/xJoKqZuphDeBh5jIKueW3aNIiWs1XFcct+h
...
aH7xmpHSTbbXmQkuuv+z8EKijigprd/FoJpTX1f5/R+4wQ==
-----END RSA PRIVATE KEY-----</test.google-cloud-storage.credential>
</properties>
```

Example identity :- 123451234-abcd01234efgh@developer.gserviceaccount.com (NUMERIC_PREFIX-ALPHANEUMERIC_SUFFIX@developer.gserviceaccount.com)

2. mvn integration-test -pl google-cloud-storage -Plive

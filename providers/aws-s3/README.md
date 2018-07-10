# The jclouds provider for Amazon's S3 (http://aws.amazon.com/s3/)

Expects the jclouds s3 API to be present on your application's classpath.

* **TODO**: Implementation status.
* **TODO**: Supported features.
* **TODO**: Usage example.

## Running live tests

Try

```sh
mvn clean install -Plive -pl :aws-s3 -Dtest=AWSS3ClientLiveTest -Dtest.aws-s3.identity=<aws_access_key_id> -Dtest.aws-s3.credential=<aws_secret_access_key>
```

optionally adding

```sh
-Dtest.aws-s3.sessionToken=<aws_session_token>
```

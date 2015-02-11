/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.s3.filters;

/**
 * AWS Signature Version 4 Constants.
 */
public abstract class AwsSignatureV4Constants {

    /**
     * AWS authorization header key
     */
   public static final String AUTHORIZATION_HEADER = "Authorization";

   /**
    * AWS content sha256 header key
    */
   public static final String AMZ_CONTENT_SHA256_HEADER = "x-amz-content-sha256";

   /**
    * AWS date header key
    */
   public static final String AMZ_DATE_HEADER = "X-Amz-Date";

   /**
    * AWS security token key
    */
   public static final String AMZ_SECURITY_TOKEN_HEADER = "X-Amz-Security-Token";

   /**
    * For AWS Signature Version 4, you set this parameter value to "AWS4-HMAC-SHA256".
    */
   public static final String AMZ_ALGORITHM_PARAM = "X-Amz-Algorithm";
   /**
    * This string identifies AWS Signature Version 4 (AWS4) and the HMAC-SHA256 algorithm (HMAC-SHA256).
    */
   public static final String AMZ_ALGORITHM_HMAC_SHA256 = "AWS4-HMAC-SHA256";

   /**
    * In addition to your access key ID, this parameter also provides scope information identifying the region and
    * service for which the signature is valid.
    * <p>This value should match the scope that you use to calculate the signing key, as discussed in the following section.</p>
    * <p>The general form for this parameter value is as follows:</p>
    * <code> &lt;your-access-key-id>/&lt;date>/&lt;AWS-region>/&lt;AWS-service>/aws4_request.</code>
    * <p>
    * For example:
    * <code>AKIAIOSFODNN7EXAMPLE/20130721/us-east-1/s3/aws4_request.</code><br>
    * For Amazon S3, the AWS-service string is "s3". For a list of AWS-region strings, go to Regions and Endpoints
    * in the Amazon Web Services General Reference
    * </p>
    */
   public static final String AMZ_CREDENTIAL_PARAM = "X-Amz-Credential";

   /**
    * This header can be used in the following scenarios:
    * <ul>
    * <li>Provide security tokens for Amazon DevPay operations—Each request that uses Amazon DevPay requires two
    * x-amz-security-token headers: one for the product token and one for the user token. When Amazon S3 receives
    * an authenticated request, it compares the computed signature with the provided signature.
    * Improperly formatted multi-value headers used to calculate a signature can cause authentication issues</li>
    * <li>Provide security token when using temporary security credentials—When making requests using temporary
    * security credentials you obtained from IAM you must provide a security token using this header.
    * To learn more about temporary security credentials, go to Making Requests.</li>
    * </ul>
    * This header is required for requests that use Amazon DevPay and requests that are signed using temporary security credentials.
    */

   public static final String AMZ_SECURITY_TOKEN_PARAM = AMZ_SECURITY_TOKEN_HEADER;

   /**
    * The date in ISO 8601 format, for example, 20130721T201207Z. This value must match the date value used to
    * calculate the signature.
    */
   public static final String AMZ_DATE_PARAM = AMZ_DATE_HEADER;

   /**
    * Provides the time period, in seconds, for which the generated presigned URL is valid.
    * <p> For example, 86400 (24 hours). This value is an integer. The minimum value you can set is 1,
    * and the maximum is 604800 (seven days). </p>
    * <p> A presigned URL can be valid for a maximum of seven days because the signing key you use in signature
    * calculation is valid for up to seven days.</p>
    */
   public static final String AMZ_EXPIRES_PARAM = "X-Amz-Expires";

   /**
    * Lists the headers that you used to calculate the signature.
    * <p> The HTTP host header is required. Any x-amz-* headers that you plan to add to the request are also required
    * for signature calculation. </p>
    * <p> In general, for added security, you should sign all the request headers that you plan to include in your
    * request.</p>
    */
   public static final String AMZ_SIGNEDHEADERS_PARAM = "X-Amz-SignedHeaders";

   /**
    * X-Amz-Signature Provides the signature to authenticate your request.
    * <p>This signature must match the signature Amazon S3 calculates; otherwise, Amazon S3 denies the request.
    * For example, 733255ef022bec3f2a8701cd61d4b371f3f28c9f193a1f02279211d48d5193d7</p>
    */
   public static final String AMZ_SIGNATURE_PARAM = "X-Amz-Signature";

   /**
    * You don't include a payload hash in the Canonical Request, because when you create a presigned URL,
    * <p> you don't know anything about the payload. Instead, you use a constant string "UNSIGNED-PAYLOAD".</p>
    */
   public static final String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";

   /**
    * SHA256 substitute marker used in place of x-amz-content-sha256 when
    * employing chunked uploads
    */
   public static final String STREAMING_BODY_SHA256 = "STREAMING-AWS4-HMAC-SHA256-PAYLOAD";
   public static final String CHUNK_STRING_TO_SIGN_PREFIX = "AWS4-HMAC-SHA256-PAYLOAD";

   public static final String CLRF = "\r\n";

   public static final String CHUNK_SIGNATURE_HEADER = ";chunk-signature=";
   public static final int SIGNATURE_LENGTH = 64;
   public static final byte[] FINAL_CHUNK = new byte[0];

   /**
    * Content-Encoding
    * <p>
    * Set the value to aws-chunked.<br>
    * Amazon S3 supports multiple content encodings, for example,<br>
    * Content-Encoding : aws-chunked, gzip<br>
    * That is, you can specify your custom content-encoding when using Signature Version 4 streaming API.
    * </p>
    */
   public static final String CONTENT_ENCODING_HEADER_AWS_CHUNKED = "aws-chunked";
   /**
    * 'x-amz-decoded-content-length' is used to transmit the actual
    */
   public static final String AMZ_DECODED_CONTENT_LENGTH_HEADER = "x-amz-decoded-content-length";

   private AwsSignatureV4Constants() {
   }
}

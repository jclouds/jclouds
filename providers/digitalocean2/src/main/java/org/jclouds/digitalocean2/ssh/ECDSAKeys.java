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
package org.jclouds.digitalocean2.ssh;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Splitter.fixedLength;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static com.google.common.io.BaseEncoding.base16;
import static com.google.common.io.BaseEncoding.base64;
import static org.jclouds.util.Strings2.toStringAndClose;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

/**
 * Utility methods to work with ECDSA Elliptic Curve DSA keys.
 * <p>
 * Methods in this class should be moved to the {@link org.jclouds.ssh.SshKeys} class.
 * 
 * @see org.jclouds.ssh.SshKeys
 */
public class ECDSAKeys {
   public static final String ECDSA_SHA2_PREFIX = "ecdsa-sha2-";

   private static final String NISTP256 = "nistp256";
   private static final String NISTP384 = "nistp384";
   private static final String NISTP521 = "nistp521";

   private static final Map<String, ECParameterSpec> CURVES = new TreeMap<String, ECParameterSpec>();
   static {
      CURVES.put(NISTP256, EllipticCurves.nistp256);
      CURVES.put(NISTP384, EllipticCurves.nistp384);
      CURVES.put(NISTP521, EllipticCurves.nistp521);
   }

   private static final Map<Integer, String> CURVE_SIZES = new TreeMap<Integer, String>();
   static {
      CURVE_SIZES.put(256, NISTP256);
      CURVE_SIZES.put(384, NISTP384);
      CURVE_SIZES.put(521, NISTP521);
   }

   public static String encodeAsOpenSSH(ECPublicKey key) {

      String curveName = null;
      try {
         curveName = getCurveName(key.getParams());
      } catch (IOException e) {
         propagate(e);
      }

      String keyFormat = ECDSA_SHA2_PREFIX + curveName;

      byte[] keyBlob = keyBlob(key);
      return keyFormat + " " + base64().encode(keyBlob);
   }

   /**
    * Executes {@link org.jclouds.crypto.Pems#publicKeySpecFromOpenSSH(com.google.common.io.InputSupplier)} on the
    * string which was OpenSSH Base64 Encoded {@code id_rsa.pub}
    * 
    * @param idRsaPub formatted {@code ssh-dss AAAAB3NzaC1yc2EAAAADAQABAAAB...}
    * @see org.jclouds.crypto.Pems#publicKeySpecFromOpenSSH(com.google.common.io.InputSupplier)
    */
   public static ECPublicKeySpec publicKeySpecFromOpenSSH(String ecDsaPub) {
      try {
         return publicKeySpecFromOpenSSH(ByteSource.wrap(ecDsaPub.getBytes(Charsets.UTF_8)));
      } catch (IOException e) {
         throw propagate(e);
      }
   }

   /**
    * Returns {@link java.security.spec.DSAPublicKeySpec} which was OpenSSH Base64 Encoded {@code id_rsa.pub}
    *
    * @param supplier the input stream factory, formatted {@code ssh-dss AAAAB3NzaC1yc2EAAAADAQABAAAB...}
    *
    * @return the {@link java.security.spec.DSAPublicKeySpec} which was OpenSSH Base64 Encoded {@code id_rsa.pub}
    * @throws java.io.IOException if an I/O error occurs
    */
   public static ECPublicKeySpec publicKeySpecFromOpenSSH(ByteSource supplier) throws IOException {
      InputStream stream = supplier.openStream();
      Iterable<String> parts = Splitter.on(' ').split(toStringAndClose(stream).trim());
      String signatureFormat = get(parts, 0);
      checkArgument(size(parts) >= 2 && signatureFormat.startsWith(ECDSA_SHA2_PREFIX), "bad format, should be: ecdsa-sha2-xxx AAAAB3...");

      String curveName = signatureFormat.substring(ECDSA_SHA2_PREFIX.length());
      if (!CURVES.containsKey(curveName)) {
         throw new IOException("Unsupported curve: " + curveName);
      }
      ECParameterSpec spec = CURVES.get(curveName);
      stream = new ByteArrayInputStream(base64().decode(get(parts, 1)));
      readLengthFirst(stream);  // ignore return value
      String curveMarker = new String(readLengthFirst(stream));
      checkArgument(curveName.equals(curveMarker), "looking for marker %s but got %s", curveName, curveMarker);

      ECPoint ecPoint = decodeECPoint(readLengthFirst(stream), spec.getCurve());

      return new ECPublicKeySpec(ecPoint, spec);
   }

   /**
    * @param publicKeyOpenSSH RSA public key in OpenSSH format
    * @return fingerprint ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    */
   public static String fingerprintPublicKey(String publicKeyOpenSSH) throws IOException {
      ECPublicKeySpec publicKeySpec = publicKeySpecFromOpenSSH(publicKeyOpenSSH);
      String fingerprint = null;
      try {
         ECPublicKey pk = (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(publicKeySpec);
         fingerprint = fingerprint(pk);
      } catch (InvalidKeySpecException e) {
         e.printStackTrace();
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      }
      return fingerprint;
   }

   /**
    * Create a fingerprint per the following <a href="http://tools.ietf.org/html/draft-friedl-secsh-fingerprint-00"
    * >spec</a>
    * 
    * @return hex fingerprint ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    */
   public static String fingerprint(ECPublicKey publicKey) {
      byte[] keyBlob = keyBlob(publicKey);
      return hexColonDelimited(Hashing.md5().hashBytes(keyBlob));
   }

   /**
    * @see org.jclouds.ssh.SshKeys
    */
   private static String hexColonDelimited(HashCode hc) {
      return on(':').join(fixedLength(2).split(base16().lowerCase().encode(hc.asBytes())));
   }

   private static byte[] keyBlob(ECPublicKey key) {
      try {
         String curveName = getCurveName(key.getParams());
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         writeLengthFirst((ECDSA_SHA2_PREFIX + curveName).getBytes(), out);
         writeLengthFirst(curveName.getBytes(), out);
         writeLengthFirst(encodeECPoint(key.getW(), key.getParams().getCurve()), out);
         return out.toByteArray();
      } catch (IOException e) {
         throw propagate(e);
      }
   }

   /**
    * @see org.jclouds.ssh.SshKeys
    */
   // http://www.ietf.org/rfc/rfc4253.txt
   private static byte[] readLengthFirst(InputStream in) throws IOException {
      int byte1 = in.read();
      int byte2 = in.read();
      int byte3 = in.read();
      int byte4 = in.read();
      int length = (byte1 << 24) + (byte2 << 16) + (byte3 << 8) + (byte4 << 0);
      byte[] val = new byte[length];
      ByteStreams.readFully(in, val);
      return val;
   }

   /**
    * @see org.jclouds.ssh.SshKeys
    */
   // http://www.ietf.org/rfc/rfc4253.txt
   private static void writeLengthFirst(byte[] array, ByteArrayOutputStream out) throws IOException {
      out.write(array.length >>> 24 & 0xFF);
      out.write(array.length >>> 16 & 0xFF);
      out.write(array.length >>> 8 & 0xFF);
      out.write(array.length >>> 0 & 0xFF);
      if (array.length == 1 && array[0] == (byte) 0x00) {
         out.write(new byte[0]);
      } else {
         out.write(array);
      }
   }

   private static String getCurveName(ECParameterSpec params) throws IOException {
      int fieldSize = getCurveSize(params);
      String curveName = CURVE_SIZES.get(fieldSize);
      if (curveName == null) {
         throw new IOException("Unsupported curve field size: " + fieldSize);
      }
      return curveName;
   }

   private static int getCurveSize(ECParameterSpec params) {
      return params.getCurve().getField().getFieldSize();
   }

   /**
    * Encode EllipticCurvePoint to an OctetString
    */
   public static byte[] encodeECPoint(ECPoint group, EllipticCurve curve)
   {
      // M has len 2 ceil(log_2(q)/8) + 1 ?
      int elementSize = (curve.getField().getFieldSize() + 7) / 8;
      byte[] M = new byte[2 * elementSize + 1];

      // Uncompressed format
      M[0] = 0x04;

      {
         byte[] affineX = removeLeadingZeroes(group.getAffineX().toByteArray());
         System.arraycopy(affineX, 0, M, 1 + elementSize - affineX.length, affineX.length);
      }

      {
         byte[] affineY = removeLeadingZeroes(group.getAffineY().toByteArray());
         System.arraycopy(affineY, 0, M, 1 + elementSize + elementSize - affineY.length,
               affineY.length);
      }

      return M;
   }

   private static byte[] removeLeadingZeroes(byte[] input) {
      if (input[0] != 0x00) {
         return input;
      }

      int pos = 1;
      while (pos < input.length - 1 && input[pos] == 0x00) {
         pos++;
      }

      byte[] output = new byte[input.length - pos];
      System.arraycopy(input, pos, output, 0, output.length);
      return output;
   }

   /**
    * Decode an OctetString to EllipticCurvePoint according to SECG 2.3.4
    */
   public static ECPoint decodeECPoint(byte[] M, EllipticCurve curve) {
      if (M.length == 0) {
         return null;
      }

      // M has len 2 ceil(log_2(q)/8) + 1 ?
      int elementSize = (curve.getField().getFieldSize() + 7) / 8;
      if (M.length != 2 * elementSize + 1) {
         return null;
      }

      // step 3.2
      if (M[0] != 0x04) {
         return null;
      }

      // Step 3.3
      byte[] xp = new byte[elementSize];
      System.arraycopy(M, 1, xp, 0, elementSize);

      // Step 3.4
      byte[] yp = new byte[elementSize];
      System.arraycopy(M, 1 + elementSize, yp, 0, elementSize);

      ECPoint P = new ECPoint(new BigInteger(1, xp), new BigInteger(1, yp));

      // TODO check point 3.5

      // Step 3.6
      return P;
   }

   public static class EllipticCurves {
      public static final ECParameterSpec nistp256 = new ECParameterSpec(
            new EllipticCurve(
                  new ECFieldFp(new BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", 16)),
                  new BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", 16),
                  new BigInteger("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b", 16)),
            new ECPoint(new BigInteger("6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", 16),
                  new BigInteger("4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", 16)),
            new BigInteger("FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 16),
            1);

      public static final ECParameterSpec nistp384 = new ECParameterSpec(
            new EllipticCurve(
                  new ECFieldFp(new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFF", 16)),
                  new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFC", 16),
                  new BigInteger("B3312FA7E23EE7E4988E056BE3F82D19181D9C6EFE8141120314088F5013875AC656398D8A2ED19D2A85C8EDD3EC2AEF", 16)),
            new ECPoint(new BigInteger("AA87CA22BE8B05378EB1C71EF320AD746E1D3B628BA79B9859F741E082542A385502F25DBF55296C3A545E3872760AB7", 16),
                  new BigInteger("3617DE4A96262C6F5D9E98BF9292DC29F8F41DBD289A147CE9DA3113B5F0B8C00A60B1CE1D7E819D7A431D7C90EA0E5F", 16)),
            new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC7634D81F4372DDF581A0DB248B0A77AECEC196ACCC52973", 16),
            1);

      public static final ECParameterSpec nistp521 = new ECParameterSpec(
            new EllipticCurve(
                  new ECFieldFp(new BigInteger("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)),
                  new BigInteger("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC", 16),
                  new BigInteger("0051953EB9618E1C9A1F929A21A0B68540EEA2DA725B99B315F3B8B489918EF109E156193951EC7E937B1652C0BD3BB1BF073573DF883D2C34F1EF451FD46B503F00", 16)
            ),
            new ECPoint(new BigInteger("00C6858E06B70404E9CD9E3ECB662395B4429C648139053FB521F828AF606B4D3DBAA14B5E77EFE75928FE1DC127A2FFA8DE3348B3C1856A429BF97E7E31C2E5BD66", 16),
                  new BigInteger("011839296A789A3BC0045C8A5FB42C7D1BD998F54449579B446817AFBD17273E662C97EE72995EF42640C550B9013FAD0761353C7086A272C24088BE94769FD16650", 16)
            ),
            new BigInteger("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA51868783BF2F966B7FCC0148F709A5D03BB5C9B8899C47AEBB6FB71E91386409", 16),
            1);
   }
}

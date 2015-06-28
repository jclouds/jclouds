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
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

/**
 * Utility methods to work with DSA SSH keys.
 * <p>
 * Methods in this class should be moved to the {@link org.jclouds.ssh.SshKeys} class.
 * 
 * 
 * @see org.jclouds.ssh.SshKeys
 */
public class DSAKeys {

   public static String encodeAsOpenSSH(DSAPublicKey key) {
      DSAParams params = key.getParams();
      byte[] keyBlob = keyBlob(params.getP(), params.getQ(), params.getG(), key.getY());
      return "ssh-dss " + base64().encode(keyBlob);
   }

   /**
    * Executes {@link org.jclouds.crypto.Pems#publicKeySpecFromOpenSSH(com.google.common.io.InputSupplier)} on the
    * string which was OpenSSH Base64 Encoded {@code id_rsa.pub}
    * 
    * @param idRsaPub formatted {@code ssh-dss AAAAB3NzaC1yc2EAAAADAQABAAAB...}
    * @see org.jclouds.crypto.Pems#publicKeySpecFromOpenSSH(com.google.common.io.InputSupplier)
    */
   public static DSAPublicKeySpec publicKeySpecFromOpenSSH(String idDsaPub) {
      try {
         return publicKeySpecFromOpenSSH(ByteSource.wrap(idDsaPub.getBytes(Charsets.UTF_8)));
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
   public static DSAPublicKeySpec publicKeySpecFromOpenSSH(ByteSource supplier) throws IOException {
      InputStream stream = supplier.openStream();
      Iterable<String> parts = Splitter.on(' ').split(toStringAndClose(stream).trim());
      checkArgument(size(parts) >= 2 && "ssh-dss".equals(get(parts, 0)), "bad format, should be: ssh-dss AAAAB3...");
      stream = new ByteArrayInputStream(base64().decode(get(parts, 1)));
      String marker = new String(readLengthFirst(stream));
      checkArgument("ssh-dss".equals(marker), "looking for marker ssh-dss but got %s", marker);
      BigInteger p = new BigInteger(readLengthFirst(stream));
      BigInteger q = new BigInteger(readLengthFirst(stream));
      BigInteger g = new BigInteger(readLengthFirst(stream));
      BigInteger y = new BigInteger(readLengthFirst(stream));
      return new DSAPublicKeySpec(y, p, q, g);
   }

   /**
    * @param publicKeyOpenSSH RSA public key in OpenSSH format
    * @return fingerprint ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    */
   public static String fingerprintPublicKey(String publicKeyOpenSSH) {
      DSAPublicKeySpec publicKeySpec = publicKeySpecFromOpenSSH(publicKeyOpenSSH);
      return fingerprint(publicKeySpec.getP(), publicKeySpec.getQ(), publicKeySpec.getG(), publicKeySpec.getY());
   }

   /**
    * Create a fingerprint per the following <a href="http://tools.ietf.org/html/draft-friedl-secsh-fingerprint-00"
    * >spec</a>
    * 
    * @return hex fingerprint ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    */
   public static String fingerprint(BigInteger p, BigInteger q, BigInteger g, BigInteger y) {
      byte[] keyBlob = keyBlob(p, q, g, y);
      return hexColonDelimited(Hashing.md5().hashBytes(keyBlob));
   }

   /**
    * @see org.jclouds.ssh.SshKeys
    */
   private static String hexColonDelimited(HashCode hc) {
      return on(':').join(fixedLength(2).split(base16().lowerCase().encode(hc.asBytes())));
   }

   /**
    * @see org.jclouds.ssh.SshKeys
    */
   private static byte[] keyBlob(BigInteger p, BigInteger q, BigInteger g, BigInteger y) {
      try {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         writeLengthFirst("ssh-dss".getBytes(), out);
         writeLengthFirst(p.toByteArray(), out);
         writeLengthFirst(q.toByteArray(), out);
         writeLengthFirst(g.toByteArray(), out);
         writeLengthFirst(y.toByteArray(), out);
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
}

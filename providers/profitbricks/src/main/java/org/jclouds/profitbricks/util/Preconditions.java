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
package org.jclouds.profitbricks.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.net.InetAddresses.isInetAddress;
import static org.jclouds.profitbricks.util.MacAddresses.isMacAddress;
import static org.jclouds.profitbricks.util.Passwords.isValidPassword;

import java.util.List;
import java.util.regex.Pattern;

import org.jclouds.profitbricks.domain.Firewall;
import org.jclouds.profitbricks.domain.Firewall.Protocol;

/**
 * Static convenience methods for validating various ProfitBricks domain preconditions
 */
public final class Preconditions {

   private static final Pattern INVALID_CHARS = Pattern.compile("^.*[@/\\|'`’^].*$");

   public static void checkInvalidChars(String name) {
      checkArgument(!isNullOrEmpty(name), "Name is required.");
      checkArgument(!INVALID_CHARS.matcher(name).matches(), "Name must not contain any of: @ / \\ | ' ` ’ ^");
   }

   public static void checkIp(String ip) {
      checkArgument(isInetAddress(ip), "IP '%s' is invalid", ip);
   }

   public static void checkIps(List<String> ips) {
      checkNotNull(ips, "Null ip list");
      for (String ip : ips)
         checkIp(ip);
   }

   public static void checkPortRange(Integer portRangeStart, Integer portRangeEnd, Firewall.Protocol protocol) {
      checkArgument(!(portRangeEnd == null ^ portRangeStart == null), "Port range must be both present or null");
      if (portRangeEnd != null) {
         checkArgument(protocol == Firewall.Protocol.TCP || protocol == Firewall.Protocol.UDP, "Port range can only be set for TCP or UDP");
         checkArgument(portRangeEnd > portRangeStart, "portRangeEnd must be greater than portRangeStart");
         checkArgument(portRangeEnd >= 1 && portRangeEnd <= 65534, "Port range end must be 1 to 65534");
         checkArgument(portRangeStart >= 1 && portRangeStart <= 65534, "Port range start must be 1 to 65534");
      }
   }

   public static void checkMacAddress(String macAddress) {
      checkArgument(isMacAddress(macAddress), "MAC must match pattern 'aa:bb:cc:dd:ee:ff'");
   }

   public static void checkIcmp(Integer icmpType, Integer icmpCode, Protocol protocol) {
      checkNotNull(protocol, "Protocol can't be null");
      if (protocol == Protocol.ICMP) {
         if (icmpType != null)
            checkArgument(icmpType >= 1 && icmpType <= 254, "ICMP type must be 1 to 254");
         if (icmpCode != null)
            checkArgument(icmpCode >= 1 && icmpCode <= 254, "ICMP code must be 1 to 254");
      }
   }

   public static void checkLanId(Integer id) {
      checkArgument(id >= 0, "LAN ID must be non-negative");
   }

   public static void checkCores(Integer cores) {
      checkArgument(cores > 0, "Number of cores must be atleast 1.");
   }

   public static void checkRam(Integer ram, Boolean isRamHotPlug) {
      int minRam = (isRamHotPlug == null || !isRamHotPlug) ? 256 : 1024;
      checkArgument(ram >= minRam && ram % 256 == 0, "RAM must be multiples of 256 with minimum of 256 MB "
              + "(1024 MB if ramHotPlug is enabled)");
   }

   public static void checkSize(Float size) {
      checkArgument(size > 1, "Storage size must be > 1GB");
   }

   public static void checkPassword(String password) {
      checkArgument(isValidPassword(password), "Password must be between 8 and 50 characters, "
              + "only a-z, A-Z, 0-9 without  characters i, I, l, o, O, w, W, y, Y, z, Z and 1, 0");
   }
}

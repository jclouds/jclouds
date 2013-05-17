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
package org.jclouds.virtualbox.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.virtualbox.domain.BridgedIf;
import org.testng.annotations.Test;

@Test(groups = "live", singleThreaded = true, testName = "BridgedIfStringToBridgedIfTest")
public class BridgedIfStringToBridgedIfTest {
		
	private static final String en0 = "Name:            en0: Ethernet\n" +
	"GUID:            00306e65-0000-4000-8000-3c0754205d2f\n" + 
	"Dhcp:            Disabled\n" +
	"IPAddress:       192.168.56.1\n" +
	"NetworkMask:     255.255.255.0\n" +
	"IPV6Address:     \n" +
	"IPV6NetworkMaskPrefixLength: 0\n" +
	"HardwareAddress: 3c:07:54:20:5d:2f\n" +
	"MediumType:      Ethernet\n" +
	"Status:          Up\n" +
	"VBoxNetworkName: HostInterfaceNetworking-en0: Ethernet\n";

	private static final String en1 = "Name:            en1: Wi-Fi (AirPort)\n" +
	"GUID:            00316e65-0000-4000-8000-28cfdaf2917a\n" +
	"Dhcp:            Disabled\n" +
	"IPAddress:       192.168.57.1\n" +
	"NetworkMask:     255.255.255.0\n" +
	"IPV6Address:     \n" +
	"IPV6NetworkMaskPrefixLength: 0\n" +
	"HardwareAddress: 28:cf:da:f2:91:7a\n" +
	"MediumType:      Ethernet\n" +
	"Status:          Up\n" +
	"VBoxNetworkName: HostInterfaceNetworking-en1: Wi-Fi (AirPort)\n";

	private static final String p2p0 = "Name:            p2p0\n" +
	"GUID:            30703270-0000-4000-8000-0acfdaf2917a\n" +
	"Dhcp:            Disabled\n" +
	"IPAddress:       192.168.58.1\n" +
	"NetworkMask:     255.255.255.0\n" +
	"IPV6Address:     \n" +
	"IPV6NetworkMaskPrefixLength: 0\n" +
	"HardwareAddress: 0a:cf:da:f2:91:7a\n" +
	"MediumType:      Ethernet\n" +
	"Status:          Down\n" +
	"VBoxNetworkName: HostInterfaceNetworking-p2p0\n";

	@Test
	public void transformRawBridgedifToBridgedIf() {
		BridgedIf bridgedIfEn1 = new BridgedIfStringToBridgedIf().apply(en1);
	      assertEquals(bridgedIfEn1.getName(), "en1: Wi-Fi (AirPort)");
	}
}

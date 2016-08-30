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
package org.jclouds.docker.parse;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.HostConfig;
import org.jclouds.docker.domain.NetworkSettings;
import org.jclouds.docker.domain.State;
import org.jclouds.docker.internal.BaseDockerParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * This class tests Containers and Config parsing for Docker API in version 1.21. The input JSON comes from examples in 
 * <a href="https://docs.docker.com/engine/reference/api/docker_remote_api_v1.21/">Docker Remote API documentation 1.21</a>.
 * <p>
 * Two modifications were made in the "/container-1.21-create.json" due to incompatible types 
 * <ul>
 * <li>the Entrypoint field value was changed from String to List<li>
 * <li>the LxcConf field value was changed from Map to List</li>
 * <ul>
 */
public class ContainerVersionMajor1Minor21 {

   @Test(groups = "unit")
   public static class CreateTest  extends BaseDockerParseTest<Config> {
      @Override
      public String resource() {
         return "/container-1.21-create.json";
      }

      @Override
      @Consumes(MediaType.APPLICATION_JSON)
      public Config expected() {
         return Config.builder()
                     .hostname("")
                     .domainname("")
                     .user("")
                     .attachStdin(false)
                     .attachStdout(true)
                     .attachStderr(true)
                     .tty(false)
                     .openStdin(false)
                     .stdinOnce(false)
                     .env(ImmutableList.of("FOO=bar", "BAZ=quux"))
                     .cmd(ImmutableList.of("date"))
                     //original value of the "Entrypoint" in JSON doesn't contain List but String! 
                     //Both types are allowed by docker Remote API, but we are not able to parse both.
                     .entrypoint(ImmutableList.of(""))
                     .image("ubuntu")
//                     "Labels": {
//                        "com.example.vendor": "Acme",
//                        "com.example.license": "GPL",
//                        "com.example.version": "1.0"
//                      },
                     .volumes(ImmutableMap.of("/volumes/data", ImmutableMap.of()))
                     .workingDir("")
                     .networkDisabled(false)
//                     "MacAddress": "12:34:56:78:9a:bc",
                     .exposedPorts(ImmutableMap.of("22/tcp", ImmutableMap.of()))
//                     "StopSignal": "SIGTERM",
                     .hostConfig(HostConfig.builder()
                           .binds(ImmutableList.of("/tmp:/tmp"))
                           .links(ImmutableList.of("redis3:redis"))
                           //The LxcConf causes the type mismatch too (Map vs List<Map>)
                           .lxcConf(ImmutableList.<Map<String, String>> of(
                                 ImmutableMap.<String, String> of("lxc.utsname", "docker")))
//                           "Memory": 0,
//                           "MemorySwap": 0,
//                           "MemoryReservation": 0,
//                           "KernelMemory": 0,
//                           "CpuShares": 512,
//                           "CpuPeriod": 100000,
//                           "CpuQuota": 50000,
//                           "CpusetCpus": "0,1",
//                           "CpusetMems": "0,1",
//                           "BlkioWeight": 300,
//                           "MemorySwappiness": 60,
//                           "OomKillDisable": false,
                           .portBindings(ImmutableMap.<String, List<Map<String, String>>> of(
                                 "22/tcp", ImmutableList.<Map<String, String>> of(ImmutableMap.of("HostPort", "11022"))))
                           .publishAllPorts(false)
                           .privileged(false)
//                           "ReadonlyRootfs": false,
                           .dns(ImmutableList.of("8.8.8.8"))
//                           "DnsOptions": [""],
                           .dnsSearch(ImmutableList.of(""))
                           .extraHosts(null)
                           .volumesFrom(ImmutableList.of("parent", "other:ro"))
                           .capAdd(ImmutableList.of("NET_ADMIN"))
                           .capDrop(ImmutableList.of("MKNOD"))
//                           "GroupAdd": ["newgroup"],
                           .restartPolicy(ImmutableMap.of("Name", "", "MaximumRetryCount", "0"))
                           .networkMode("bridge")
//                           "Devices": [],
//                           "Ulimits": [{}],                           
//                           "LogConfig": { "Type": "json-file", "Config": {} },
                           .securityOpt(ImmutableList.<String>of())
//                           "CgroupParent": "",
//                           "VolumeDriver": ""
                           .build()
      )
                     .build();
      }
   }
   
   @Test(groups = "unit")
   public static class InspectTest  extends BaseDockerParseTest<Container> {
      @Override
      public String resource() {
         return "/container-1.21-inspect.json";
      }

      @Override
      @Consumes(MediaType.APPLICATION_JSON)
      public Container expected() {
         return Container.builder()
//            "AppArmorProfile": "",
               .args(ImmutableList.<String>of("-c", "exit 9"))
               .config(Config.builder()
                     .attachStderr(true)
                     .attachStdin(false)
                     .attachStdout(true)
                     .cmd(ImmutableList.<String> of("/bin/sh", "-c", "exit 9"))
                     .domainname("")
                     .entrypoint(null)
                     .env(ImmutableList.<String> of("PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"))
                     .exposedPorts(null)
                     .hostname("ba033ac44011")
                     .image("ubuntu")                    
//                "Labels": {
//                    "com.example.vendor": "Acme",
//                    "com.example.license": "GPL",
//                    "com.example.version": "1.0"
//                },
//                "MacAddress": "",
                     .networkDisabled(false)
//                "OnBuild": null,
                     .openStdin(false)
                     .stdinOnce(false)
                     .tty(false)
                     .user("")
                     .volumes(null)
                     .workingDir("")
//                "StopSignal": "SIGTERM"
                     .build())
               .created(new SimpleDateFormatDateService().iso8601DateParse("2015-01-06T15:47:31.485331387Z"))
               .driver("devicemapper")
               .execDriver("native-0.2")
//            "ExecIDs": null,
               .hostConfig(HostConfig.builder()
                     .binds(null)
//                "BlkioWeight": 0,
                     .capAdd(null)
                     .capDrop(null)
                     .containerIDFile("")
//                "CpusetCpus": "",
//                "CpusetMems": "",
//                "CpuShares": 0,
//                "CpuPeriod": 100000,
//                "Devices": [],
                     .dns(null)
//                "DnsOptions": null,
                     .dnsSearch(null)
                     .extraHosts(null)
//                "IpcMode": "",
                     .links(null)
                     .lxcConf(ImmutableList.<Map<String, String>> of())
//                "Memory": 0,
//                "MemorySwap": 0,
//                "MemoryReservation": 0,
//                "KernelMemory": 0,
//                "OomKillDisable": false,
                     .networkMode("bridge")
                     .portBindings(ImmutableMap.<String, List<Map<String, String>>> of())
                     .privileged(false)
//                "ReadonlyRootfs": false,
                     .publishAllPorts(false)
                     .restartPolicy(ImmutableMap.<String, String> of("MaximumRetryCount", "2", "Name", "on-failure"))
//                "LogConfig": {
//                    "Config": null,
//                    "Type": "json-file"
//                },
                     .securityOpt(null)
                     .volumesFrom(null)
//                "Ulimits": [{}],
//                "VolumeDriver": ""
                     .build())
               .hostnamePath("/var/lib/docker/containers/ba033ac4401106a3b513bc9d639eee123ad78ca3616b921167cd74b20e25ed39/hostname")
               .hostsPath("/var/lib/docker/containers/ba033ac4401106a3b513bc9d639eee123ad78ca3616b921167cd74b20e25ed39/hosts")
//            "LogPath": "/var/lib/docker/containers/1eb5fabf5a03807136561b3c00adcd2992b535d624d5e18b6cdc6a6844d9767b/1eb5fabf5a03807136561b3c00adcd2992b535d624d5e18b6cdc6a6844d9767b-json.log",
               .id("ba033ac4401106a3b513bc9d639eee123ad78ca3616b921167cd74b20e25ed39")
               .image("04c5d3b7b0656168630d3ba35d8889bd0e9caafcaeb3004d2bfbc47e7c5d35d2")
               .mountLabel("")
               .name("/boring_euclid")
               .networkSettings(NetworkSettings.builder()
                     .bridge("")
                     .sandboxId("")
                     .hairpinMode(false)
                     .linkLocalIPv6Address("")
                     .linkLocalIPv6PrefixLen(0)
                     .ports(null)
                     .sandboxKey("")
                     .secondaryIPAddresses(null)
                     .secondaryIPv6Addresses(null)
                     .endpointId("")
                     .gateway("")
                     .globalIPv6Address("")
                     .globalIPv6PrefixLen(0)
                     .ipAddress("")
                     .ipPrefixLen(0)
                     .ipv6Gateway("")
                     .macAddress("")
                     .networks(ImmutableMap.<String, NetworkSettings.Details> of(
                           "bridge", NetworkSettings.Details.create("", "", "", 0, "", "", 0, "")))
                     .build())
               .path("/bin/sh")
               .node(null)
               .processLabel("")
               .resolvConfPath("/var/lib/docker/containers/ba033ac4401106a3b513bc9d639eee123ad78ca3616b921167cd74b20e25ed39/resolv.conf")
//            "RestartCount": 1,
               .state(State.create(0, true, 9, "2015-01-06T15:47:32.072697474Z", "2015-01-06T15:47:32.080254511Z", false, false, "running", false,
                     // We don't have the "Dead" field in this API version! 
                     false, 
                     ""
//                "Paused": false,
//                "Running": true,
                     ))
//            "Mounts": [
//                {
//                    "Source": "/data",
//                    "Destination": "/data",
//                    "Mode": "ro,Z",
//                    "RW": false
//                }
               .build();
      }
   }

}

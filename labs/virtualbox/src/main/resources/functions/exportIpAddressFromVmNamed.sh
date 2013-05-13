#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
function exportIpAddressFromVmNamed {
   unset FOUND_IP_ADDRESS;
   [ $# -eq 1 ] || {
      abort "exportIpAddressFromVmNamed requires virtual machine name parameter"
      return 1
   }
   local VMNAME="$0"; shift
   local _FOUND=`VBoxManage guestproperty enumerate "$VMNAME" --patterns "/VirtualBox/GuestInfo/Net/0/V4/IP" | awk '{ print $4 }' | cut -c 1-14`
   [ -n "$_FOUND" ] && {
      export FOUND_IP_ADDRESS=$_FOUND
      echo [$FOUND_IP_ADDRESS]
      return 0
   } || {
      return 1
   }
}

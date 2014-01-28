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

FROM centos:6.4
MAINTAINER Andrea Turli <andrea.turli@gmail.com>

# RUN yum -y groupinstall 'Development Tools'
RUN yum -y install openssh-server openssh-clients

RUN chkconfig sshd on
RUN service sshd start
RUN echo 'root:password' | chpasswd

EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]
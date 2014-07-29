#!/bin/bash

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

# Usage examples:
#
# Verify a release candidate
# $ ./fetch_jclouds_rc.sh 2.0.0 https://dist.apache.org/repos/dist/dev/jclouds /tmp

VERSION=$1
REPO=$2
DL_DIR=$3

for a in ${REPO}/org/apache/jclouds/jclouds/${VERSION}/jclouds-${VERSION}-source-release.tar.gz \
    ${REPO}/org/apache/jclouds/labs/jclouds-labs/${VERSION}/jclouds-labs-${VERSION}-source-release.tar.gz \
    ${REPO}/org/apache/jclouds/chef/jclouds-chef/${VERSION}/jclouds-chef-${VERSION}-source-release.tar.gz \
    ${REPO}/org/apache/jclouds/jclouds-karaf/${VERSION}/jclouds-karaf-${VERSION}-source-release.tar.gz \
    ${REPO}/org/apache/jclouds/cli/jclouds-cli/${VERSION}/jclouds-cli-${VERSION}-source-release.tar.gz \
    ${REPO}/org/apache/jclouds/labs/jclouds-labs-openstack/${VERSION}/jclouds-labs-openstack-${VERSION}-source-release.tar.gz \
    ${REPO}/org/apache/jclouds/labs/jclouds-labs-aws/${VERSION}/jclouds-labs-aws-${VERSION}-source-release.tar.gz \
    ${REPO}/org/apache/jclouds/labs/jclouds-labs-google/${VERSION}/jclouds-labs-google-${VERSION}-source-release.tar.gz; do

    wget -P ${DL_DIR} --no-check-certificate ${a}{,.asc,.md5,.sha1};

done

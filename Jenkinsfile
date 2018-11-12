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

pipeline {
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 240, unit: "MINUTES")
    }

    triggers {
        pollSCM("H/15 * * * *")
    }

    tools {
        jdk "JDK 1.8 (latest)"
        maven "Maven 3.0.5"
    }

    agent {
        label "ubuntu"
    }

    stages {
        stage("Build and Unit Test") {
            steps {
                sh "mvn clean deploy checkstyle:checkstyle -Psrc -e -Pjenkins -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dmaven.test.failure.ignore=true"
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', keepLongStdio: true
                }
            }
        }
    }
}

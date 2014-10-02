# Docker as a local cloud provider
jclouds-docker is a local cloud provider modelled on [docker](http://www.docker.io). Similar to other jclouds supported
providers, it supports the same portable abstractions offered by jclouds.

##Setup

Please follow these steps to configure your workstation for jclouds-docker:

- install the latest Docker release (please visit https://docs.docker.com/installation/)
If you are using boot2docker, notice that from version v1.3.0 the Docker daemon is set to use an encrypted TCP socket (--tls, or --tlsverify), 
then you need to create a p12 certificate using the following command:
      
      `openssl pkcs12 -export -out $HOME/.jclouds/docker.p12 -inkey $HOME/.boot2docker/certs/boot2docker-vm/key.pem -in $HOME/.boot2docker/certs/boot2docker-vm/cert.pem -certfile $HOME/.boot2docker/certs/boot2docker-vm/ca.pem`

#How it works


                                               ---------------   -------------
                                              |   Image(s)    | |   Node(s)   |
                                              ---------------   -------------
     ---------    docker remote api           ----------------------------------------
    | jclouds | ---------------------------> |              DOCKER_HOST              |
     ---------                               ----------------------------------------

##Components

- jclouds \- acts as a java client to access to docker features
- DOCKER_HOST \- hosts Docker API, NB: jclouds-docker assumes that the latest Docker is installed
- Image \- it is a docker image that can be started.
- Node \- is a docker container

## Assumptions

- jclouds-docker assumes that the images specified using the template are ssh'able.

--------------

#Notes:
- jclouds-docker is still at alpha stage please report any issues you find at [jclouds issues](https://issues.apache.org/jira/browse/JCLOUDS)
- jclouds-docker has been tested on Mac OSX, it might work on Linux iff vbox is running and set up correctly. However, it has never been tried on Windows.

--------------

#Troubleshooting
As jclouds docker support is quite new, issues may occasionally arise. Please follow these steps to get things going again:

1. Remove all containers
    
    `$ docker ps -aq | xargs docker rm -f`
2. remove all the images

    `$ docker images -q | xargs docker rmi -f`

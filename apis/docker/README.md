# Docker as a local cloud provider

jclouds-docker is a local cloud provider modelled on [docker](http://www.docker.io). Similar to other jclouds supported
providers, it supports the same portable abstractions offered by jclouds.

## Setup

Please follow these steps to configure your workstation for jclouds-docker:

- install the latest Docker release (please visit https://docs.docker.com/installation/)
- [enable remote access](https://docs.docker.com/engine/quickstart/#bind-docker-to-another-host-port-or-a-unix-socket) to Docker

### Sample configuration for Linux systems using systemd

Run following commands on a machine where is the Docker Engine installed.
It enables remote access (plain TCP - only for loopback address 127.0.0.1)
on standard port `2375`.

```bash
# switch to root account
sudo su -

# create override for docker start-script
mkdir /etc/systemd/system/docker.service.d
cat << EOT > /etc/systemd/system/docker.service.d/allow-tcp.conf
[Service]
ExecStart=
ExecStart=/usr/bin/docker daemon -H fd:// -H tcp://
EOT

# reload configuration and restart docker daemon
systemctl daemon-reload
systemctl restart docker

# close the 'root' session
exit
```

If the `-H fd://` Docker daemon parameter doesn't work on your Linux (e.g. Fedora),
then replace it by `-H unix:///var/run/docker.sock`

Find more details in [Control and configure Docker with systemd](https://docs.docker.com/engine/admin/systemd/) guide.

### Running live tests

The `DOCKER_HOST` environment variable has to be configured as it's used as a value for `test.docker.endpoint` system property.

```
export DOCKER_HOST="http://localhost:2375/"
mvn -Plive integration-test
```

Notice, if you are using [Docker for Mac](https://docs.docker.com/engine/installation/mac/), Docker Remote API may not be available over HTTP. 
In the [Known issue]{https://docs.docker.com/docker-for-mac/troubleshoot/#known-issues) Docker team recommend to use [socat](http://www.dest-unreach.org/socat/)
to expose `docker.sock` over tcp. You may find useful the following simple commands:

```
docker run -d -v /var/run/docker.sock:/var/run/docker.sock -p 127.0.0.1:1234:1234 bobrik/socat TCP-LISTEN:1234,fork UNIX-CONNECT:/var/run/docker.sock
```

and then 

```
export DOCKER_HOST=http://localhost:1234
mvn -Plive integration-test
```

# How it works


                                               ---------------   -------------
                                              |   Image(s)    | |   Node(s)   |
                                              ---------------   -------------
     ---------    docker remote api           ----------------------------------------
    | jclouds | ---------------------------> |              DOCKER_HOST              |
     ---------                               ----------------------------------------

## Components

- jclouds \- acts as a java client to access to docker features
- DOCKER_HOST \- hosts Docker API, NB: jclouds-docker assumes that the latest Docker is installed
- Image \- it is a docker image that can be started.
- Node \- is a docker container

## Assumptions

- jclouds-docker assumes that the images specified using the template are ssh'able.

--------------

# Notes:
- report any issues you find at [jclouds issues](https://issues.apache.org/jira/browse/JCLOUDS)
- jclouds-docker has been tested on Mac OSX and Linux. However, it has never been tried on Windows.

--------------

# Troubleshooting

As jclouds docker support is quite new, issues may occasionally arise. 
You can try to remove all containers to get things going again:

```bash
docker rm -f `docker ps -aq`
```

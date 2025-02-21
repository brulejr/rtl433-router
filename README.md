# Overiew
This project watches an MQTT topic on which the `rtl_433` publishes raw 433HMz events. It then does the following with
these events:
- Matches the event to expect data models (Unknown events are logged and dumped)
- Filters them based upon a catalog of known devices
- Broadcasts the filtered devices to a new MQTT topic on which systems like Home Assistant can listen

This software is designed to run on a Raspberry Pi within Docker.

# Getting Started
This section shows how to setup the `rtl_433` software within Docker as well as build / publish this project.

### RTL433 Setup
Create the directory structure
```bash
mkdir -p rtl_433/mosquitto
```
Create the following `docker-compose.yml` file on a Raspberry Pi:
```yaml
version: "3"

services:

  mosquitto:
    image: eclipse-mosquitto
    hostname: mosquitto
    container_name: mosquitto
    restart: unless-stopped
    ports:
      - "1883:1883"
      - "9001:9001"
    volumes:
      - ./mosquitto/mosquitto.conf:/mosquitto/config/mosquitto.conf

  rtl_433:
    container_name: rtl_433
    image: hertzg/rtl_433:latest
    restart: unless-stopped
    devices:
      - "/dev/bus/usb/001/005"
    command:
      - "-Mtime:unix:usec:utc"
      - "-Fmqtt://mosquitto:1883,retain=1"
```
Create the following `mosquitto/mosquitto.conf` file
```yaml
allow_anonymous true
listener 1883
listener 9001
protocol websockets
persistence true
persistence_file mosquitto.db
persistence_location /mosquitto/data/
```
Start the service by running the following
```bash
docker compose up -d
```

### Build
To build the container for this application, run the following for **docker**:
```bash
./gradlew --console=plain -no-daemon clean build jibDockerBuild
```
Or, the following for **podman**:
```bash
./gradlew --console=plain -no-daemon clean build jibDockerBuild -Djib.dockerClient.executable=$(which podman)
```
This will generate the container in your local **docker** / **podman** repository.

### Publish
To publish to a local docker registry, `docker.brulenet.dev`, run the following commands.

Obtain the image id from local docker.
```bash
docker image ls
```

Run the following to push the previously-built image to the registry.
```bash
podman push <imageid> docker://docker.brulenet.dev/brulejr/rtl433-router:<version>
```
replacing `<imageid>` with the image id from the previous command and `<version>` with the release version number

# Resources

Podman / Docker
- [StackOverflow - How to push an image to the Docker registry using podman](https://stackoverflow.com/questions/64199116/how-to-push-an-image-to-the-docker-registry-using-podman)
- [Medium - Interacting with Docker Registry without Docker Client](https://blog.pentesteracademy.com/interacting-with-docker-registry-without-docker-client-2d6cd08ff244)
- [Unit Test with Kotlin Flow](https://levelup.gitconnected.com/unit-test-with-kotlin-flow-7e6f675e5b14)
- [Exploring Jib's support for multi-platform container images in Java](https://colinchjava.github.io/2023-09-18/09-35-39-681593-exploring-jibs-support-for-multi-platform-container-images-in-java/)

Libraries
- [GitHub - Turbine](https://github.com/cashapp/turbine)

Kotlin Techniques
- [Sealed Classes Instead of Exceptions in Kotlin](https://phauer.com/2019/sealed-classes-exceptions-kotlin/)
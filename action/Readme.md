## How to Build

Currently there is no automated build pipeline to create a new docker image using the latest version of the action. To publish a new version of the action one must first create an updated version of the Action's docker base image:


### 1. Build an uberjar from the sources

Create an uberjar from the latest code using `clojure.tools.build`:


```sh
clj -T:build uber
```

### 2. Create a statically linked Binary

First follow [these steps](https://www.graalvm.org/22.0/reference-manual/native-image/StaticImages/index.html) to setup Graal and native-image. Please use Java 21 as base.

Then build a staticly linked byinary by running

```sh
/usr/lib/jvm/graalvm-jdk-21/bin/native-image \
                -jar target/fill-gh-pr-template.jar \
                -H:+ReportExceptionStackTraces \
                --report-unsupported-elements-at-runtime \
                --initialize-at-build-time \
                --no-fallback \
                --no-server \
                --static \
                --libc=musl \
                --native-compiler-path=/opt/src/x86_64-linux-musl-native/bin/x86_64-linux-musl-gcc \
                target/fill-gh-pr-template
```

> [!NOTE]  
> You need to change the `--native-compiler-path=[...]` to match the path on you machine.


### 3. Build the Docker Image

```sh
docker build -t io.clockworks/fill-pr-template-gh-action:latest -f docker/Dockerfile .
docker tag io.clockworks/fill-pr-template-gh-action:latest io.clockworks/fill-pr-template-gh-action:0.0.0
```

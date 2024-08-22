Build a native binary:
```sh
/usr/lib/jvm/graalvm-jdk-21/bin/native-image \
                -jar action/target/fill-gh-pr-template.jar \
                -H:+ReportExceptionStackTraces \
                --report-unsupported-elements-at-runtime \
                --initialize-at-build-time \
                --no-fallback \
                --no-server \
                --static \
                --libc=musl \
                --native-compiler-path=/opt/src/x86_64-linux-musl-native/bin/x86_64-linux-musl-gcc \
                action/target/fill-gh-pr-template
```

Build Docker image locally:
```sh
docker build -t io.clockworks/fill-pr-template-gh-action .
```
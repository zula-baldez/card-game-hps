FROM gradle:alpine AS builder
RUN $JAVA_HOME/bin/jlink \
	 --add-modules ALL-MODULE-PATH \
	 --strip-debug \
	 --no-man-pages \
	 --no-header-files \
	 --compress=2 \
	 --output /javaruntime
WORKDIR /app

COPY ./settings.gradle.kts ./settings.gradle.kts
COPY ./gateway-server/build.gradle.kts ./gateway-server/build.gradle.kts
COPY ./gateway-server/src ./gateway-server/src
COPY common ./common

#RUN cd gateway-server; gradle build -x test | exit 0

RUN gradle build -x test

FROM alpine:latest

ENV JAVA_HOME=/jre
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=builder /javaruntime $JAVA_HOME
COPY --from=builder /app/gateway-server/build/libs/gateway-server-0.0.1-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]
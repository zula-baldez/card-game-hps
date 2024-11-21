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
COPY ./avatars-service/build.gradle.kts ./avatars-service/build.gradle.kts
COPY ./avatars-service/src ./avatars-service/src
COPY common ./common

#RUN cd avatars-service; gradle build -x test | exit 0

RUN gradle build -x test

FROM alpine:latest

ENV JAVA_HOME=/jre
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=builder /javaruntime $JAVA_HOME
COPY --from=builder /app/avatars-service/build/libs/avatars-service-0.0.1-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]
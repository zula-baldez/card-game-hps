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
COPY ./room-service/build.gradle.kts ./room-service/build.gradle.kts
COPY ./room-service/src ./room-service/src
COPY common ./common

#RUN cd room-service; gradle build -x test | exit 0

RUN gradle build -x test

FROM alpine:latest

ENV JAVA_HOME=/jre
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=builder /javaruntime $JAVA_HOME
COPY --from=builder /app/room-service/build/libs/room-service-0.0.1-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]
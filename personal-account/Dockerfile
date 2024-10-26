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
COPY ./personal-account/build.gradle.kts ./personal-account/build.gradle.kts
COPY ./personal-account/src ./personal-account/src
COPY common ./common

#RUN cd personal-account; gradle build -x test | exit 0

RUN gradle build -x test

FROM alpine:latest

ENV JAVA_HOME=/jre
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=builder /javaruntime $JAVA_HOME
COPY --from=builder /app/personal-account/build/libs/personal-account-0.0.1-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]
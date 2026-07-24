# FROM eclipse-temurin:21-jdk-alpine
# WORKDIR /app

# Copy file jar đã build từ máy host vào container
# COPY target/*.jar music-streaming-platform-0.0.1-SNAPSHOT.jar

# Expose port Spring Boot (8080)
# EXPOSE 8080

# Chạy ứng dụng
# ENTRYPOINT ["java", "-jar", "music-streaming-platform-0.0.1-SNAPSHOT.jar"]

#
# Multi-stage Dockerfile for Java Spring Boot application
#

# Stage 1: Build stage - Where the magic happens
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /usr/src/project

ENV TZ=Asia/Ho_Chi_Minh

# Copy Maven files first because we're smart like that
# (Docker cache is your best friend, treat it right)
COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
RUN chmod +x mvnw

# Download dependencies - this is usually the longest stage
# Trust me, without caching this step, you'll age visibly
RUN ./mvnw dependency:go-offline

# Now for the actual source code (the star of the show!)
COPY src/ src/

# Build the thing! Skip tests because YOLO production deployment
# (Just kidding, run your tests in CI/CD like a responsible adult)
RUN ./mvnw clean package -DskipTests

# Time for some JAR surgery
RUN jar xf target/music-streaming-platform-0.0.1-SNAPSHOT.jar

# Let jdeps be the detective and figure out what we actually need
RUN jdeps  \
    --ignore-missing-deps  \
    -q --recursive  \
    --multi-release 21 \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*' \
    target/music-streaming-platform-0.0.1-SNAPSHOT.jar > deps.info

# Create the minimalist JRE
# jdk.crypto.ec is for HTTPS, otherwise ignore it
RUN jlink \
    --add-modules $(cat deps.info),jdk.crypto.ec \
    --strip-debug  \
    --compress 2  \
    --no-header-files  \
    --no-man-pages \
    --output /jre-minimalist

# Stage 2: The lean, mean, production machine
FROM alpine:3.21.3 AS final

RUN apk add --no-cache ffmpeg

# Set up our custom Java home
ENV JAVA_HOME=/opt/java/jre-minimalist
ENV PATH=$JAVA_HOME/bin:$PATH

# Move in our custom-built JRE
COPY --from=build /jre-minimalist $JAVA_HOME

# Security 101: Don't run as root
RUN addgroup -S springgroup \
    && adduser -S springuser -G springgroup \
    && mkdir -p /app \
    && chown -R springuser:springgroup /app

# Bring over our precious application
COPY --from=build /usr/src/project/target/music-streaming-platform-0.0.1-SNAPSHOT.jar /app/

WORKDIR /app
USER springuser
EXPOSE 8080

HEALTHCHECK --interval=30s \
    --timeout=5s \
    --start-period=40s \
    --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

#
# Launch sequence initiated! 🚀
#

# These JVM flags are like performance vitamins for your container:
# - MaxRAMPercentage: Don't be greedy, use 75% max
# - InitialRAMPercentage: Start with 50% like a reasonable person
# - MaxMetaspaceSize: Uncontrollable growing is cancer, in form of a deadly OutOfMemoryError
# - UseG1GC: Because G1 is the cool garbage collector, fine tune it for your actual usage
#
# Add other parameters to tailor to your project's needs
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-XX:MaxMetaspaceSize=512m", "-XX:+UseG1GC", "-jar", "music-streaming-platform-0.0.1-SNAPSHOT.jar"]
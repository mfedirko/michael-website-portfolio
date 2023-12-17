FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN mkdir -p ./target
RUN ./mvnw clean package
RUN mv ./target/*.jar ./application.jar

CMD ["java", "-jar", "application.jar"]
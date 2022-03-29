FROM maven:eclipse-temurin
#Add a new User (not root)
RUN useradd -ms /bin/bash app
WORKDIR /home/app
ARG SRC_ROOT=./
COPY ${SRC_ROOT} .
RUN mvn clean install
RUN mvn test

ENTRYPOINT ["mvn", "spring-boot:run"]
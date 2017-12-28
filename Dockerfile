FROM mvn:3-jdk-8
RUN mkdir /work
ADD . /work
WORKDIR /work
RUN mvn spring-boot:run

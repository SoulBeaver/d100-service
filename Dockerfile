FROM java:openjdk-8

WORKDIR /var/d100

ADD build/libs/d100-web.jar /var/d100/d100-web.jar

EXPOSE 9000 9000

ENTRYPOINT ["java", "-jar", "d100-web.jar"]
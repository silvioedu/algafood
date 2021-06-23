FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/*jar /app/algafood-auth.jar
COPY wait-for-it.sh /wait-for-it.sh

RUN chmod +x /wait-for-it.sh

EXPOSE 8081

CMD ["java", "-jar", "algafood-auth.jar"]

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/thais.almeida-0.0.1-SNAPSHOT.jar app.jar

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod 755 /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
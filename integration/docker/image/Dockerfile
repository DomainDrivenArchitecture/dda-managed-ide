FROM ubuntu

RUN apt-get update > /dev/null \
    && apt-get upgrade -y > /dev/null && \
    apt-get install openjdk-11-jre -y > /dev/null && \
    apt-get install curl gnupg2 apt-utils sudo -y > /dev/null

COPY target/uberjar/dda-managed-ide-standalone.jar /app/dda-managed-ide-standalone.jar
COPY integration/docker/image/ide.edn /app/ide.edn

# check prerequisits
RUN java -jar /app/dda-managed-ide-standalone.jar --serverspec /app/ide.edn

RUN java -jar /app/dda-managed-ide-standalone.jar /app/ide.edn

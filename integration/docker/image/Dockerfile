FROM ubuntu

RUN apt-get update && apt-get upgrade -y && apt-get install openjdk-8-jre -y

RUN apt-get install curl gnupg2 apt-utils sudo -y

COPY target/uberjar/dda-managed-ide-standalone.jar /app/dda-managed-ide-standalone.jar
COPY integration/docker/image/ide.edn /app/ide.edn

# check prerequisits
RUN java -jar /app/dda-managed-ide-standalone.jar --serverspec /app/ide.edn

RUN java -jar /app/dda-managed-ide-standalone.jar /app/ide.edn

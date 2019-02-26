FROM dda-managed-ide

RUN apt-get install curl -y
RUN update-java-alternatives --set java-1.8.0-openjdk-amd64

RUN curl -L -o /app/serverspec.jar https://github.com/DomainDrivenArchitecture/dda-serverspec-crate/releases/download/1.3.0/dda-serverspec-standalone.jar

COPY integration/docker/test/serverspec.edn /app/serverspec.edn

RUN java -jar /app/serverspec.jar /app/serverspec.edn

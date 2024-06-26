FROM ubuntu:20.04

ARG DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Ho_Chi_Minh

# Install required packages

RUN apt-get update && \
    apt-get install -y build-essential && \
    apt-get install -y software-properties-common && \
    apt-get install -y --no-install-recommends python3

# Install Java.

# RUN \
#   echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
#   add-apt-repository -y ppa:webupd8team/java && \
#   apt-get update && \
#   apt-get install -y oracle-java8-installer && \
#   rm -rf /var/lib/apt/lists/* && \
#   rm -rf /var/cache/oracle-jdk8-installer

# ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

RUN apt-get install -y --no-install-recommends gnupg2 software-properties-common 
RUN \
    add-apt-repository -y ppa:linuxuprising/java && \ 
    apt-get update && \
    echo oracle-java17-installer shared/accepted-oracle-license-v1-3 select true | debconf-set-selections && \
    echo oracle-java17-installer shared/accepted-oracle-license-v1-3 seen true | debconf-set-selections && \
    echo oracle-java17-installer shared/accepted-oracle-license-v1-3 boolean true | debconf-set-selections && \
    apt-get install -y oracle-java17-installer --no-install-recommends && \ 
    rm -rf /var/lib/apt/lists/* && \
    rm -rf /var/cache/oracle-jdk17-installer
    
ENV JAVA_HOME /usr/lib/jvm/java-17-oracle


# Install Storm.

ENV STORM_USER=usama \
    STORM_CONF_DIR=/conf \
    STORM_DATA_DIR=/data \
    STORM_LOG_DIR=/logs

# Add a user and make dirs
RUN set -ex; \
    adduser "$STORM_USER"; \
    mkdir -p "$STORM_CONF_DIR" "$STORM_DATA_DIR" "$STORM_LOG_DIR"; \
    chown -R "$STORM_USER:$STORM_USER" "$STORM_CONF_DIR" "$STORM_DATA_DIR" "$STORM_LOG_DIR"``

ARG DISTRO_NAME=apache-storm-2.6.1

# Download Apache Storm.
RUN set -ex; \
    wget -q "http://www.apache.org/dist/storm/$DISTRO_NAME/$DISTRO_NAME.tar.gz"; \
    export GNUPGHOME="$(mktemp -d)"; \
    tar -xzf "$DISTRO_NAME.tar.gz"; \
    chown -R "$STORM_USER:$STORM_USER" "$DISTRO_NAME"; \
    rm "$DISTRO_NAME.tar.gz";

ENV PATH $PATH:/$DISTRO_NAME/bin

ADD storm.yaml /conf

RUN chown -R "$STORM_USER" "$STORM_CONF_DIR" "$STORM_DATA_DIR" "$STORM_LOG_DIR"


# Install Maven.

ENV MAVEN_VERSION="3.9.6" \
    M2_HOME=/usr/lib/mvn

RUN cd /tmp && \
    wget "http://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" && \
    tar -zxvf "apache-maven-$MAVEN_VERSION-bin.tar.gz" && \
    mv "apache-maven-$MAVEN_VERSION" "$M2_HOME" && \
    ln -s "$M2_HOME/bin/mvn" /usr/bin/mvn && \
    rm -R /tmp/*

RUN mkdir /theproject
WORKDIR /theproject

CMD storm nimbus
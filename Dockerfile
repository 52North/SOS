# Building SOS project

FROM ubuntu:14.04
MAINTAINER gonephishing <riteshoneinamillion@gmail.com>

RUN apt-get update
RUN apt-get install -y unzip wget maven
RUN wget https://github.com/52North/SOS/archive/develop.zip
RUN unzip develop.zip
WORKDIR /SOS-develop
RUN ["mvn", "clean", "install"]

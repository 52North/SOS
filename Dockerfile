# Building SOS project

FROM ubuntu:14.04
MAINTAINER gonephishing <riteshoneinamillion@gmail.com>

# Update and install basic requirements
RUN apt-get update
RUN apt-get install -y unzip wget maven

# Get the development branch from github and extract it
RUN wget https://github.com/52North/SOS/archive/develop.zip
RUN unzip develop.zip

# Chaange working directory to the project and install it using maven
WORKDIR /SOS-develop
RUN ["mvn", "clean", "install"]

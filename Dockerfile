# Building SOS project

FROM java:7
MAINTAINER gonephishing <riteshoneinamillion@gmail.com>

# Update and install basic requirements
RUN apt-get update
RUN apt-get install -y wget maven git

# Get the development branch from github and extract it
RUN git clone https://github.com/52North/SOS.git

# Chaange working directory to the project and install it using maven
WORKDIR /SOS
RUN ["mvn", "clean", "install"]

FROM jboss/wildfly:latest
MAINTAINER Alexander Ryan (alexander_the_1st@hotmail.com) [MT1ST]
# Because jboss/wildfly:latest doesn't use a higher version of Centos (Uses 7, as I understand, by looking at it in Docker Desktop)
# We need to update its yum repos to the appropriate ones.
# We do it this way instead of using a "FROM maven:latest" reference, because we need the regular reference to the JBOSS instance.
# May be able to test that in the future if this works properly, so that this...isn't as necessary.
# We just need the JBOSS_ROOT so that mvn install takes the existing JBOSS_ROOT to the project's POM file.
# First things first though, we need to change ourselves back to root, because USER jboss does not have the ability to access root.
USER root
RUN ["sed", "-i", "-e", "s%^mirrorlist%#mirrorlist/g%", "/etc/yum.repos.d/CentOS-Base.repo"]
RUN ["sed", "-i", "-e", "s%^#baseurl=http:\/\/mirror.%baseurl=https:\/\/vault.%", "/etc/yum.repos.d/CentOS-Base.repo"]
RUN ["yum", "clean", "all"]
RUN ["yum", "makecache"]
RUN ["yum", "install", "-y", "maven"]
WORKDIR /
# Just copying local to Docker image space - using a tmp folder to avoid having the project folders overfill the image.
COPY ./ ./tmp/Assignment/
WORKDIR /tmp/Assignment/
RUN ["mvn", "install"]
# Okay, now to force the actual server to start, defaulting to jboss user to ensure that, unless we explicitly want to, that we don't get root access.
USER jboss
ENTRYPOINT ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
EXPOSE 8080
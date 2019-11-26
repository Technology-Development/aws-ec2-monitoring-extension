FROM alpine:latest
RUN apk add --no-cache openjdk8
WORKDIR /opt/appdynamics/machine-agent
COPY --from=dtr.corp.appdynamics.com/appdynamics/machine-agent /opt/appdynamics/machine-agent .

# RUN apt-get update
# RUN sleep 60
RUN apk update && apk add wget unzip

RUN sleep 60

ADD target/AWSEC2Monitor-*.zip /opt/appdynamics/machine-agent/monitors

RUN unzip -q "/opt/appdynamics/machine-agent/monitors/AWSEC2Monitor-*.zip" -d /opt/appdynamics/machine-agent/monitors
RUN find /opt/appdynamics/machine-agent/monitors -name '*.zip' -delete


CMD ["sh", "-c", "java ${MACHINE_AGENT_PROPERTIES} -jar /opt/appdynamics/machine-agent/machineagent.jar"]
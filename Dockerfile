FROM dtr.corp.appdynamics.com/appdynamics/machine-agent:latest AS MA

RUN apt-get update
RUN sleep 60
RUN apt-get -y install wget unzip

RUN sleep 60

ADD target/AWSEC2Monitor-*.zip /opt/appdynamics/machine-agent/monitors

RUN unzip -q "/opt/appdynamics/machine-agent/monitors/AWSEC2Monitor-*.zip" -d /opt/appdynamics/machine-agent/monitors
RUN find /opt/appdynamics/machine-agent/monitors -name '*.zip' -delete


CMD ["sh", "-c", "java ${MACHINE_AGENT_PROPERTIES} -jar /opt/appdynamics/machine-agent/machineagent.jar"]
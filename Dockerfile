FROM eclipse-temurin:21-jre-alpine

LABEL authors="hatrongvu"

WORKDIR /opt/service

COPY /target/*.jar /opt/service/app.jar

COPY /src/main/resources /opt/service/resources_default

COPY entrypoint.sh entrypoint.sh

RUN chmod +x /opt/service/entrypoint.sh

RUN chgrp -R 0 ./ && chmod -R g=u ./

RUN ls -l /

RUN cat /opt/service/entrypoint.sh

ENTRYPOINT ["sh","/opt/service/entrypoint.sh"]
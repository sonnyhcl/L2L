# IoT
cd BusinessEntities/vesselIoT
mvn spring-boot:run &

# Enterprises
cd Enterprises/vessel-A
mvn tomcat7:run &
cd ../manager-A
mvn tomcat7:run &
cd ../supplier-A
mvn tomcat7:run &
cd ../logistics-A
mvn tomcat7:run &


# Front-End
cd ../../../L2L-monitor
npm run start





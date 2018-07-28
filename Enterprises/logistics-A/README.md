# Logistics Enterprise
Logistics Enterprise 

## Prerequisites 
- Development Tools
   - IDE: [Intellij IDEA](https://www.jetbrains.com/idea/download)    
   - Database: [MYSQL](https://www.mysql.com/)
## DataBase Configuration
- Make sure you have installed `MySql` and your mysql has a empty schema called `logisticsA`.
- You can customize your own database configuration in [activiti-app.properties](src/main/resources/META-INF/activiti-app/activiti-app.properties)
    - `datasource.url` : jdbc:mysql://127.0.0.1:3306/logisticsA?characterEncoding=UTF-8
    - `datasource.username` : root
    - `datasource.password` : root
## AWS Client Configuration
- You need to alternate  the AWSIoT Client configuration in [activiti-app.properties](src/main/resources/META-INF/activiti-app/activiti-app.properties)
    - `awsiot.clientEndpoint` : AWSIOT client endpoint.
    - `awsiot.clientId` : AWSIoT client identifier, unique string.
    - `awsiot.certificateFile` : the file location of the AWSIoT certification 
    - `awsiot.privateKeyFile` : the file location of  the AWSIoT private key.
## Coordinator and Business Entities Endpoint Configuration
- config the endpoint and other properties relevant to `Cross-Enterprise Coordinators` in [activiti-app.properties](src/main/resources/META-INF/activiti-app/activiti-app.properties)
    - `org.vmcContextPath` : endpoint for `Logistics Manager Coordinator`.
    - `org.lvcContextPath` : endpoint for `Logistics Vessel Coordinator`.
    - `org.wdevContextPath` : endpoint for `Wagon Business Entities`.

## Run Configuration
- Add run configuration for this Maven project.
   ![logistics_maven_run_conf](../../images/logistics_maven_run_conf.png) 
- Login url :  [http://localhost:9031/logistics-A/](http://localhost:9031/logistics-A/), you can login in the system as the default user.
     - `username`: admin
     - `password`: test
## Import logistics process models into the system.
- You need to upload 'Logistics-A.zip' under the root directory `processes` and  publish your app, then  go to processes page to start the selected process.
    ![import_logistics_process](../../images/import_logistics_process.png)
     ![publish_logistics_process](../../images/publish_logistics_process.png)





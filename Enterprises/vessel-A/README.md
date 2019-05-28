# Vessel Enterprise
Vessel Enterprise 

## Prerequisites 
- Development Tools
   - IDE: [Intellij IDEA](https://www.jetbrains.com/idea/download)    
   - Database: [MYSQL](https://www.mysql.com/)
## DataBase Configuration
- Make sure you have installed `MySql` and your mysql has a empty schema called `vesselA`.
- You can customize your own database configuration in [activiti-app.properties](src/main/resources/META-INF/activiti-app/activiti-app.properties)
    - `datasource.url` : jdbc:mysql://127.0.0.1:3306/vesselA?characterEncoding=UTF-8
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
    - `org.vmcContextPath` : endpoint for Vessel Manager Coordinator.
    - `org.lvcContextPath` : endpoint for Logistics Vessel Coordinator.
    - `org.vdevContextPath` : endpoint for Vessel Business Entities.
## Run Configuration
- Add run configuration for this Maven project.
   ![vessel_maven_run_conf](../../images/vessel_maven_run_conf.png) 
- Login url :  [http://localhost:9001/vessel-A/](http://localhost:9001/vessel-A/), you can login in the system as the default user.
     - `username`: admin
     - `password`: test
- Or run the app with terminal
    ```
    mvn tomcat7:run
    ```
## Import vessel process models into the system.
- You need to upload 'Vessel-A.zip' under the root directory `processes` and  publish your app, then  go to processes page to start the selected process.
    ![import_vessel_process](../../images/import_vessel_process.png)
     ![publish_vessel_process](../../images/publish_vessel_process.png)





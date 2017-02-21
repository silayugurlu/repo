# The EUH lighting system
The application is REST API application for an EUH Lighting System Installation.
It contains following services:

- POST **/lighting/installation**
    creates new lamps and gateways.
    link lamps among each other.
    link lamps to a gateway.
- GET **/lighting/installation/{name}**
    list elements and links in installation
- GET **/lighting/checkAllLampsOnline/{name}**
    determines whether all the lamps are online
- GET **/lighting/makeAllLampsOnline/{name}**
    adds the necessary links so that all the lamps become online

This application is a SpringBoot application and built with Maven.
To download and install maven, you can follow the instructions from the links below :  
http://maven.apache.org/download.cgi
http://maven.apache.org/install.html

Also, you need the Java8 installed, please see the link https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html

You can run the application with maven using `mvn spring-boot:run`.
Or you can build the JAR file with `mvn clean package`. Then you can run the JAR file: `java -jar target/lighting-0.1.0.jar`


You can see and call all services via this page while application is running on local: http://localhost:8080/swagger-ui.html#/
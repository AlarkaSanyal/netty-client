# netty-client
# Description
A simple Netty Client application example. This can be played with the project `netty-server` for a demo on how Netty works.

## POST
Based on what color is being passed by this app for a rental booking, this server responds with a model of the car.If a response is received, the status is set to booked. 
````
URL: http://localhost:8065/api/rental/v1
Headers: Content-Type, application/json
````
````
Request
-------
{
    "id": "123",
    "car_type": "sedan",
    "color": "red"
}

Response
--------
{
    "id": "123",
    "status": "booked",
    "car_type": "sedan",
    "model": "Nissan"
}
````

# Prereq
* Maven
* `netty-server` is running

# Notes
Server runs on port 8065, but can be modified in `resources/application.properties`

# Running the server
This can be run from any IDE as a simple Spring Boot app or the following commands can be used for running from CLI
* `mvn clean compile`
* `mvn clean spring-boot:run`

# Logs
Logs are pushed to the `/tmp/netty` directory and can be modified in `resources/logback.xml` file.
To view logs, use the following command
* `tail -f /tmp/netty/netty-client.log`


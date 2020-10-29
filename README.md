# GameHub
We are building a Game Hub which manages games and gamer information. You will capture the games played by
each gamer and the number of points earned in the games.

Following are the responsibility of the system that needs to be built.
- Manage Games 
- Manage Gamers information.  
- Manage Games played by a Gamer and points earned. 
- Find the Gamer with the highest point for a Game. 
- Find the most played Game. 
- Find the average points for a given game across all gamers. 

## Getting Started

You may run this project by building and running this project in a docker container.
The docker container will conveniently serve up the API documentation from the root uri.

    ./gradlew clean test bootJar
    docker build -t gamehub .
    docker run -p 8080:8080 gamehub:latest   
    
Once the container is up, you may navigate to http://localhost:8080 to view the documentation and interact with the app.
    
This application is using Spring WebFlux and the new R2DBC drivers introduced to support functional reactive flows to H2.

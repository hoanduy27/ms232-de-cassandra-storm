
# MS232 - Data Engineering

# Build and run
```sh
    docker compose up -d
    docker exec -it coincident-hashtags-with-apache-storm-storm-nimbus-1 /bin/bash 
    mvn clean install # still got bug
    cd code
    storm jar target/coincident-hashtags-1.2.1.jar coincident_hashtags.ExclamationTopology exclamation-topology-usama
```
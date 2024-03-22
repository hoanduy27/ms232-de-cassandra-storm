
# MS232 - Data Engineering

# Build and run
```sh
    docker compose up -d
    docker exec -it ms232-de-cassandra-storm-storm-nimbus-1 /bin/bash 
    cd code
    mvn clean install 
    storm nimbus

    # Run in the 2nd terminal
    storm ui
    
    # Run in the 3rd terminal
    storm jar target/coincident-hashtags-2.6.1.jar coincident_hashtags.ExclamationTopology exclamation-topology-usama
```
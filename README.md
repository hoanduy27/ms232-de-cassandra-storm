
# MS232 - Data Engineering



# Build and run
```sh
    docker compose up -d
    docker cp cassandra/cassandra.yaml ms232-de-cassandra-storm-cassandra-1:/etc/cassandra/cassandra.yaml
    docker restart ms232-de-cassandra-storm-cassandra-1
```

# Storm progress
- Created 2 spouts to read bronze tables in Cassandra.
- Writing-up 2 Bolts for silver data tables (Only TODO now).
- Set up the storm topology.

# Known bugs
- With cassandra=5.0*: 
  ``` Error initializing connection: Invalid or unsupported protocol version (1); supported versions are (3/v3, 4/v4, 5/v5, 6/v6-beta)))```

- With cassandra=4.1.4: `cassandra.yaml` is conflicted. Has remove some conflicted keys but no luck. :( 


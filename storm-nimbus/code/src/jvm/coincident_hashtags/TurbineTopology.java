package coincident_hashtags;

import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.utils.Utils;
import org.yaml.snakeyaml.Yaml;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;

import java.io.InputStream;
import java.util.*;

public class TurbineTopology {
    
    public static class TurbineSpout extends BaseRichSpout {
        private SpoutOutputCollector collector;
        private Cluster cluster;
        private Session session;
        private String keyspace;
        private String table;
    
        public TurbineSpout(String keyspace, String table) {
            this.keyspace = keyspace;
            this.table = table;
        }
        
        @Override
        public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
            this.collector = collector;
            cluster = Cluster.builder().addContactPoint("cassandra").withPort(9042).build();
            session = cluster.connect(keyspace);
        }
    
        @Override
        public void nextTuple() {
            Statement statement = QueryBuilder.select().all().from(keyspace, table);
            ResultSet resultSet = session.execute(statement);
            for (Row row : resultSet) {
                // Assuming the row format, you may need to adjust this according to your Cassandra schema
                java.util.UUID id = row.getUUID("id");
                Date recorded_date = row.getDate("recorded_date");
                String device_id = row.getString("device_id");
                String window = row.getString("window");
                Float rpm = row.getFloat("rpm");
                Float angle = row.getFloat("angle");
      
                // Emit tuple to the topology
                collector.emit(new Values(id, recorded_date, device_id, window, rpm, angle));
            }
        }
    
        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "recorded_date", "device_id", "window", "rpm", "angle"));
        }
    
        // @Override
        // public void close() {
        //     session.close();
        //     cluster.close();
        // }
    }

    public static class WeatherSpout extends BaseRichSpout {
        private SpoutOutputCollector collector;
        private Cluster cluster;
        private Session session;
        private String keyspace;
        private String table;
    
        public WeatherSpout(String keyspace, String table) {
            this.keyspace = keyspace;
            this.table = table;
        }
        
        @Override
        public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
            this.collector = collector;
            cluster = Cluster.builder().addContactPoint("cassandra").withPort(9042).build();
            session = cluster.connect(keyspace);
        }
    
        @Override
        public void nextTuple() {
            Statement statement = QueryBuilder.select().all().from(keyspace, table);
            ResultSet resultSet = session.execute(statement);
            for (Row row : resultSet) {
                // Assuming the row format, you may need to adjust this according to your Cassandra schema
                java.util.UUID id = row.getUUID("id");
                Date recorded_date = row.getDate("recorded_date");
                String device_id = row.getString("device_id");
                String window = row.getString("window");
                Float temperature = row.getFloat("temperature");
                Float humidity = row.getFloat("humidity");
                Float windspeed = row.getFloat("windspeed");
                Float winddirection = row.getFloat("winddirection");
      
                // Emit tuple to the topology
                collector.emit(new Values(id, recorded_date, device_id, window, temperature, humidity, windspeed, winddirection));
            }
        }
    
        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "recorded_date", "device_id", "window", "temperature", "windspeed", "winddirection"));
        }
    
        // @Override
        // public void close() {
        //     session.close();
        //     cluster.close();
        // }
    }

    public static class TurbineSilverBolt extends BaseRichBolt {
        private OutputCollector collector;

        @Override
        public void prepare(Map<String, Object> stormConf, TopologyContext context, OutputCollector collector) {
            this.collector = collector;
        }

        @Override
        public void execute(org.apache.storm.tuple.Tuple input) {
            // TODO: Do aggregation

            // Process the data as per your requirements
            System.out.println(input);

            // Acknowledge the tuple
            collector.ack(input);
        }

        @Override
        public void declareOutputFields(org.apache.storm.topology.OutputFieldsDeclarer declarer) {
            // No output fields for this bolt
        }
    }

    public static class WeatherSilverBolt extends BaseRichBolt {
        private OutputCollector collector;

        @Override
        public void prepare(Map<String, Object> stormConf, TopologyContext context, OutputCollector collector) {
            this.collector = collector;
        }

        @Override
        public void execute(org.apache.storm.tuple.Tuple input) {
            // TODO: Do aggregation

            // Process the data as per your requirements
            System.out.println(input);

            // Acknowledge the tuple
            collector.ack(input);
        }

        @Override
        public void declareOutputFields(org.apache.storm.topology.OutputFieldsDeclarer declarer) {
            // No output fields for this bolt
        }
    }

    public static void main(String[] args) throws Exception {
        // Create a Storm topology builder
        TopologyBuilder builder = new TopologyBuilder();

        // Set up the Cassandra spout
        builder.setSpout("turbineSpout", new TurbineSpout("iotsolution", "bronze_turbine_sensor"));
        
        builder.setSpout("weatherSpout", new TurbineSpout("iotsolution", "bronze_weather_sensor"));

        // Set up a bolt for processing the tuples
        builder.setBolt("turbineBolt", new TurbineSilverBolt())
                .fieldsGrouping("turbineSpout", new Fields("device_id", "recorded_date"));

        builder.setBolt("turbineBolt", new WeatherSilverBolt())
                .fieldsGrouping("turbineSpout", new Fields("device_id", "recorded_date"));

        // Create the Storm configuration
        InputStream inputStream = TurbineTopology.class.getResourceAsStream("storm.yaml");

        Yaml yaml = new Yaml();
        Map<String, Object> conf = yaml.load(inputStream);

        if (args != null && args.length > 0){
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
            builder.createTopology();
        }
        else{
            // Submit the topology to a local Storm cluster
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("TurbineTopology", conf, builder.createTopology());
    
            // Wait for some time and then shut down the cluster
            Utils.sleep(10000);
            cluster.killTopology("TurbineTopology");
            cluster.shutdown();
        }

    }

}
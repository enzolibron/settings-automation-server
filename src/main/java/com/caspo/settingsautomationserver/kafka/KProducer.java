package com.caspo.settingsautomationserver.kafka;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Component
public class KProducer {

    public boolean isAsync = false;
    public final KafkaProducer<Integer, String> producer;

    public final String topicName = "enzo_test";
    public final String CLIENT_ID;

    public KProducer() {
        CLIENT_ID = topicName + "-producer";
        Properties properties = new Properties();
        properties.put("bootstrap.servers",
                "10.12.8.22:9092,10.12.8.23:9092,10.12.8.24:9092,10.12.8.25:9092,10.12.8.26:9092,10.12.8.27:9092");
        properties.put("client.id", CLIENT_ID);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("enable.idempotence", true); //exactly once
        producer = new KafkaProducer<>(properties);
    }

    public void sendToKafka(String messageStr) {
        try {
            producer.send(new ProducerRecord(topicName, messageStr));
//            System.out.println("Kafka:\t" + messageStr);
        } catch (Exception ex) {
            Logger.getLogger(KProducer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

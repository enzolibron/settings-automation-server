package com.caspo.settingsautomationserver.kafka;

import com.caspo.settingsautomationserver.EventStorage;
import com.caspo.settingsautomationserver.models.EcPushFeedEventDto;
import com.caspo.settingsautomationserver.models.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Service
public class KConsumer {

    final JsonMapper jsonMapper;
    final XmlMapper xmlMapper;
    final Properties props;
    final String topicName = "sbk-ec-mapping";

    private final static String BOOTSTRAP_SERVERS_UAT = "10.12.8.146:9092,10.12.8.147:9092,10.12.8.148:9092";

    public KConsumer() {

        this.props = new Properties();
        this.jsonMapper = new JsonMapper();
        this.xmlMapper = new XmlMapper();
        this.props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS_UAT);
        this.props.put(ConsumerConfig.GROUP_ID_CONFIG, "ta-consumer-" + topicName + "-" + new Random().nextInt());
        this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        this.props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        this.jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void startConsumer() {
        new Thread(() -> {
            KafkaConsumer<String, String> consumer = new KafkaConsumer(this.props);
            consumer.subscribe(Collections.singletonList(this.topicName));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(5000);
                System.out.println("listening for updates in " + this.topicName);
                System.out.println("EventStorage Size: " + EventStorage.getInstance().getEvents().size());

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        Event newEvent = processRecord(record);
                        System.out.println("Newly Added Event: " + newEvent.toString());

                    } catch (JsonProcessingException ex) {
                        Logger.getLogger(KConsumer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }
        }).start();

    }

    private Event processRecord(ConsumerRecord<String, String> record) throws JsonProcessingException {

        JSONObject json = xmlMapper.readValue(record.value(), JSONObject.class);
        EcPushFeedEventDto ecPushFeedEventDto = jsonMapper.readValue(jsonMapper.writeValueAsString(json.get("event")), EcPushFeedEventDto.class);
        System.out.println(ecPushFeedEventDto);
        Event event = new Event();

        if (ecPushFeedEventDto.getEventid() != null) {
            //if record has eventid it is a new match, if not is an update record
            //add new event in EventStorage
            event.setEcEventID(ecPushFeedEventDto.getEventid());
            event.setEventDate(ecPushFeedEventDto.getEventDate());
            event.setIsRB(ecPushFeedEventDto.getIsRB());
            event.setCompetitionId(ecPushFeedEventDto.getCompetition().getId());
            event.setCompetitionName(ecPushFeedEventDto.getCompetition().getName());
            EventStorage.getInstance().add(event);
            return event;
        } else {
            //if update event scheduled
            Event eventFromList = EventStorage.getInstance().getEvents().stream()
                    .filter(item -> item.getEcEventID().equalsIgnoreCase(ecPushFeedEventDto.getEcEventID()))
                    .findFirst()
                    .orElse(null);

            int eventIndex = EventStorage.getInstance().getIndex(eventFromList);
            eventFromList.setEventDate(ecPushFeedEventDto.getEventDate());
            EventStorage.getInstance().updateEvent(eventIndex, eventFromList);
            return eventFromList;
        }
    }

}

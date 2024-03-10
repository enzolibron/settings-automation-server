package com.caspo.settingsautomationserver.kafka;

import com.caspo.settingsautomationserver.ScheduledEventsStorage;
import com.caspo.settingsautomationserver.daos.CompetitionGroupSettingDao;
import com.caspo.settingsautomationserver.daos.EventDao;
import com.caspo.settingsautomationserver.daos.ParentChildSettingDao;
import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
import com.caspo.settingsautomationserver.models.EcPushFeedEventDto;
import com.caspo.settingsautomationserver.models.Event;
import com.caspo.settingsautomationserver.models.ParentChildSetting;
import com.caspo.settingsautomationserver.services.EventSettingService;
import static com.caspo.settingsautomationserver.utils.DateUtil.formatDateFromKafkaPushFeed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.Collections;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Service
public class KConsumer {

    private final JsonMapper jsonMapper = new JsonMapper();
    private final XmlMapper xmlMapper = new XmlMapper();
    private final Properties props;
    private final String topicName = "sbk-ec-mapping";

    @Autowired
    private EventSettingService eventSettingService;

    @Autowired
    private CompetitionGroupSettingDao competitionGroupSettingDao;

    @Autowired
    private ParentChildSettingDao parentChildSettingDao;

    @Autowired
    private EventDao eventDao;

    private final static String BOOTSTRAP_SERVERS_UAT = "10.12.8.146:9092,10.12.8.147:9092,10.12.8.148:9092";

    public KConsumer() {

        this.props = new Properties();
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
                ConsumerRecords<String, String> records = consumer.poll(1500);
                Logger.getLogger(KConsumer.class.getName()).log(Level.INFO, "listening for updates in " + this.topicName);
                Logger.getLogger(KConsumer.class.getName()).log(Level.INFO, "EventScheduledStorage(Parents and Children) Size: {0}", ScheduledEventsStorage.get().getEvents().size());

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        Event newEventPush = processRecord(record);
                        if (newEventPush != null) {
                            Logger.getLogger(KConsumer.class.getName()).log(Level.INFO, "New KConsumer record: {0}", newEventPush.toString());
                        }

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

        Event event = null;

        if (ecPushFeedEventDto.getEventid() != null) {
            //statement block for new events
            ParentChildSetting parentChildSetting = parentChildSettingDao.getParentChildSettingByCompetitionIdAndType(Long.valueOf(ecPushFeedEventDto.getCompetition().getId()), "parent", 23);
            if (parentChildSetting != null) {
                CompetitionGroupSetting competitionGroupSettingParent = competitionGroupSettingDao.get(parentChildSetting.getCompetitionGroupSettingName());

                if (competitionGroupSettingParent != null) {
                    event = new Event();

                    event.setEventId(ecPushFeedEventDto.getEventid());
                    event.setEventDate(formatDateFromKafkaPushFeed(ecPushFeedEventDto.getEventDate()));
                    event.setIsRB(ecPushFeedEventDto.getIsRB());
                    event.setCompetitionId(ecPushFeedEventDto.getCompetition().getId());
                    event.setCompetitionName(ecPushFeedEventDto.getCompetition().getName());
                    event.setCompetitionGroupSetting(competitionGroupSettingParent);
                    event.setAway(ecPushFeedEventDto.getAway().getName());
                    event.setHome(ecPushFeedEventDto.getHome().getName());

                    eventSettingService.setNewMatchSetting(event);
                    event.setKickoffTimeMinusTodayScheduledTask(eventSettingService.setKickoffTimeMinusTodayScheduledTask(event));
                    event.setKickoffTimeScheduledTask(eventSettingService.setKickoffTimeScheduledTask(event));
                    eventSettingService.processChildEvents(event);

                    ScheduledEventsStorage.get().add(event);
                    eventDao.save(event);
                }
            }

        } else {
            //statement block for new updates
            event = ScheduledEventsStorage.get().getEvents().stream()
                    .filter(item -> item.getEventId().equalsIgnoreCase(ecPushFeedEventDto.getEcEventID()))
                    .findFirst()
                    .orElse(null);

            int eventIndex = ScheduledEventsStorage.get().getIndex(event);

            //cancel previous scheduled task
            if (event.getKickoffTimeMinusTodayScheduledTask() != null) {
                event.getKickoffTimeMinusTodayScheduledTask().cancel(false);
            }

            if (event.getKickoffTimeScheduledTask() != null) {
                event.getKickoffTimeScheduledTask().cancel(false);
            }

            //update and save
            event.setEventDate(formatDateFromKafkaPushFeed(ecPushFeedEventDto.getEventDate()));

            //set new scheduled task
            event.setKickoffTimeMinusTodayScheduledTask(eventSettingService.setKickoffTimeMinusTodayScheduledTask(event));
            event.setKickoffTimeScheduledTask(eventSettingService.setKickoffTimeScheduledTask(event));

            eventSettingService.updateChildEventsTask(event);

            ScheduledEventsStorage.get().updateEvent(eventIndex, event);
            eventDao.update(event, event.getEventId());
        }

        return event;
    }

}

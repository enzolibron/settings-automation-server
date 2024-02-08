package com.caspo.settingsautomationserver;

import com.caspo.settingsautomationserver.ec.GetEsportEvents;
import com.caspo.settingsautomationserver.kafka.KConsumer;
import com.caspo.settingsautomationserver.models.Event;
import com.caspo.settingsautomationserver.services.EventSettingService;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Transactional
public class SettingsAutomationServerApplication implements CommandLineRunner {

    @Autowired
    private EventSettingService eventSettingService;

    @Autowired
    private GetEsportEvents getEsportEvents;

    @Autowired
    private KConsumer kConsumer;

    public static void main(String[] args) {
        SpringApplication.run(SettingsAutomationServerApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {

        try {
            List<Event> eventList = null;

            while (eventList == null) {
                System.out.println(new Date() + " retrying... ");
                eventList = getEsportEvents.connect();
                TimeUnit.SECONDS.sleep(5);
            }
            eventList.stream().forEach(event -> {
                if (event.getIsRB().equalsIgnoreCase("0") || event.getIsRB().equalsIgnoreCase("No")) {

                    eventSettingService.setNewMatchSetting(event);
                    eventSettingService.setScheduledTask(event);

                }

            });

            EventStorage.getInstance().addAll(eventList);
        } catch (IOException ex) {
            Logger.getLogger(SettingsAutomationServerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        kConsumer.startConsumer();
    }

}

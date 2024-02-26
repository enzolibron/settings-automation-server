package com.caspo.settingsautomationserver;

import com.caspo.settingsautomationserver.daos.EventDao;
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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
@Transactional
public class SettingsAutomationServerApplication implements CommandLineRunner {

    private final EventSettingService eventSettingService;
    private final KConsumer kConsumer;

    public static void main(String[] args) {
        SpringApplication.run(SettingsAutomationServerApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {

        try {
            eventSettingService.processEventsFromEc();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SettingsAutomationServerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

//        kConsumer.startConsumer();
    }

}

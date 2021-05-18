package com.jitterted.moborg;

import com.jitterted.moborg.domain.Huddle;
import com.jitterted.moborg.domain.HuddleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class DataLoader implements CommandLineRunner {
    private final HuddleRepository huddleRepository;

    @Autowired
    public DataLoader(HuddleRepository huddleRepository) {
        this.huddleRepository = huddleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Huddle huddle = new Huddle("Mob #6", ZonedDateTime.of(2021, 5, 7, 9, 0, 0, 0, ZoneId.of("America/Los_Angeles")));
        huddleRepository.save(huddle);
    }
}

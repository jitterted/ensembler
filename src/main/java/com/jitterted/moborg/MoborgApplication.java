package com.jitterted.moborg;

import com.jitterted.moborg.domain.HuddleService;
import com.jitterted.moborg.domain.InMemoryHuddleRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MoborgApplication {

  public static void main(String[] args) {
    SpringApplication.run(MoborgApplication.class, args);
  }

  @Bean
  public HuddleService createHuddleService() {
    return new HuddleService(createHuddleRepository());
  }

  @Bean
  public InMemoryHuddleRepository createHuddleRepository() {
    return new InMemoryHuddleRepository();
  }

}

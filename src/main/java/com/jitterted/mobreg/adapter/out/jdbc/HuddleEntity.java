package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// Database Entity for Huddle to be stored in the database
public class HuddleEntity {
    @Id
    private Long id;

    private String name;
    private LocalDateTime dateTimeUtc;

    @MappedCollection(idColumn = "HUDDLE_ID")
    private Set<MemberEntityId> registeredMembers = new HashSet<>();

    public static HuddleEntity from(Huddle huddle) {
        HuddleEntity huddleEntity = new HuddleEntity();
        if (huddle.getId() != null) {
            huddleEntity.setId(huddle.getId().id());
        }
        huddleEntity.setName(huddle.name());
        huddleEntity.setDateTimeUtc(huddle.startDateTime().toLocalDateTime());
        huddleEntity.setRegisteredMembers(
                huddle.registeredMembers()
                      .stream()
                      .map(MemberEntityId::toEntityId)
                      .collect(Collectors.toSet()));
        return huddleEntity;
    }

    public Huddle asHuddle() {
        ZonedDateTime startDateTime = ZonedDateTime.of(dateTimeUtc, ZoneId.systemDefault());
        Huddle huddle = new Huddle(name, startDateTime);
        huddle.setId(HuddleId.of(id));

        registeredMembers.stream()
                         .map(MemberEntityId::asMemberId)
                         .forEach(huddle::registerById);

        return huddle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTimeUtc() {
        return dateTimeUtc;
    }

    public void setDateTimeUtc(LocalDateTime dateTimeUtc) {
        this.dateTimeUtc = dateTimeUtc;
    }

    public Set<MemberEntityId> getRegisteredMembers() {
        return registeredMembers;
    }

    public void setRegisteredMembers(Set<MemberEntityId> registeredMembers) {
        this.registeredMembers = registeredMembers;
    }
}

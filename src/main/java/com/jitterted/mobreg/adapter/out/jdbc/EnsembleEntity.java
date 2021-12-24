package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// Database Entity for Ensemble to be stored in the database
public class EnsembleEntity {
    @Id
    private Long id;

    private String name;
    private String zoomMeetingLink;
    private LocalDateTime dateTimeUtc;
    private boolean isCompleted;
    private String recordingLink;

    @MappedCollection(idColumn = "ensemble_id")
    private Set<AcceptedMember> acceptedMembers = new HashSet<>();

    @MappedCollection(idColumn = "ensemble_id")
    private Set<DeclinedMember> declinedMembers = new HashSet<>();

    public static EnsembleEntity from(Ensemble ensemble) {
        EnsembleEntity ensembleEntity = new EnsembleEntity();
        if (ensemble.getId() != null) {
            ensembleEntity.setId(ensemble.getId().id());
        }
        ensembleEntity.setName(ensemble.name());
        ensembleEntity.setDateTimeUtc(ensemble.startDateTime().toLocalDateTime());
        ensembleEntity.setZoomMeetingLink(ensemble.zoomMeetingLink().toString());
        ensembleEntity.setCompleted(ensemble.isCompleted());
        ensembleEntity.setRecordingLink(ensemble.recordingLink().toString());
        ensembleEntity.setAcceptedMembers(
                ensemble.acceptedMembers()
                        .map(AcceptedMember::toEntityId)
                        .collect(Collectors.toSet()));
        ensembleEntity.setDeclinedMembers(
                ensemble.declinedMembers()
                        .map(DeclinedMember::toEntityId)
                        .collect(Collectors.toSet()));
        return ensembleEntity;
    }

    public Ensemble asEnsemble() {
        ZonedDateTime startDateTime = ZonedDateTime.of(dateTimeUtc, ZoneOffset.UTC);
        Ensemble ensemble = new Ensemble(name, URI.create(zoomMeetingLink), startDateTime);
        ensemble.setId(EnsembleId.of(id));
        ensemble.linkToRecordingAt(URI.create(recordingLink));

        acceptedMembers.stream()
                       .map(AcceptedMember::asMemberId)
                       .forEach(ensemble::acceptedBy);

        declinedMembers.stream()
                       .map(DeclinedMember::asMemberId)
                       .forEach(ensemble::declinedBy);

        if (isCompleted) {
            ensemble.complete();
        }

        return ensemble;
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

    public Set<AcceptedMember> getAcceptedMembers() {
        return acceptedMembers;
    }

    public void setAcceptedMembers(Set<AcceptedMember> acceptedMembers) {
        this.acceptedMembers = acceptedMembers;
    }

    public Set<DeclinedMember> getDeclinedMembers() {
        return declinedMembers;
    }

    public void setDeclinedMembers(Set<DeclinedMember> declinedMembers) {
        this.declinedMembers = declinedMembers;
    }

    public String getZoomMeetingLink() {
        return zoomMeetingLink;
    }

    public void setZoomMeetingLink(String zoomMeetingLink) {
        this.zoomMeetingLink = zoomMeetingLink;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getRecordingLink() {
        return recordingLink;
    }

    public void setRecordingLink(String recordingLink) {
        this.recordingLink = recordingLink;
    }
}

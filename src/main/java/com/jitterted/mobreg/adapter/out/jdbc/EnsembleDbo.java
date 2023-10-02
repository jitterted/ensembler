package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// Database-specific DTO for Ensemble to be stored in the database
@Table("ensembles")
class EnsembleDbo {
    @Id
    private Long id;

    private String name;
    private String conferenceJoinUrl;
    private String conferenceStartUrl;
    private String conferenceMeetingId;
    private LocalDateTime dateTimeUtc;
    private String state;
    private String recordingLink;

    @MappedCollection(idColumn = "ensemble_id")
    private Set<AcceptedMember> acceptedMembers = new HashSet<>();

    @MappedCollection(idColumn = "ensemble_id")
    private Set<DeclinedMember> declinedMembers = new HashSet<>();

    @MappedCollection(idColumn = "ensemble_id")
    private Set<SpectatorMember> spectatorMembers = new HashSet<>();

    public static EnsembleDbo from(Ensemble ensemble) {
        EnsembleDbo ensembleDbo = new EnsembleDbo();
        if (ensemble.getId() != null) {
            ensembleDbo.setId(ensemble.getId().id());
        }
        ensembleDbo.setName(ensemble.name());
        ensembleDbo.setDateTimeUtc(ensemble.startDateTime().toLocalDateTime());
        ensembleDbo.setConferenceMeetingId(ensemble.conferenceDetails().meetingId());
        ensembleDbo.setConferenceJoinUrl(ensemble.conferenceDetails().joinUrl().toString());
        ensembleDbo.setConferenceStartUrl(ensemble.conferenceDetails().startUrl().toString());
        ensembleDbo.setState(ensemble.state().toString());
        ensembleDbo.setRecordingLink(ensemble.recordingLink().toString());
        ensembleDbo.setAcceptedMembers(
                ensemble.acceptedMembers()
                        .map(AcceptedMember::toEntityId)
                        .collect(Collectors.toSet()));
        ensembleDbo.setDeclinedMembers(
                ensemble.declinedMembers()
                        .map(DeclinedMember::toEntityId)
                        .collect(Collectors.toSet()));
        ensembleDbo.setSpectatorMembers(
                ensemble.spectators()
                        .map(SpectatorMember::toEntityId)
                        .collect(Collectors.toSet()));
        return ensembleDbo;
    }

    public Ensemble asEnsemble() {
        ZonedDateTime startDateTime = ZonedDateTime.of(dateTimeUtc, ZoneOffset.UTC);
        Ensemble ensemble = new Ensemble(name, startDateTime);
        ensemble.changeConferenceDetailsTo(new ConferenceDetails(getConferenceMeetingId(),
                                                                 URI.create(getConferenceStartUrl()),
                                                                 URI.create(getConferenceJoinUrl())));
        ensemble.setId(EnsembleId.of(id));
        ensemble.linkToRecordingAt(URI.create(recordingLink));

        acceptedMembers.stream()
                       .map(AcceptedMember::asMemberId)
                       .forEach(ensemble::acceptedBy);

        declinedMembers.stream()
                       .map(DeclinedMember::asMemberId)
                       .forEach(ensemble::declinedBy);

        spectatorMembers.stream()
                       .map(SpectatorMember::asMemberId)
                       .forEach(ensemble::joinAsSpectator);

        if (state.equalsIgnoreCase("COMPLETED")) {
            ensemble.complete();
        } else if (state.equalsIgnoreCase("CANCELED")) {
            ensemble.cancel();
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

    public Set<SpectatorMember> getSpectatorMembers() {
        return spectatorMembers;
    }

    public void setSpectatorMembers(Set<SpectatorMember> spectatorMembers) {
        this.spectatorMembers = spectatorMembers;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRecordingLink() {
        return recordingLink;
    }

    public void setRecordingLink(String recordingLink) {
        this.recordingLink = recordingLink;
    }

    public String getConferenceJoinUrl() {
        return conferenceJoinUrl;
    }

    public void setConferenceJoinUrl(String conferenceJoinUrl) {
        this.conferenceJoinUrl = conferenceJoinUrl;
    }

    public String getConferenceStartUrl() {
        return conferenceStartUrl;
    }

    public void setConferenceStartUrl(String conferenceStartUrl) {
        this.conferenceStartUrl = conferenceStartUrl;
    }

    public String getConferenceMeetingId() {
        return conferenceMeetingId;
    }

    public void setConferenceMeetingId(String conferenceMeetingId) {
        this.conferenceMeetingId = conferenceMeetingId;
    }
}

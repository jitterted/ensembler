package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.MemberId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class HuddleSummaryView {
    private final long id;
    private final String name;
    private final String zoomMeetingLink;
    private final String dateTime;
    private final int numberRegistered;
    private final boolean memberRegistered;

    HuddleSummaryView(long id,
                      String name,
                      String zoomMeetingLink,
                      String dateTime,
                      int numberRegistered,
                      boolean memberRegistered) {
        this.id = id;
        this.name = name;
        this.zoomMeetingLink = zoomMeetingLink;
        this.dateTime = dateTime;
        this.numberRegistered = numberRegistered;
        this.memberRegistered = memberRegistered;
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String zoomMeetingLink() {
        return zoomMeetingLink;
    }

    public String dateTime() {
        return dateTime;
    }

    public int numberRegistered() {
        return numberRegistered;
    }

    public boolean memberRegistered() {
        return memberRegistered;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (HuddleSummaryView) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.zoomMeetingLink, that.zoomMeetingLink) &&
                Objects.equals(this.dateTime, that.dateTime) &&
                this.numberRegistered == that.numberRegistered &&
                this.memberRegistered == that.memberRegistered;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, zoomMeetingLink, dateTime, numberRegistered, memberRegistered);
    }

    @Override
    public String toString() {
        return "HuddleSummaryView[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "zoomMeetingLink=" + zoomMeetingLink + ", " +
                "dateTime=" + dateTime + ", " +
                "numberRegistered=" + numberRegistered + ", " +
                "memberRegistered=" + memberRegistered + ']';
    }

    public static List<HuddleSummaryView> from(List<Huddle> huddles, MemberId memberId) {
        return huddles.stream()
                      .map(huddle -> toView(huddle, memberId))
                      .collect(Collectors.toList());
    }

    public static HuddleSummaryView toView(Huddle huddle, MemberId memberId) {
        return new HuddleSummaryView(huddle.getId().id(),
                                     huddle.name(),
                                     huddle.zoomMeetingLink().toString(),
                                     DateTimeFormatting.formatAsDateTime(huddle.startDateTime()),
                                     huddle.registeredMemberCount(),
                                     huddle.isRegisteredById(memberId));
    }
}

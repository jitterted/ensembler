package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.DateTimeFormatting;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class ScheduleHuddleForm {
    private String name;
    private String date;
    private String time;
    private String zoomMeetingLink;
    private String timezone;

    public ScheduleHuddleForm() {
    }

    public ScheduleHuddleForm(String name, String zoomMeetingLink, String date, String time, String timezone) {
        this.name = name;
        this.zoomMeetingLink = zoomMeetingLink;
        this.date = date;
        this.time = time;
        this.timezone = timezone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getZoomMeetingLink() {
        return zoomMeetingLink;
    }

    public void setZoomMeetingLink(String zoomMeetingLink) {
        this.zoomMeetingLink = zoomMeetingLink;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    ZonedDateTime getDateTimeInUtc() {
        LocalDateTime localDateTime = LocalDateTime.parse(getDate() + " " + getTime(), DateTimeFormatting.YYYY_MM_DD_HH_MM_FORMATTER);
        return ZonedDateTime.of(localDateTime, ZoneId.of(timezone)).withZoneSameInstant(ZoneId.of("Z"));
    }
}

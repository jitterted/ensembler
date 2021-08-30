package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.MemberService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class HuddleDetailView {
    private final long id;
    private final String name;
    private final String startDateTime;
    private final String duration;
    private final String zoomMeetingLink;
    private final List<MemberView> memberViews;

    public HuddleDetailView(long id,
                            String name,
                            String startDateTime,
                            String duration,
                            String zoomMeetingLink,
                            List<MemberView> memberViews) {
        this.id = id;
        this.name = name;
        this.startDateTime = startDateTime;
        this.duration = duration;
        this.zoomMeetingLink = zoomMeetingLink;
        this.memberViews = memberViews;
    }

    static HuddleDetailView from(Huddle huddle, MemberService memberService) {
        List<MemberView> memberViews =
                huddle.registeredMembers().stream()
                      .map(memberService::findById)
                      .map(MemberView::from)
                      .collect(Collectors.toList());
        return new HuddleDetailView(huddle.getId().id(),
                                    huddle.name(),
                                    DateTimeFormatting.formatAsDateTime(huddle.startDateTime()),
                                    "90m",
                                    huddle.zoomMeetingLink().toString(),
                                    memberViews);
    }

    public int size() {
        return memberViews.size();
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String startDateTime() {
        return startDateTime;
    }

    public String duration() {
        return duration;
    }

    public String zoomMeetingLink() {
        return zoomMeetingLink;
    }

    public List<MemberView> memberViews() {
        return memberViews;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (HuddleDetailView) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.startDateTime, that.startDateTime) &&
                Objects.equals(this.duration, that.duration) &&
                Objects.equals(this.zoomMeetingLink, that.zoomMeetingLink) &&
                Objects.equals(this.memberViews, that.memberViews);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDateTime, duration, zoomMeetingLink, memberViews);
    }

    @Override
    public String toString() {
        return "HuddleDetailView[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "startDateTime=" + startDateTime + ", " +
                "duration=" + duration + ", " +
                "zoomMeetingLink=" + zoomMeetingLink + ", " +
                "memberViews=" + memberViews + ']';
    }

}

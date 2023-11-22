package com.jitterted.mobreg.adapter.out.zoom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Generated;

import java.util.Date;

/**
 * Zoom Meeting object from Create Meeting
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "registration_url",
        "topic",
        "type",
        "start_time",
        "start_url",
        "join_url"
})
@Generated("jsonschema2pojo")
public class ZoomCreateMeetingResponse {

    /**
     * [Meeting ID](https://support.zoom.us/hc/en-us/articles/201362373-What-is-a-Meeting-ID-): Unique identifier of the meeting in "**long**" format(represented as int64 data type in JSON), also known as the meeting number.
     */
    @JsonProperty("id")
    public Long id;

    /**
     * URL using which registrants can register for a meeting. This field is only returned for meetings that have enabled registration.
     */
    @JsonProperty("registration_url")
    public String registrationUrl;
    /**
     * Meeting topic
     */
    @JsonProperty("topic")
    public String topic;
    /**
     * Meeting Type
     */
    @JsonProperty("type")
    public Integer type;
    /**
     * Meeting start date-time in UTC/GMT. Example: "2020-03-31T12:02:00Z"
     */
    @JsonProperty("start_time")
    public Date startTime;
    /**
     * URL to start the meeting. This URL should only be used by the host of the meeting and **should not be shared with anyone other than the host** of the meeting as anyone with this URL will be able to login to the Zoom Client as the host of the meeting.
     */
    @JsonProperty("start_url")
    public String startUrl;
    /**
     * URL for participants to join the meeting. This URL should only be shared with users that you would like to invite for the meeting.
     */
    @JsonProperty("join_url")
    public String joinUrl;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ZoomCreateMeetingResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("registrationUrl");
        sb.append('=');
        sb.append(((this.registrationUrl == null) ? "<null>" : this.registrationUrl));
        sb.append(',');
        sb.append("topic");
        sb.append('=');
        sb.append(((this.topic == null) ? "<null>" : this.topic));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null) ? "<null>" : this.type));
        sb.append(',');
        sb.append("startTime");
        sb.append('=');
        sb.append(((this.startTime == null) ? "<null>" : this.startTime));
        sb.append(',');
        sb.append("startUrl");
        sb.append('=');
        sb.append(((this.startUrl == null) ? "<null>" : this.startUrl));
        sb.append(',');
        sb.append("joinUrl");
        sb.append('=');
        sb.append(((this.joinUrl == null) ? "<null>" : this.joinUrl));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}

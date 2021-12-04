package com.jitterted.mobreg.adapter.out.zoom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

/**
 * Zoom create meeting request object (minimal)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("jsonschema2pojo")
public class ZoomCreateMeetingRequest {

    /**
     * The meeting's topic.
     */
    @JsonProperty("topic")
    public String topic;


    @JsonProperty("type")
    public ZoomCreateMeetingRequest.Type type = ZoomCreateMeetingRequest.Type.fromValue(2);
    /**
     * The meeting's start time. This field is only used for scheduled and/or recurring meetings with a fixed time. This supports local time and GMT formats. 
     * * To set a meeting's start time in GMT, use the `yyyy-MM-ddTHH:mm:ssZ` date-time format. For example, `2020-03-31T12:02:00Z`. 
     * * To set a meeting's start time using a specific timezone, use the `yyyy-MM-ddTHH:mm:ss` date-time format and specify the [timezone ID](https://marketplace.zoom.us/docs/api-reference/other-references/abbreviation-lists#timezones) in the `timezone` field. If you do not specify a timezone, the `timezone` value defaults to your Zoom account's timezone. You can also use `UTC` for the `timezone` value.
     */
    @JsonProperty("start_time")
    public String startTime;

    /**
     * The meeting's scheduled duration, in minutes. This field is only used for scheduled meetings (`2`).
     */
    @JsonProperty("duration")
    public Integer duration;

    /**
     * The meeting's Description. This value has a maximum length of 2,000 characters.
     */
    @JsonProperty("agenda")
    public String description;

    /**
     * Information about the meeting's settings.
     */
    @JsonProperty("settings")
    public Settings settings;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ZoomCreateMeetingRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("topic");
        sb.append('=');
        sb.append(((this.topic == null)?"<null>":this.topic));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
        sb.append(',');
        sb.append("startTime");
        sb.append('=');
        sb.append(((this.startTime == null)?"<null>":this.startTime));
        sb.append(',');
        sb.append("duration");
        sb.append('=');
        sb.append(((this.duration == null)?"<null>":this.duration));
        sb.append(',');
        sb.append("agenda");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("settings");
        sb.append('=');
        sb.append(((this.settings == null)?"<null>":this.settings));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }


    @Generated("jsonschema2pojo")
    public enum Type {

        INSTANT_MEETING(1),
        SCHEDULED_MEETING(2),
        RECURRING_MEETING_NO_FIXED_TIME(3),
        RECURRING_MEETING_WITH_FIXED_TIME(8);
        private final Integer value;
        private final static Map<Integer, ZoomCreateMeetingRequest.Type> CONSTANTS = new HashMap<Integer, ZoomCreateMeetingRequest.Type>();

        static {
            for (ZoomCreateMeetingRequest.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Type(Integer value) {
            this.value = value;
        }

        @JsonValue
        public Integer value() {
            return this.value;
        }

        @JsonCreator
        public static ZoomCreateMeetingRequest.Type fromValue(Integer value) {
            ZoomCreateMeetingRequest.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException((value +""));
            } else {
                return constant;
            }
        }

    }

}


package com.jitterted.mobreg.adapter.out.zoom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

/**
 * Information about the meeting's settings.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("jsonschema2pojo")
public class Settings {

    /**
     * Whether to start meetings with the host video on.
     * 
     */
    @JsonProperty("host_video")
    public Boolean hostVideo = false;

    /**
     * Whether to start meetings with the participant video on.
     */
    @JsonProperty("participant_video")
    public Boolean participantVideo = false;

    /**
     * Whether participants can join the meeting before its host. This field is only used for scheduled meetings (`2`) or recurring meetings (`3` and `8`). This value defaults to `false`.
     * 
     * If the [**Waiting Room** feature](https://support.zoom.us/hc/en-us/articles/115000332726-Waiting-Room) is enabled, this setting is **disabled**.
     */
    @JsonProperty("join_before_host")
    public Boolean joinBeforeHost = false;

    /**
     * If the value of the `join_before_host` field is `true`, this field indicates the time limits within which a participant can join a meeting before the meeting's host:
     * 
     * * `0` — Allow the participant to join the meeting at anytime.
     * * `5` — Allow the participant to join 5 minutes before the meeting's start time.
     * * `10` — Allow the participant to join 10 minutes before the meeting's start time.
     */
    @JsonProperty("jbh_time")
    public Settings.JbhTime jbhTime;

    /**
     * Whether to mute participants upon entry.
     */
    @JsonProperty("mute_upon_entry")
    public Boolean muteUponEntry = false;

    @JsonProperty("auto_recording")
    public Settings.AutoRecording autoRecording = AutoRecording.NONE;

    @JsonProperty("waiting_room")
    /**
     * "Whether to enable the [**Waiting Room** feature](https://support.zoom.us/hc/en-us/articles/115000332726-Waiting-Room).
     * If this value is `true`, this **disables** the `join_before_host` setting."
     */
    public Boolean waitingRoom = false;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Settings.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("hostVideo");
        sb.append('=');
        sb.append(((this.hostVideo == null)?"<null>":this.hostVideo));
        sb.append(',');
        sb.append("participantVideo");
        sb.append('=');
        sb.append(((this.participantVideo == null)?"<null>":this.participantVideo));
        sb.append(',');
        sb.append("joinBeforeHost");
        sb.append('=');
        sb.append(((this.joinBeforeHost == null)?"<null>":this.joinBeforeHost));
        sb.append(',');
        sb.append("jbhTime");
        sb.append('=');
        sb.append(((this.jbhTime == null)?"<null>":this.jbhTime));
        sb.append(',');
        sb.append("muteUponEntry");
        sb.append('=');
        sb.append(((this.muteUponEntry == null)?"<null>":this.muteUponEntry));
        sb.append(',');
        sb.append("autoRecording");
        sb.append('=');
        sb.append(((this.autoRecording == null)?"<null>":this.autoRecording));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }


    @Generated("jsonschema2pojo")
    public enum AutoRecording {

        LOCAL("local"),
        CLOUD("cloud"),
        NONE("none");
        private final String value;
        private final static Map<String, Settings.AutoRecording> CONSTANTS = new HashMap<String, Settings.AutoRecording>();

        static {
            for (Settings.AutoRecording c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        AutoRecording(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Settings.AutoRecording fromValue(String value) {
            Settings.AutoRecording constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }


    /**
     * If the value of the `join_before_host` field is `true`, this field indicates the time limits within which a participant can join a meeting before the meeting's host:
     * 
     * * `0` — Allow the participant to join the meeting at anytime.
     * * `5` — Allow the participant to join 5 minutes before the meeting's start time.
     * * `10` — Allow the participant to join 10 minutes before the meeting's start time.
     * 
     */
    @Generated("jsonschema2pojo")
    public enum JbhTime {

        JOIN_AT_ANYTIME(0),
        JOIN_5_MINUTES_BEFORE(5),
        JOIN_10_MINUTES_BEFORE(10);
        private final Integer value;
        private final static Map<Integer, Settings.JbhTime> CONSTANTS = new HashMap<Integer, Settings.JbhTime>();

        static {
            for (Settings.JbhTime c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        JbhTime(Integer value) {
            this.value = value;
        }

        @JsonValue
        public Integer value() {
            return this.value;
        }

        @JsonCreator
        public static Settings.JbhTime fromValue(Integer value) {
            Settings.JbhTime constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException((value +""));
            } else {
                return constant;
            }
        }

    }

}

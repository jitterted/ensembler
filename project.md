# MobReg

This project manages Remote Mob Programming huddles:

# Features

* Registration
    + Collect info for participants
    + Limit registration to 5 per huddle
    + "Integrate" with MobTi.me -- via WebSocket
    - Integrate with Calendars (both theirs and mine) - Google and then ?? based on demand
    - Integrate with Zoom
    - Integrate with GitHub for adding collaborators to the repo
    - Integrate with Discord
      - As a bot, helping with self-service sign-up
    - Provide WorldTimeBuddy.com links for huddles

* Dashboard
    [X] Schedule a new huddle: date/time, duration, topic
    [X] See huddles and how many registrations

1. See existing huddles
    - Name
    - Date/time (including time zone)
    - Duration
    - Topic
    - Number of people registered

1. View details of a huddle
   
   (DONE) Requires Huddles to have identifiers to be inserted into the URI

    - Participant details
      - Name
      - Email address 
      - GitHub username
      - Discord username
      - New to group

    - Goals (one or more) of the huddle (i.e., what's the mission)

1. (DONE) Schedule a new huddle

# Name Ideas

* Moborg (MoBORG) -- discarded, name used in various places
* Mobception (per TheGrumpyGameDev)

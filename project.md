# MobReg

This project manages Remote Learning Ensemble (Mob Programming) huddles (huddles are a specific, scheduled learning session).

# Features

* Member Registration
    + Collect info for participants
    + Limit registration to 5 per huddle
    + "Integrate" with MobTi.me -- via WebSocket
    - Provide calendar invites using links
        - Provide WorldTimeBuddy.com links for huddles (maybe not needed if calendar links are sufficient)
    - Integrate with GitHub for adding collaborators to the repo
    - Integrate with Calendars (both theirs and mine) via Google API and then ?? based on demand
    - Integrate with Zoom to create a meeting each time a Huddle is scheduled
        - See https://marketplace.zoom.us/docs/sdk/native-sdks/developer-accounts and https://marketplace.zoom.us/docs/api-reference/zoom-api/meetings/meetingcreate
        - Until then, provide a field to be manually entered so that it can be added as part of the calendar invites, messages to participants, as well as serving as a reminder to create the meeting in Zoom
    - Integrate with Discord
      - As a bot, helping with self-service sign-up

# Registration Scenarios

## Member to Huddle Registration

Someone who has participated in past Huddles wants to register for a future huddle.

1. When we load the home page, if we know who they are (they previously authenticated via GitHub), take them to Huddle View (step 3 below)

2. User clicks on "Login via GitHub" and flow completes successfully. If it fails, redirect to error or "try again" page of some sort.

3. If they are not a ROLE_MEMBER, then display "sorry, only members allowed" page **terminates journey** (later, this will go to the User-to-Member scenario below)

4. Display list of Huddles, each having REGISTER buttons (if capacity limit hasn't been reached)

1. Member clicks on REGISTER button

1. Call HuddleService.register with info from Member. 


## User to Member

This scenario needs more analysis.
This scenario is in progress...

1. Unknown person hits home page, presented with "Login via GitHub" or "Become a Member"
2. If they choose Login via GitHub, then redirect to OAuth web flow


# Stories

* Dashboard
    [X] Schedule a new huddle: date/time, duration, topic
    [X] See huddles and how many registrations

* See existing huddles
    - Name
    - Date/time (including time zone)
    - Duration
    - Topic
    - Number of people registered

* View details of a huddle
   
   [X] Huddles need identifiers to be inserted into the URI

    - Participant details
      - Name
      - Email address 
      - GitHub username
      - Discord username
      - New to group

    - Goals (one or more) of the huddle (i.e., what's the mission)

* [X] Schedule a new huddle

# Name Ideas

* Moborg (MoBORG) -- discarded, name used in various places
* Mobception (per TheGrumpyGameDev)

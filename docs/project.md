# Ensembler

This project manages Remote Learning Ensembles (aka Mob Programming), which are a specific, scheduled learning session.

# Features To-Do

[X] Rename domain term: Huddle -> Ensemble

* Groups
    + Ensembles belong to a group

* Members
    + Members can belong to Zero or more groups
    + Tiers

* User Profile
    + Click on Name to go to page: need a page template
    + Display Only: First Name, GitHub Username: need a form object
    + Edit email: from the form object

    + Add/edit discord username
    + Add/edit pushover

* Notifications
    + Email
    + Discord: direct message
    + Pushover https://pushover.net/

* Provide calendar invites using links
    - Provide WorldTimeBuddy.com links for ensembles (maybe not needed if calendar links are sufficient)
  
* Integrate with Zoom to create a meeting each time a Huddle is scheduled
    - See https://marketplace.zoom.us/docs/sdk/native-sdks/developer-accounts and https://marketplace.zoom.us/docs/api-reference/zoom-api/meetings/meetingcreate



    - Integrate with GitHub for adding collaborators to the repo
    - Integrate with Calendars (both theirs and mine) via Google API and then ?? based on demand
        + Until then, provide a field to be manually entered so that it can be added as part of the calendar invites, messages to participants, as well as serving as a reminder to create the meeting in Zoom
    - As a bot, helping with self-service sign-up
    v "Integrate" with MobTi.me -- via WebSocket [blocked]

* Member Registration
    + Collect info for participants
    + Store a link to the video recording, only available to those who were participants of that Huddle
    + Limit registration to 5 per ensemble in UI


# Registration Scenarios

## Edit Member Profile

1. Display Profile link in upper-right

2. Click on Profile Link -> Profile Page

3. Profile Page:
   
    * GitHub username (not editable)
    * Display Name (how do you want to appear in the Mob membership list)
    * Discord username (optional)
    * ✅ toggle for notification of:
        * New ensembles
        * Zoom recording
        * (other?)
    * Update button to save


## Member to Huddle Registration

DONE except for optional (constraint) parts

Someone who has participated in past Huddles wants to register for a future ensemble.

1. Show sign-up/sign-in page

2. User clicks on "Sign In via GitHub" and flow completes successfully. If it fails, redirect to error or "try again" page of some sort.

3. If they are **not** a ROLE_MEMBER, then redirect them to User "Home Page" (placeholder for now), otherwise continue...

4. Display list of (optional: available) Huddles, each having REGISTER buttons

    * (optional) if capacity limit hasn't been reached, otherwise we'll check later

5. Member clicks on REGISTER button

6. Call HuddleService.register with info from Member. 


## User to Member

This scenario needs more analysis.

1. Unknown person hits home page, presented with "Login via GitHub" or "Become a Member"
2. If they choose Login via GitHub, then redirect to OAuth web flow


# Stories

* Dashboard
    [X] Schedule a new ensemble: date/time, duration, topic
    [X] See ensembles and how many registrations

* See existing ensembles
    - Name
    - Date/time (including time zone)
    - Duration
    - Topic
    - Number of people registered

* View details of a ensemble
   
   [X] Huddles need identifiers to be inserted into the URI

    - Participant details
      - Name
      - Email address 
      - GitHub username
      - Discord username
      - New to group

    - Goals (one or more) of the ensemble (i.e., what's the mission)

* [X] Schedule a new ensemble

# Name Ideas

* Moborg (MoBORG) -- discarded, name used in various places
* Mobception (per TheGrumpyGameDev)

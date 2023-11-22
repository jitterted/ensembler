# Tasks

[ ] Change "participate" button color to indicate that it's disabled when the ensemble is full

[ ] Replace EnsembleSummaryView.memberStatus with Links (for right-most column)
    [ ] Update all EnsembleSummaryViewTest to NOT use EnsembleSummaryView.memberStatus()
    [ ] Remove showing memberStatus from member-register.html
    [ ] Remove deprecated EnsembleSummaryView.memberStatus() method

[ ] (ADMIN) Add display of Spectators to Ensemble detail screen

[ ] (ADMIN) Show number of Spectators on Ensemble summary screen

[ ] Start writing HTML tests??

[ ] Add notification when joining as spectator

[ ] Update RSVP enum to also have SPECTATOR

[ ] Admin Ensemble details screen: move a Member between Participant, Spectator, and Declined

# Later

* [ ] Change storage of Member registrations from 3 separate Sets to a single Set, where there's an Enum for each (PARTICIPANT, SPECTATOR, and DECLINED)
* [ ] Convert Ensemble to use the Snapshot Persistence pattern
* [ ] Convert Member to use the Snapshot Persistence pattern

## UI

* [ ] Change table to fixed width grid instead of flex to avoid columns changing size and moving around
* [ ] New layout for Member Registration

# DONE

[X] Ensemble: Add "Spectators" (as Set<MemberId>)
    [X] void joinAsSpectator(MemberId memberId)
        [X] remove from Accepted 
        [X] remove from Declined
    [X] Set<MemberId> spectators()
    [X] Accept removes from Spectators
    [X] Decline removes from Spectators
[X] EnsembleService: joinAsSpectator(EnsembleId, MemberId)
[X] Add "Spectators" column to the member-register template
    [X] Dummy copy of spectators: use copy of participants
    [X] Add the real Spectators to the EnsembleSummaryView object
    [X] Add new POST endpoint to MemberController for joinAsSpectator
    [X] Add button for "Join as Spectator"
    [X] Only show "Spectate" when it makes sense (when not already Spectator)
        [X] If you are Spectator, show "Leave" button instead
[X] Show Zoom/Calendar link when spectating

[X] Deploy: update Zoom credentials for environment variables on Railway

[X] Hide both Join & Participate buttons when the Ensemble is in the past
    [X] Test against EnsembleSummaryView, adding a "show" boolean for each of the Actions

[X] Don't show "Spectate" button if ensemble is ineligible


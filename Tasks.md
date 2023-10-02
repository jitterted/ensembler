# Tasks

[ ] Deploy: update Zoom credentials for environment variables on Railway

[ ] Hide both Join & Participate buttons when the Ensemble is in the past (e.g., "pending_complete")
    [ ] Test against EnsembleSummaryView, adding a "show" boolean for each of the Actions 

[ ] Replace EnsembleSummaryView.memberStatus with ActionLinks (for right-most column)
    [ ] Remove deprecated EnsembleSummaryView.memberStatus() method
    [ ] Remove showing memberStatus from member-register.html

[ ] Add display of Spectators to admin screens

[ ] Start writing HTML tests??

# Optional

[ ] Add notification when joining as spectator

[ ] Don't show "Spectate" button if ensemble is ineligible
[ ] Update RSVP enum to also have SPECTATOR
[ ] Admin Ensemble details screen: move a Member between Participant, Spectator, and Declined
[ ] Update labels and location of "accept" and "decline" buttons

# Later

* Rename "accept" to "participate" [in rotation]
* Change storage of Member registrations from 3 separate Sets to a single Set, where there's an Enum for each (PARTICIPANT, SPECTATOR, and DECLINED)
* Convert Ensemble to use the Snapshot Persistence pattern

## UI

* Change table to fixed width grid instead of flex to avoid columns changing size and moving around

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
 
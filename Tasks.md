# Tasks

1. Ensemble: Add "Spectators" (as Set<MemberId>)
   [X] void joinAsSpectator(MemberId memberId)
     [X] remove from Accepted 
     [X] remove from Declined
   [X] Set<MemberId> spectators()
   [ ] Accept removes from Spectators
   [ ] Decline removes from Spectators
2. Update RSVP enum to also have SPECTATOR
3. EnsembleService: joinAsSpectator(EnsembleId, MemberId)
4. Add "Spectators" column to the member-register template
   * Add to the EnsembleSummaryView object
   * Add button for "Join as Spectator"
5. MemberController: handle the POST for Join as Spectator
6. Admin Ensemble details screen: move a Member between Participant, Spectator, and Declined
7. Update labels and location of "accept" and "decline" buttons

# Later

* Add notification when joining as spectator
* Change storage of Member registrations from 3 separate Sets to a single Set,
  where there's an Enum for each (PARTICIPANT, SPECTATOR, and DECLINED)
* 
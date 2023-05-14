# Tasks

1. Ensemble: Add "Spectators" (as Set<MemberId>)
   [X] void joinAsSpectator(MemberId memberId)
     [X] remove from Accepted 
     [X] remove from Declined
   [X] Set<MemberId> spectators()
   [ ] Accept removes from Spectators
   [ ] Decline removes from Spectators
2. EnsembleService: joinAsSpectator(EnsembleId, MemberId)
3. Add "Spectators" column to the member-register template
   * Add to the EnsembleSummaryView object
   * Add button for "Join as Spectator"
4. MemberController: handle the POST for Join as Spectator
5. Admin Ensemble details screen: move a Member between Participant, Spectator, and Declined
6. Update labels and location of "accept" and "decline" buttons

# Later

* Add notification when joining as spectator
* Change storage of Member registrations from 3 separate Sets to a single Set,
  where there's an Enum for each (PARTICIPANT, SPECTATOR, and DECLINED)
* 
# To Do Next

[ ] Admin management of Huddles
    [X] Add user as participant - where participant is only Name and GitHub-Username
    [X] Add discord username to participant info
    [ ] Remove participant

# External Integration

[ ] MobTi.me - websocket connection to add list of names from a specific huddle
    Figure out: protocol, when to push the info from MobOrg to Mobti.me.

# To Do Offline

[ ] Add Tailwind design to pages

[X] Mock the security for WebConfigurationTest

## Participant Features

[ ] Show Available Huddles for a User
    [ ] Only show future Huddles
    [ ] Only show Huddles that are not full
    [ ] Only show Huddles that the User isn't already registered for

[ ] User registers for a Huddle
    [ ] Precondition: Can't already be a registered Participant for that Huddle
    [ ] Postcondition: Participant belongs to the Huddle and set of Participants is Unique

[ ] Register as new User
    [ ] Onboard: GitHub, instructions for installing mob.sh, etc.
# Tasks To Do Next

[X] Security roles
    [ ] Logout didn't lead me back to login page?
    [X] Admin pages only available to me (hardcoded my username)
    [ ] Registration pages available to Members
        [X] Setup security authority configuration
        [ ] Create Repository for Members
            [ ] Use Discord role to define membership?

[ ] Externalize to configuration the MobTimer URI (reference https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.external-config) 

[ ] Admin management of Huddles
    [X] Add user as participant - where participant is only Name and GitHub-Username
    [X] Add discord username to participant info
    [ ] Remove participant
    [ ] Filter huddles: All|Future, Filled|Have Space, Free/Public|Member|Paid

[ ] Persistence with Spring Data JDBC ("lightweight ORM")

# External Integration

[X] MobTi.me - websocket connection to add list of names from a specific huddle
    Figure out: protocol, when to push the info from MobOrg to Mobti.me.

# To Do Offline

[ ] Add Tailwind design to pages

[X] Mock the security for WebConfigurationTest

## Participant Features

[ ] Only allow known Members (identified by their GitHub username) to self-register

[ ] Show Available Huddles for a Member
    [ ] Only show future Huddles
    [ ] Only show Huddles that are not full
    [ ] Only show Huddles that the User isn't already registered for

[ ] User registers for a Huddle
    [ ] Precondition: Can't already be a registered Participant for that Huddle
    [ ] Postcondition: Participant belongs to the Huddle and set of Participants is Unique

[ ] Register as new User
    [ ] Onboard: GitHub, instructions for installing mob.sh, etc.
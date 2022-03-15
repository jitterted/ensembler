# Mob Timer WebSocket "Spec"

Reverse-engineering the WS spec for use with mobti.me

## URI

The URI is: `${protocol}://${window.location.hostname}:${window.location.port}/${timerId}`;

e.g., wss://mobti.me/jitterted

## Connect

Returns

```json
{
    "type": "timer:ownership",
    "isOwner":false
}
```

## Mob Participants

Mob update is a "put", pushing the new list into the timer.

Type is `mob:update`, `mob` is an array of participants. Each participant is an object with:

`id` - base-36 random number in 10 characters
`name` - name of participant

```json
{
  "type": "mob:update",
  "mob": [
    {"id":"f7y8rlt79m","name":"Manoj"},
    {"id":"nmhms2ovj3","name":"Mike"},
    {"id":"asdfjkh123","name":"Ted"}
  ]
}
```

## Timer Settings

Settings for turn duration and roles and their ordering, `duration` is in milliseconds.

```json
{
  "type": "settings:update",
  "settings": {
    "mobOrder": "Typist,Navigator",
    "duration": 300000
  }
}
```

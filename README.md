# Ensembler (aka MobReg) - The Ensemble Programming Registrar

I wrote this tool to help me manage public (and private) remote mob programming sessions, where participants are not part of an existing team, but are individuals joining to learn a skill, or to help develop a product.
Since folks can come and go, managing who is participating in which mob programming session (known as an "ensemble") can get tedious.
Managing their access to the GitHub repository, knowing if they're new to mobbing, and making sure no more than 5 people are part of each ensemble, etc., pushed me over the edge into creating this tool.

This is currently a Work In Progress, being built almost 100% in public, [live on Twitch](https://JitterTed.Live).

## Questions?

Join me on my Discord in the dedicated `#mob-reg` channel: https://discord.gg/FYSkuufDSH.

## Environment Variables

To run this project, you will need to add the following environment variables
or update the `application.properties` file directly.

(to update...)

`github.oauth2.clientId`

`github.oauth2.clientSecret`


## Installation & Deployment

Requires Java 17 (or later) and uses Maven for building.

Since it uses GitHub OAuth2 for authentication, you'll need to register this application with your GitHub account if you want to run it yourself.

## Development Setup

### Docker

Docker is needed for running the TestContainer-based database tests. If you don't have docker running, those tests will be skipped.

See `PostgresTestcontainerBase` for information on the reusable Testcontainer setup.

### Tailwind CSS

We're using Tailwind CSS 3.x with the platform-specific command-line tool (CLI). You'll need to install that separately from https://tailwindcss.com/blog/standalone-cli#get-started. You can keep it running in the background when working on HTML files (templates):

```
tailwindcss -i ./src/main/resources/static/ensembler.css -o ./src/main/resources/static/tailwind.css --watch
```

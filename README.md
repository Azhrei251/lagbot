![workflow](https://github.com/azhrei251/lagbot/actions/workflows/docker-image.yml/badge.svg)

A simple discord music bot written in Kotlin using Javacord and LavaPlayer.

To run locally, update ```discord.token``` in the properties file with your bot token and add the bot to your server. You may need to fill out other properties also.

poToken and visitorData are used to improve YouTube playback reliability. To generate use: https://github.com/iv-org/youtube-trusted-session-generator

<h4>Docker Compose</h4>

```yaml
lagbot:
  image: azhrei251/lagbot:latest
  environment:
    DISCORD_TOKEN: YOUR_TOKEN_HERE
    AFK_TIMEOUT: 600000
    GITHUB_TOKEN: YOUR_TOKEN_HERE
    SPOTIFY_CLIENT_ID: YOUR_CLIENT_ID_HERE
    SPOTIFY_CLIENT_SECRET: YOUR_CLIENT_SECRET_HERE
    DEBUG: false
    PO_TOKEN: YOUR_PO_TOKEN_HERE
    VISITOR_DATA: YOUR_VISITOR_DATA_HERE
    LOCAL_AUDIO_REFRESH_PERIOD: 86400000
  volumes:
    - /path/to/your/audio:/audio
restart: unless-stopped
  ```  
<h4>Codebase commands</h4>

```gradlew run```: Run the bot locally

```gradlew shadowJar```: Build a fat jar

```./docker-build.sh```: Generate a docker image

```./docker-run.sh```: Run the docker image locally

<h4>Bot commands:</h4>

```!help```: Provides a list of commands and their uses

```!play {identifier}```: Adds the requested song to the end of the queue

```!playnext {identifier}```: Adds the requested song to the front of the queue

```!playnow {identifier}```: Immediately plays the requested song

```!stop```: Stops the music playback and clears the queue

```!pause```: Pauses the music playback

```!resume```: Resumes the music playback

```!queue```: Displays the current queue

```!skip```: Skips the currently playing song

```!clear```: Clears the queue

```!remove {index}```: Removes the song at the given index

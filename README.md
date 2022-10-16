![workflow](https://github.com/azhrei251/lagbot/actions/workflows/docker-image.yml/badge.svg)

A simple discord music bot written in Kotlin using Javacord and LavaPlayer.

To use, update ```discord.token``` in the properties file with your bot token and add the bot to your server.

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

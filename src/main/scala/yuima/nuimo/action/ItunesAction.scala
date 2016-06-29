// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.action

import yuima.nuimo.Nuimode
import yuima.nuimo.config.LedImage

object ItunesAction {
  def playpause = {
    val cmd = """tell application "iTunes"
                |  playpause
                |end tell
              """.stripMargin
    Nuimode.runAppleScript(cmd)
  }

  def activate = {
    val cmd = """tell application "iTunes"
                |  activate
                |end tell
              """.stripMargin
    Nuimode.runAppleScript(cmd)
  }

  def choosePlayListWithActivatingItunes = {
    val cmd = """set prevApp to (path to frontmost application as text)
                |
                |tell application "iTunes"
                |  activate
                |  set shuffle enabled to true
                |	 play playlist (item 1 of (choose from list (get name of playlists as list)))
                |end tell
                |
                |tell application prevApp to activate
              """.stripMargin
    Nuimode.runAppleScript(cmd)
  }

  def choosePlayList = {
    val cmd = """tell application "iTunes"
                |	 set plists to (get name of playlists as list)
                |end tell
                |
                |activate
                |choose from list (plists) with prompt "Choose a playlist"
                |
                |if result is not false then
                |	 tell application "iTunes"
                |    play playlist (item 1 of result)
                |  end tell
                |end if
              """.stripMargin
    Nuimode.runAppleScript(cmd)
  }

  def fadeInOut(client: Nuimode, uuid: String, duration: Double = 0.5) = {
    if (isPlaying)
      client.writeLedImage(uuid, LedImage.pause)
    else
      client.writeLedImage(uuid, LedImage.play)

    val resolution = getSoundVolume min 40
    val cmd = s"""property resolution : $resolution
                  |property delayIncr : ${ duration / resolution }
                  |tell application "iTunes"
                  |  set originalVol to sound volume
                  |  set volIncr to originalVol div resolution
                  |  if player state is not playing then
                  |    set sound volume to 0
                  |    play
                  |    -- Fade in
                  |    repeat while (sound volume ≤ (originalVol - volIncr))
                  |      set sound volume to (sound volume + volIncr)
                  |      delay delayIncr
                  |    end repeat
                  |  else
                  |    -- Fade out
                  |    repeat while (sound volume > 0)
                  |      set sound volume to (sound volume - volIncr)
                  |      delay delayIncr
                  |    end repeat
                  |    pause
                  |  end if
                  |  set sound volume to originalVol
                  |end tell
      """.stripMargin
    Nuimode.runAppleScriptSync(cmd)
  }

  def getSoundVolume = {
    Nuimode.runAppleScriptSync( """tell application "iTunes"
                                  |	 return sound volume
                                  |end tell
                                """.stripMargin).toInt
  }

  def isPlaying = {
    val cmd = """tell application "iTunes"
                |  if player state is playing then
                |    return true
                |  else
                |    return false
                |  end if
                |end tell
              """.stripMargin
    Nuimode.runAppleScriptSync(cmd).toBoolean
  }

  def prevTrack(client: Nuimode, uuid: String) = {
    client.writeLedImage(uuid, LedImage.backward)
    val cmd = """tell application "iTunes"
                |  back track
                |end tell
              """.stripMargin
    Nuimode.runAppleScript(cmd)
  }

  def nextTrack(client: Nuimode, uuid: String) = {
    client.writeLedImage(uuid, LedImage.forward)
    val cmd = """tell application "iTunes"
                |      next track
                |end tell
              """.stripMargin
    Nuimode.runAppleScript(cmd)
  }

  def notifyCurrentTrack = {
    val cmd = """tell application "iTunes"
                |	 set trackName to name of current track
                |	 set trackArtist to artist of current track
                |	 set trackAlbum to album of current track
                |	 set trackTime to time of current track
                |	 set trackPosition to player position
                |
                |	 set min to "0"
                |	 set sec to "00"
                |	 if 59 < trackPosition then
                |	   set min to trackPosition div 60
                |	   set sec to round (trackPosition mod 60)
                |	 else
                |	   set min to "0"
                |	   set sec to round (trackPosition)
                |	 end if
                |
                |	 if sec < 10 then
                |	   set sec to "0" & sec
                |	 end if
                |
                |	 set currentTime to (min & ":" & sec as text)
                |
                |	 set str to trackArtist & " - " & trackAlbum & return & currentTime & " / " & trackTime
                |	 display notification str with title trackName
                |end tell
              """.stripMargin
    Nuimode.runAppleScript(cmd)
  }
}

// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.action

object ChromeYoutubeAction {
  def tryPlayPause = {
    AppleScript(
                 """set foundYoutube to false
                   |tell application "Google Chrome"
                   |  repeat with t in tabs of windows
                   |    tell t
                   |      if URL starts with "http://www.youtube.com/watch" or URL starts with "https://www.youtube.com/watch" then
                   |        execute javascript "
                   |          var player = document.getElementById('movie_player') || document.getElementsByTagName('embed')[0];
                   |          if (player) {
                   |            document.getElementsByClassName('ytp-play-button')[0].click()
                   |		       }
                   |        "
                   |        set foundYoutube to true
                   |        exit repeat
                   |      end if
                   |    end tell
                   |  end repeat
                   |end tell
                   |return foundYoutube
                 """.stripMargin).runScriptSync.toBoolean
  }

  def tryNextTrack = {
    AppleScript(
                 """set foundYoutube to false
                   |tell application "Google Chrome"
                   |  repeat with t in tabs of windows
                   |    tell t
                   |      if URL starts with "http://www.youtube.com/watch" or URL starts with "https://www.youtube.com/watch" then
                   |        execute javascript "
                   |          var player = document.getElementById('movie_player') || document.getElementsByTagName('embed')[0];
                   |          if (player) {
                   |            document.getElementsByClassName('ytp-next-button')[0].click()
                   |          }
                   |        "
                   |        set foundYoutube to true
                   |        exit repeat
                   |      end if
                   |    end tell
                   |  end repeat
                   |end tell
                   |return foundYoutube
                 """.stripMargin).runScriptSync.toBoolean
  }

  def tryPrevTrack = {
    AppleScript(
                 """set foundYoutube to false
                   |tell application "Google Chrome"
                   |  repeat with t in tabs of windows
                   |    tell t
                   |      if URL starts with "http://www.youtube.com/watch" or URL starts with "https://www.youtube.com/watch" then
                   |        execute javascript "
                   |          var player = document.getElementById('movie_player') || document.getElementsByTagName('embed')[0];
                   |          if (player) {
                   |            document.getElementsByClassName('ytp-prev-button')[0].click()
                   |          }
                   |        "
                   |        set foundYoutube to true
                   |        exit repeat
                   |      end if
                   |    end tell
                   |  end repeat
                   |end tell
                   |return foundYoutube
                 """.stripMargin).runScriptSync.toBoolean
  }
}

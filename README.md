# NoteEdgeSidebarService
This project is about creating a Sidebar for the Samsung Galaxy Note Edge running non Stock Roms (aiming at Android Oreo)

It will work best in combination with my other project https://github.com/agrucza/XposedNoteEdgeResizer which will resize the Android GUI / Android Apps to only take the screenspace without the edge screen.

Current status:
---------------
Whats working:
1. There is a settings app (Note Edge Sidebar Settings) in the Launcher.
2. Inside the settings app the visibility of the sidebar content can be toggled. (just a black bar or (at the moment) the launcher)
3. The app lists all installed apps and by clicking on them they are added to the launcher in the sidebar.
4. When clicking on the app in the sidebar launcher the app will be launched with the CATEGORY_LAUNCHER intent.
5. Apps will removed when clicking on the list again.
6. The Launcher has a little eye icon as first item in the list this will toggle the launcher icons visibility. In case there are moments the icons should not be shown without setting the whole sidebar to black in the settings app.
7. When rotating the device the sidebar will be rearranged to match the Edge part of the screen.

There are some issues:
1. When the Sidebar is installed and launched it will crash as the Sidebar needs the permission to show over other apps. Go to "settings -> apps -> Node Edge Sidebar -> permissions -> show over other apps" and grant permission. The next time the app is run the sidebar will be shown.
2. The Sidebar seems to be partially overlayed by the status bar. This is an issue with OREO which seems to prevent apps to overlay the status bar.
3. The settings app will list all installed apps, this will need a few seconds to show the list.
4. When an app should be added to the launcher dont click the switch as this currently does not work. Click on the app item in the list but not the switch. This will add the app to the launcher. Also the switch is not updated to be switched to on right after that the switch will go to on after the list is updated (rotation or settings app started again).
5. The launcher icons order is ordered like the apps are reported by the system.
6. If you add more icons than there is space on the sidebar they will not be reachable. (At the moment)
7. The Android power management tends to kill the Service quite often especially when the device is woken up.
8. At the moment the Sidebar will not be shown at the Lockscreen.

Comming up next:
----------------
1. Rearranging the Launcher icons
2. Adding a scrolling to the icons so that you can reach off screen icons
3. Adding launcher apps intents to get the icons updated when apps have notifications. (Like new messages or calls or stuff)
4. Adding an autostart intent for starting the Sidebar when the system has done booting.
5. Finding a way to show the sidebar at the lockscreen.
6. Adding some additional panels to the sidebar to have access to other functions on the sidebar.

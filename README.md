# Quinoid

**Application to use TiddlyWiki on Android**

After Android 4.4 (KitKat) the Android permissions and file access model changed.
 This meant that the older Android  (unnamed) application for
TiddlyWiki could not always save. 
This project will hopefully work better with the new permissions model.

In addition, thinking about additonal aspirational directions:

* Switch active page by swiping (working?)
* Access to files provided by the new system explorer, enabling access to services such as GDrive. (working?)
* Display TW file information (partially working?)
* Select which files are active for swiping. (working?)
* Link to external websites (working -- user chooses browser)
* Create local files based on editions

## DISCLAIMER

All releases should be consider **beta** or even **alpha** quality. Be sure to make any backups before using with important TW files. Be
especially careful when dealing with files on GDrive or other synchronized platforms. At the moment, Quinoid will consider itself
"in charge" of any files you have listed, and could possibly write over your synchronized platform file with its own internal version.

In all cases, it is up to you to properly backup and secure your files.

## Usage Notes

Note that there are two file selection mechanisms. "File Explorer" and "System Exlorer". __File Explorer__ will probably only work
with files that Android considers "internal". This is due to changes in Android permissons starting with Android 4.4.
However, the __File Explorer__ is probably more efficient than __System Explorer__, so if you can get it to work that might be the first choice. In future editions,
the __File Explorer__ may be disabled for Android 5 and greater.

A long press on the line item in the list view will bring up a dialog where you can change the display name (not the internal TiddlyWiki name), mark the file as browsable, or mark the file to be removed (in which case, the file will be removed as soon as you confirm your choice).

When you've made modifications to a TW file and need the page to refresh, you have two options. The first is that you can use the "back" button to back all of the way out of Quinoid. Then return to Quinoid and continue as before. The other is to import the small tiddler, "Refresh-for-Quinoid.json", which should be available along with the releases. After importing and refreshing (either before loading into Quinoid, or by backing out of Quinoid as outline above), you should be able to use the normal refresh/reload buttons made available by TW.

### Notes about GDrive

I don't use GDrive myself very much. I've only been testing it in emulators. The behavior in emulators may be different than the behavior on actual devices. Based on several observations, it appears possible that a TW file that has been accessed by Quinoid from GDrive may not send back a date stamp. Thus it appears a prudent course to follow after using GDrive in offline mode goes something like:

1. Back out of Quinoid to flush any current work.
2. Visit the GDrive app, and restore your file to "online" 
3. Click on the "Reload/Recycle" button. GDrive will announce that it is uploading your file.
4. Switch your file to "offline" again if you continue to work in Quinoid.

It also appears that you don't need to use your file in offline mode at all if you are continously connected to the internet. However, I haven't tested this and am not sure what happens when you "walk" out of internet range. In all cases, be careful. Don't commit any work that you really want to keep.

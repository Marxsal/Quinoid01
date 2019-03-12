# Quinoid 0.0.8alpha

**Application to use TiddlyWiki on Android**

After Android 4.4 (KitKat) the Android permissions and file access model changed.
 This meant that the older Android  (unnamed) application for
TiddlyWiki could not always save. 
This project will hopefully work better with the new permissions model.

## Features

* Switch active page by swiping (working)
* List active TW files in working list, using your own preferred title.
* Hand off links to external websites to other browsers
* Select which files should be used in browse (swipe) mode.
* Display TW files' favicon, if available
* Access to files provided by the Android system explorer, enabling access to services such as GDrive. (working?)
* Create local files based on editions (in progess)
* Configurable list of TW files that can be downloaded on demand (next?)
* Auto-load files from directory (working?).
* Capture text shared from other apps via Android sharing .
* Import tiddlers, plugins, etc.

## Aspirational / roadmap

* Serve up files? Maybe.
* Light file maintenance - physically delete TW's no longer wanted.
* User preferences to automatically open in browsing session

## DISCLAIMER

All releases should be consider **beta** or even **alpha** quality. Be sure to make any backups before using with important TW files. Be
especially careful when dealing with files on GDrive or other synchronized platforms. At the moment, Quinoid will consider itself
"in charge" of any files you have listed, and could possibly write over your synchronized platform file with its own internal version.

In all cases, it is up to you to properly backup and secure your files.

## Usage Notes

Note that there are two file selection mechanisms. "File Explorer" and "System Exlorer". "File Explorer" is now inside the overflow menu. The "System Explorer" appears as a "+" icon on the action bar. __File Explorer__ will probably only work
with files that Android considers "internal". This is due to changes in Android permissons starting with Android 4.4.
However, the __File Explorer__ is probably more efficient than __System Explorer__, so if you can get it to work that might be the first choice. In future editions,
the __File Explorer__ may be disabled for Android 5 and greater.

A long press on the line item in the list view will bring up a dialog where you can change the display name (not the internal TiddlyWiki name), mark the file as browsable, mark the file as a clipboard, or mark the file to be removed (in which case, the file will be removed as soon as you confirm your choice).

When you've made modifications to a TW file and need the page to refresh, you have two options. The first is that you can use the "exit" button to exit all of the way out of Quinoid. Then return to Quinoid and continue as before. This seems to work.

The other is to import the small tiddler, "Refresh-for-Quinoid.json", which should be available along with the releases. After importing and refreshing (either before loading into Quinoid, or by backing out of Quinoid as outline above), you should be able to use the normal refresh/reload buttons made available by TW.

### Capturing/sharing text with Quinoid

You can send (share) text with Quinoid, though I don't know how robust it is, so test carefully.

In Quinoid, you can long-press on an item and select it to be a "clipboard" for Quinoid. If you do not make a selection, then Quinoid will use the first item in the list by default.

In your 3rd-party application, select some text the usual way and then choose to "share". From the selection menu, pick Quinoid. The screen will go blank momentarily before returning to your app. Behind the scenes, it's activating Quinoid and saving your text.

Back in Quinoid, navigate to your target page. After the page loads you should wait until it says "wiki saved." If you check under the "recent" tab, you'll see that there is one or more tiddlers with the name "Quinoid-Clip-<date-stamp>". Currently (vsn 0.0.007) all the text that you shared will be in one tiddler. A new tiddler will not be created until you have visited the page once.

Various notes and warnings: I don't know if it matters whether Quinoid is launched for this to work. Perhaps people can provide feedback. I've tested it with Quinoid always launched. I also don't know if it matters whether you are the dashboard page or on the browse pages, though it seems to work for me either way. Awaiting feedback.

Note really well: The text that is captured is *not* transferred to the target TW file until you visit it. So if you use some other tool (e.g. web-browser) to visit the page, the new text will not be available.

### Using the File downloader and auto-loading of TW-files.

Under the activity-bar 3-dot menu, you can select "Resources" which will allow you to download one or more pre-existing starter files. Click on "Resources" and then click on whatever items you are interested in. If you are connected to the internet the downloads should start immediately. The file(s) will be saved to a TwFiles sub-folder of your public documents folder. This subfolder may be on your physical internal drive or your external drive, depending on Android version and manufacturer. I targeted the external drive, but as far as I can tell, I (the developer) don't have full control.  I'll be interested in hearing reports back.

Whatever location is chosen, any TW files (or html files) placed in that directory will be automatically added to Quinoid's file list upon start-up.  

### Configuring Downloadable File List

The first time Quinoid is loaded on your device, a new resource file (TwResources.json) will be created in the same directory mentioned above for downloading resource files. It contains a handful of existing sites (including possibly this help file). This file is in JSON format. If you are comfortable with JSON, you can edit the JSON file to create your own list of starter files. For instance, you might want to have an "empty" pre-release file, rather than an empty release file to work with. Or, if you are a gamer, you might want to download a new gaming TW for each game you want to track. Or you might provide your own curated list of files to friends and colleagues.

Currently the TwResources file is very simple, containing only the three properties, "id", "title", and "description". "id" represents the path to the file you wish to download. "title" is the short title that will be displayed in the menu, and "description" is a longer field for explaining the nature of the target HTML file.

The main thing to keep in mind is that the file must follow the "real" rules of JSON. Certain characters may have to be escaped. In particular, when making file paths, each forward slash (/) must be prefixed by a backward slash, thusly: \/ . Breaking the JSON will probably mean that no menu will appear when you click on the "Resources" menu item.

### Notes about GDrive

I don't use GDrive myself very much. I've only been testing it in emulators. The behavior in emulators may be different than the behavior on actual devices. Based on several observations, it appears possible that a TW file that has been accessed by Quinoid from GDrive may not send back a date stamp. Thus it appears a prudent course to follow after using GDrive in offline mode goes something like:

1. Exit out of Quinoid (via the exit option) to flush any current work.
2. Visit the GDrive app, and restore your file to "online" 
3. Click on the "Reload/Recycle" button. GDrive will announce that it is uploading your file.
4. Switch your file to "offline" again if you continue to work in Quinoid.

It also appears that you don't need to use your file in offline mode at all if you are continously connected to the internet. However, I haven't tested this and am not sure what happens when you "walk" out of internet range. In all cases, be careful. Don't commit any work that you really want to keep.# Quinoid


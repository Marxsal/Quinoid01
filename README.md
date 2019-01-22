# Quinoid01

**Application to use TiddlyWiki on Android**

After Android 4.4 (KitKat) the Android permissions and file access model changed. This meant that the older Android  (unnamed) application
for
TiddlyWiki could not always save. This project will hopefully work better with the new permissions model.

In addition, thinking about additonal aspirational directions:

* Switch active page by swiping (working?)
* Access to files provided by the new system explorer, enabling access to services such as GDrive. (working?)
* Display TW file information (partially working?)
* Select which files are active for swiping.
* Link to external websites
* Create local files based on editions

## DISCLAIMER

All releases should be consider **beta** or even **alpha** quality. Be sure to make any backups before using with important TW files. Be
especially careful when dealing with files on GDrive or other synchronized platforms. At the moment, Quinoid will consider itself
"in charge" of any files you have listed, and could possibly write over your synchronized platform file with its own internal version.

In all cases, it is up to you to properly backup and secure your files.

## Usage Notes
Note that there are two file selection mechanisms. "File Explorer" and "System Exlorer". __File Explorer_ will probably only work
with files that Android considers "internal". This is due to changes in Android permissons starting with Android 4.4.
However, the __File Explorer__ is probably more efficient than __System Explorer__, so if you can get it to work that might be the first choice. In future editions,
the __File Explorer__ may be disabled for Android 5 and greater.
# Android Notes App

<p>Android app built with MVVM architecture for writing down notes and saving them locally.</p>

<h3><strong>Features</strong></h3>

<p>- Uses Room database to store notes / tags.
- Fragments to (only) show all notes, favorites, reminders (notes which have reminder set), trash.
- Sets date and time formatting depending on user country, user can also override this in settings activity.
- Search notes (title / text), query run as the user type.
- Sort alphabetically (title), by creation or modified date, with favorites on top.
  Dynamically updated in background view as user selects how to sort.
- Deleting notes put them in the trash, deleting them from trashed state completely deletes them.
- Set reminder date / time for a note, this will send a system notification containing title + text to the user.
- Switch between light / dark theme, icons / clickable backgrounds change accordingly.
- Add tags to notes to allow user specific sorting.
- Favoritize notes.
- Take a photo and add it to Note.</p>

<h3><strong>Cloning</strong></h3>
<p>If you are going to clone this repository, please note that in order for gradle to sync properly you either need to modify the keystore parameters or remove the line <code>signingConfig signingConfigs.config</code> in (buildTypes -> release) inside the app level build.gradle.</p>
<p>This app uses Google Firebase but the required google-services.json file is not published on github for security reasons so in order for the app to run you have two options:</p>
<p>1. Add your own google-services.json file at <code>app/google-services.json</code> which you get from the Firebase console.
<br>2. Remove dependencies + code which are related to Firebase:</p>
<p>In build.gradle (project), remove the lines:
<br><code>classpath 'com.google.gms:google-services:x.x.x'</code>
<br><code>classpath 'com.google.firebase:perf-plugin:x.x.x'</code></p>
<p>In build.gradle (app), remove the lines:
<br><code>apply plugin: 'com.google.gms.google-services'</code>
<br><code>apply plugin: 'com.google.firebase.firebase-perf'</code>
<br><code>implementation "com.google.firebase:firebase-analytics:x.x.x"</code>
<br><code>implementation "com.google.firebase:firebase-perf:x.x.x"</code></p>
<p>Remove all lines of code pointing to and the <code>AnalyticsUtil.java</code> class.</p>

<h3><strong>Screen Captures</strong> (v1.0)</h3>

<img src="https://i.imgur.com/zsVeASC.png" width="32%"> <img src="https://i.imgur.com/uoiD80E.png" width="32%"> <img src="https://i.imgur.com/WLjaqy9.png" width="32%">

<img src="https://i.imgur.com/faU1yRs.png" width="97%">

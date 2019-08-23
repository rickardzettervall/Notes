# Android Notes App
Android app built with MVVM architecture for writing down notes and saving them locally.

<h3><strong>Features</strong></h3>

- Uses Room database to store notes / tags.
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

<strong><h3>Screen Captures</h3></strong>

<img src="https://i.imgur.com/zsVeASC.png" width="32%"> <img src="https://i.imgur.com/uoiD80E.png" width="32%"> <img src="https://i.imgur.com/WLjaqy9.png" width="32%">

<img src="https://i.imgur.com/faU1yRs.png" width="97%">

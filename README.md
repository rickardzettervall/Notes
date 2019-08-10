# Android Notes App (WIP)
Android app for writing down notes and saving them locally.

<h3><strong>Features</strong> (some not yet implemented)</h3>

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

<img src="https://i.imgur.com/faU1yRs.png">

<strong><h3>License</h3></strong>

Copyright 2019, Rickard Zettervall.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this project except in compliance with the License.
You may obtain a copy of the License at <a href="https://www.apache.org/licenses/LICENSE-2.0" target="_blank">http://www.apache.org/licenses/LICENSE-2.0</a>.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

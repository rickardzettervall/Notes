package tech.zettervall.notes.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Library;

public abstract class LibrariesUtil {

    /**
     * Create and return the list of open source libraries used
     * in the project, in order to give credit in the About Activity.
     * Since the libraries are static you add any new ones in this method.
     *
     * @param context Needed to reach strings.xml
     * @return List of LibrariesUtil
     */
    public static List<Library> getLibraries(Context context) {
        List<Library> libraries = new ArrayList<>();

        // Parceler
        libraries.add(new Library(context.getString(R.string.library_parceler),
                context.getString(R.string.library_parceler_desc),
                context.getString(R.string.library_parceler_url)));

        // Mockito
        libraries.add(new Library(context.getString(R.string.library_mockito),
                context.getString(R.string.library_mockito_desc),
                context.getString(R.string.library_mockito_url)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return sortLibraries(libraries);
        }
        return libraries;
    }

    /**
     * Sort List of LibrariesUtil alphabetically.
     */
    @TargetApi(24)
    private static List<Library> sortLibraries(List<Library> libraries) {
        libraries.sort(new Comparator<Library>() {
            @Override
            public int compare(Library o1, Library o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        return libraries;
    }
}

package tech.zettervall.notes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Library;

/**
 * Used to display open source LibrariesUtil in About Activity.
 */
public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private OnLibraryClickListener onLibraryClickListener;
    private List<Library> libraries;

    public LibraryAdapter(OnLibraryClickListener onLibraryClickListener, List<Library> libraries) {
        this.onLibraryClickListener = onLibraryClickListener;
        this.libraries = libraries;
    }

    @NonNull
    @Override
    public LibraryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.list_library, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.ViewHolder holder, int position) {
        final Library currentLibrary = libraries.get(position);
        if (currentLibrary != null) {
            holder.title_tv.setText(currentLibrary.getTitle());
            holder.description_tv.setText(currentLibrary.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return libraries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title_tv, description_tv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.title_tv);
            description_tv = itemView.findViewById(R.id.description_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onLibraryClickListener.onLibraryClick(getAdapterPosition());
        }
    }

    public interface OnLibraryClickListener {
        void onLibraryClick(int index);
    }

    public List<Library> getLibraries() {
        return new ArrayList<>(libraries);
    }
}

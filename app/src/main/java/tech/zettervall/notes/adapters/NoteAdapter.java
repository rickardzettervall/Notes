package tech.zettervall.notes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;

/**
 * Adapter for displaying Notes in RecyclerView.
 */
public class NoteAdapter extends PagedListAdapter<Note, NoteAdapter.NoteViewHolder> {

    private static final String TAG = NoteAdapter.class.getSimpleName();
    /**
     * Callback to check for difference and decide whether to update the list.
     */
    private static DiffUtil.ItemCallback<Note> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Note>() {
                @Override
                public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                    // Check Note class equals method for more information.
                    return oldItem.equals(newItem);
                }
            };
    private OnNoteClickListener mOnNoteClickListener;

    public NoteAdapter(OnNoteClickListener onNoteClickListener) {
        super(DIFF_CALLBACK);
        mOnNoteClickListener = onNoteClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_note, viewGroup, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = getItem(position);
        if (note != null) {
            if (note.getTitle().isEmpty() && !note.isFavorite() && !(note.notificationEpoch > 0)) {
                // Hide title field when empty
                holder.mTitleLayout.setVisibility(View.GONE);
            } else {
                holder.mTitleLayout.setVisibility(View.VISIBLE);
                holder.mHeadlineTv.setText(note.getTitle());
            }

            if (note.getText().isEmpty()) { // Hide text field when empty
                holder.mTextTv.setVisibility(View.GONE);
            } else {
                holder.mTextTv.setVisibility(View.VISIBLE);
                holder.mTextTv.setText(note.getText());
            }

            // Favorite
            if (note.isFavorite()) {
                holder.mFavorite.setVisibility(View.VISIBLE);
            } else {
                holder.mFavorite.setVisibility(View.GONE);
            }

            // Reminder
            if (note.getNotificationEpoch() > 0) {
                holder.mReminder.setVisibility(View.VISIBLE);
            } else {
                holder.mReminder.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Callback interface.
     */
    public interface OnNoteClickListener {
        void onNoteClick(int index);
    }

    /**
     * ViewHolder
     */
    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mHeadlineTv, mTextTv;
        private ImageView mFavorite, mReminder;
        private LinearLayout mTitleLayout;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find Views
            mHeadlineTv = itemView.findViewById(R.id.title_tv);
            mTextTv = itemView.findViewById(R.id.text_tv);
            mFavorite = itemView.findViewById(R.id.favorite_iv);
            mReminder = itemView.findViewById(R.id.reminder_iv);
            mTitleLayout = itemView.findViewById(R.id.title_layout);

            // Set OnClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnNoteClickListener.onNoteClick(getAdapterPosition());
        }
    }
}

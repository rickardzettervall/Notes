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
                holder.mTitleLinearLayout.setVisibility(View.GONE);
            } else {
                holder.mTitleLinearLayout.setVisibility(View.VISIBLE);
                holder.mTitleTextView.setText(note.getTitle());
            }

            if (note.getText().isEmpty()) { // Hide text field when empty
                holder.mTextTextView.setVisibility(View.GONE);
            } else {
                holder.mTextTextView.setVisibility(View.VISIBLE);
                holder.mTextTextView.setText(note.getText());
            }

            // Favorite
            if (note.isFavorite()) {
                holder.mFavoriteImageView.setVisibility(View.VISIBLE);
            } else {
                holder.mFavoriteImageView.setVisibility(View.GONE);
            }

            // Reminder
            if (note.getNotificationEpoch() > 0) {
                holder.mReminderImageView.setVisibility(View.VISIBLE);
            } else {
                holder.mReminderImageView.setVisibility(View.GONE);
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

        private TextView mTitleTextView, mTextTextView;
        private ImageView mFavoriteImageView, mReminderImageView;
        private LinearLayout mTitleLinearLayout;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find Views
            mTitleTextView = itemView.findViewById(R.id.list_note_title_textview);
            mTextTextView = itemView.findViewById(R.id.list_note_text_textview);
            mFavoriteImageView = itemView.findViewById(R.id.list_note_favorite_imageview);
            mReminderImageView = itemView.findViewById(R.id.list_note_reminder_imageview);
            mTitleLinearLayout = itemView.findViewById(R.id.list_note_title_linearlayout);

            // Set OnClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnNoteClickListener.onNoteClick(getAdapterPosition());
        }
    }
}

package tech.zettervall.notes.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.Constants;
import tech.zettervall.notes.models.Note;

public class NoteAdapter extends PagedListAdapter<Note, NoteAdapter.NoteViewHolder> {

    private static final String TAG = NoteAdapter.class.getSimpleName();
    private OnNoteClickListener mOnNoteClickListener;
    private Context mContext;

    public NoteAdapter(OnNoteClickListener onNoteClickListener, Context context) {
        super(DIFF_CALLBACK);
        mOnNoteClickListener = onNoteClickListener;
        mContext = context;
    }

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
                    return oldItem.equals(newItem);
                }
            };

    /**
     * ViewHolder
     */
    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mHeadlineTv, mTextTv, mDateTv;
        private ImageView mFavorite, mReminder;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find Views
            mHeadlineTv = itemView.findViewById(R.id.title_tv);
            mTextTv = itemView.findViewById(R.id.text_tv);
            mDateTv = itemView.findViewById(R.id.date_tv);
            mFavorite = itemView.findViewById(R.id.favorite_iv);
            mReminder = itemView.findViewById(R.id.reminder_iv);

            // Set OnClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnNoteClickListener.onNoteClick(getAdapterPosition());
        }
    }

    /**
     * Callback interface used for Click events.
     */
    public interface OnNoteClickListener {
        void onNoteClick(int index);
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
            holder.mHeadlineTv.setText(note.getTitle());
            holder.mTextTv.setText(note.getText());

            // Date
            int sortType = PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getInt(Constants.SORT_TYPE_KEY, Constants.SORT_TYPE_DEFAULT);
            if (sortType == Constants.SORT_TYPE_CREATION_DATE) {
                holder.mDateTv.setText(note.getCreationString(mContext));
            } else {
                holder.mDateTv.setText(note.getModifiedString(mContext));
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
}

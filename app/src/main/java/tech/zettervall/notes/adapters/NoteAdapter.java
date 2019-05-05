package tech.zettervall.notes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Note;

public class NoteAdapter extends PagedListAdapter<Note, NoteAdapter.NoteViewHolder> {

    private static final String TAG = NoteAdapter.class.getSimpleName();
    private OnNoteClickListener mOnNoteClickListener;

    public NoteAdapter(OnNoteClickListener onNoteClickListener) {
        super(DIFF_CALLBACK);
        mOnNoteClickListener = onNoteClickListener;
    }

    /**
     * Callback to check for difference and decide whether to update the list.
     */
    private static DiffUtil.ItemCallback<Note> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Note>() {
                @Override
                public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                    return oldItem.get_id() == newItem.get_id();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                    return (oldItem.compareTo(newItem) == 0);
                }
            };

    /**
     * ViewHolder
     */
    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mHeadlineTv, mTextTv, mDateTv;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find Views
            mHeadlineTv = itemView.findViewById(R.id.headline_tv);
            mTextTv = itemView.findViewById(R.id.text_tv);
            mDateTv = itemView.findViewById(R.id.date_tv);

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
                .inflate(R.layout.list_notes, viewGroup, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = getItem(position);
        if (note != null) {
            holder.mHeadlineTv.setText(note.getHeadline());
            holder.mTextTv.setText(note.getText());
            holder.mDateTv.setText(note.getDate());
        }
    }
}

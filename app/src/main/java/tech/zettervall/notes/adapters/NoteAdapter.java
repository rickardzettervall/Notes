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
import tech.zettervall.notes.models.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static final String TAG = NoteAdapter.class.getSimpleName();
    private OnNoteClickListener mOnNoteClickListener;
    private List<Note> mNotes;

    public NoteAdapter(OnNoteClickListener mOnNoteClickListener, List<Note> mNotes) {
        this.mOnNoteClickListener = mOnNoteClickListener;
        this.mNotes = mNotes;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mHeadlineTv, mTextTv, mDateTv;

        public ViewHolder(@NonNull View itemView) {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.list_notes, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Note note = mNotes.get(position);

        // Set TextView data
        holder.mHeadlineTv.setText(note.getHeadline());
        holder.mTextTv.setText(note.getText());
        holder.mDateTv.setText(note.getDate());
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public List<Note> getNotes() {
        return new ArrayList<>(mNotes);
    }

    public void setNotes(List<Note> mNotes) {
        this.mNotes = mNotes;
        notifyDataSetChanged();
    }
}

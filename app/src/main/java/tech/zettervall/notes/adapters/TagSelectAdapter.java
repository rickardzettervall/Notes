package tech.zettervall.notes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.models.Tag;

public class TagSelectAdapter extends RecyclerView.Adapter<TagSelectAdapter.ViewHolder> {

    private List<Tag> mTags;
    private OnTagClickListener mOnTagClickListener;
    private boolean[] mCheckedTags;

    public TagSelectAdapter(OnTagClickListener onTagClickListener, List<Tag> tags) {
        mOnTagClickListener = onTagClickListener;
        mTags = tags;
        mCheckedTags = new boolean[mTags.size()]; // Used for keeping track of checked states
    }

    public List<Tag> getTags() {
        return mTags;
    }

    public boolean[] getCheckedTags() {
        return mCheckedTags;
    }

    /**
     * Change the CheckBox state of single a item.
     *
     * @param index     Position in the Adapter
     * @param isChecked Checked state
     */
    public void setCheckedState(int index, boolean isChecked) {
        mCheckedTags[index] = isChecked;
        notifyDataSetChanged(); // Update Adapter
    }

    @NonNull
    @Override
    public TagSelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.list_tag_select, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagSelectAdapter.ViewHolder holder, final int position) {
        final Tag tag = mTags.get(position);

        /* Disable CheckBox click function. Checked state is set below
         * programmatically when user clicks anywhere on the adapter row. */
        holder.mTagCheckBox.setClickable(false);

        // Set CheckBox
        if (mCheckedTags[position]) {
            holder.mTagCheckBox.setChecked(true);
        } else {
            holder.mTagCheckBox.setChecked(false);
        }

        // Set title
        String tagString = "#" + tag.getTitle();
        holder.mTagTextView.setText(tagString);
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    /**
     * Callback interface.
     */
    public interface OnTagClickListener {
        void onTagClick(int index);
    }

    /**
     * ViewHolder.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CheckBox mTagCheckBox;
        private TextView mTagTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTagCheckBox = itemView.findViewById(R.id.list_tag_select_checkbox);
            mTagTextView = itemView.findViewById(R.id.list_tag_select_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnTagClickListener.onTagClick(getAdapterPosition());
        }
    }
}

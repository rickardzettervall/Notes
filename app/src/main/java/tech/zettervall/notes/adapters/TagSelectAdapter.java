package tech.zettervall.notes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

        private CheckBox tag_cb;
        private TextView tag_tv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tag_cb = itemView.findViewById(R.id.list_tag_selecter_cb);
            tag_tv = itemView.findViewById(R.id.list_tag_selecter_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnTagClickListener.onTagClick(getAdapterPosition());
        }
    }

    public List<Tag> getTags() {
        return mTags;
    }

    public boolean[] getCheckedTags() {
        return mCheckedTags;
    }

    // Change the CheckBox state of single a item
    public void setCheckedState(int index, boolean state) {
        mCheckedTags[index] = state;
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

        // Set CheckBox
        if(mCheckedTags[position]) {
            holder.tag_cb.setChecked(true);
        } else {
            holder.tag_cb.setChecked(false);
        }

        // Set title
        String tagString = "#" + tag.getTag();
        holder.tag_tv.setText(tagString);
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }
}

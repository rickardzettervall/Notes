package tech.zettervall.notes.adapters;

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
import tech.zettervall.notes.models.Tag;

/**
 * Adapter for displaying Tags in RecyclerView.
 */
public class TagAdapter extends PagedListAdapter<Tag, TagAdapter.TagViewHolder> {

    private static final String TAG = TagAdapter.class.getSimpleName();
    /**
     * Callback to check for difference and decide whether to update the list.
     */
    private static DiffUtil.ItemCallback<Tag> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Tag>() {
                @Override
                public boolean areItemsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
                    // Check Tag class equals method for more information.
                    return oldItem.equals(newItem);
                }
            };
    private OnTagClickListener mOnTagClickListener;

    public TagAdapter(OnTagClickListener onTagClickListener) {
        super(DIFF_CALLBACK);
        mOnTagClickListener = onTagClickListener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_tag, viewGroup, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = getItem(position);
        if (tag != null) {
            String tagString = "#" + tag.getTitle();
            holder.mTagTextView.setText(tagString);
        }
    }

    /**
     * Callback interface.
     */
    public interface OnTagClickListener {
        void onTagClick(int index);

        void onTagDeleteClick(int index);
    }

    /**
     * ViewHolder.
     */
    class TagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTagTextView;
        private ImageView mRemoveImageView;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find Views
            mTagTextView = itemView.findViewById(R.id.list_tag_title_textview);
            mRemoveImageView = itemView.findViewById(R.id.list_tag_delete_imageview);

            // Set OnClickListener
            itemView.setOnClickListener(this);
            mRemoveImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.list_tag_root:
                    mOnTagClickListener.onTagClick(getAdapterPosition());
                    break;
                case R.id.list_tag_delete_imageview:
                    mOnTagClickListener.onTagDeleteClick(getAdapterPosition());
                    break;
            }
        }
    }
}

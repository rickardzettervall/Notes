package tech.zettervall.notes.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.utils.BitmapUtil;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private List<String> mPhotoPaths;
    private OnPhotoClickListener mOnPhotoClickListener;

    public PhotoAdapter(OnPhotoClickListener onPhotoClickListener, List<String> photoPaths) {
        mOnPhotoClickListener = onPhotoClickListener;
        mPhotoPaths = photoPaths;
    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.list_photo, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, final int position) {
        final String photoPath = mPhotoPaths.get(position);

        if (!photoPath.isEmpty()) {
            holder.mPhotoImageView.setImageBitmap(BitmapUtil.getBitmap(photoPath));
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoPaths.size();
    }

    /**
     * Callback interface.
     */
    public interface OnPhotoClickListener {
        void onPhotoClick(int index);

        void onDeleteClick(int index);
    }

    /**
     * ViewHolder.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPhotoImageView;
        private ImageButton mDeleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPhotoImageView = itemView.findViewById(R.id.list_photo_imageview);
            mDeleteButton = itemView.findViewById(R.id.list_photo_delete_button);
            mDeleteButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == mPhotoImageView) {
                mOnPhotoClickListener.onPhotoClick(getAdapterPosition());
            } else if (v == mDeleteButton) {
                mOnPhotoClickListener.onDeleteClick(getAdapterPosition());
            }
        }
    }
}

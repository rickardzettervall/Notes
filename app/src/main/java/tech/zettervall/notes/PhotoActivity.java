package tech.zettervall.notes;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import tech.zettervall.mNotes.R;

/**
 * Activity for showing photo in full view.
 */
public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ImageView photoImageView = findViewById(R.id.activity_photo_imageview);

        if (getIntent().getExtras() != null) {
            String photoPath = getIntent().getExtras().getString(Constants.PHOTO_PATH);
            Bitmap photo = NoteFragment.getPhotoFromPath(photoPath);
            if (photo != null) {
                photoImageView.setImageBitmap(photo);
            }
        }
    }
}

package tech.zettervall.notes;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.utils.BitmapUtil;

/**
 * Activity for showing photo in full view.
 */
public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        // Find view
        ImageView photoImageView = findViewById(R.id.activity_photo_imageview);

        // Set Home menu item
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get Extras
        if (getIntent().getExtras() != null) {
            // Set title
            setTitle(getIntent().getExtras().getString(Constants.NOTE_TITLE));

            // Set photo
            String photoPath = getIntent().getExtras().getString(Constants.PHOTO_PATH);
            Bitmap photo = BitmapUtil.getBitmap(photoPath);
            if (photo != null) {
                photoImageView.setImageBitmap(photo);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

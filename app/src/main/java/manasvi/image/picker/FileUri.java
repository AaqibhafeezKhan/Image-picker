package manasvi.image.picker;

import android.net.Uri;

import java.io.File;

public class FileUri {
    private Uri imageUrl;
    private File file;

    public FileUri() {
    }

    public Uri getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
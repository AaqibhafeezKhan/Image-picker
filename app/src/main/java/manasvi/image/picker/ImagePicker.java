package manasvi.image.picker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import manasvi.image.picker.R.string;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView.Guidelines;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class ImagePicker implements ImageCallBackManager{

    private static final int CAMERA_PICTURE = 10;
    private static final int GALLERY_PICTURE = 11;
    public static final int STORAGE_PERMISSION_IMAGE = 111;
    private static final int STORAGE_PERMISSION_CAMERA = 112;
    public static final int CAMERA_PERMISSION = 115;
    private static final int CAMERA_BUT_STORAGE_PERMISSION = 116;
    private ImagePicker.OnFileChoose mOnFileChoose;
    private Uri imageUrl;
    private int requestCode;
    private Activity activity;
    private Fragment fragment;
    private boolean allowCrop;
    private boolean allowDelete;
    private List<String> fileUrls = new ArrayList();
    private boolean isFixedRatio;

    public ImagePicker(Activity activity, ImagePicker.OnFileChoose mOnFileChoose) {
        this.activity = activity;
        this.mOnFileChoose = mOnFileChoose;
    }

    public ImagePicker(Fragment fragment, ImagePicker.OnFileChoose mOnFileChoose) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.mOnFileChoose = mOnFileChoose;
    }

    public ImageCallBackManager getCallBackManager() {
        return (ImageCallBackManager) this;
    }

    public void requestImageGallery(int requestCode, boolean allowCrop, boolean isFixedRatio) {
        this.requestCode = requestCode;
        this.allowCrop = allowCrop;
        this.isFixedRatio = isFixedRatio;
        boolean hasStoragePermission = this.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        if (hasStoragePermission) {
            this.selectImageFromGallery();
        } else {
            this.requestPermissionForExternalStorage();
        }

    }

    public void selectImageFromGallery() {
        Intent pictureActionIntent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(pictureActionIntent, 11);
    }

    public void requestImageCamera(int requestCode, boolean allowCrop, boolean isFixedRatio) {
        this.requestCode = requestCode;
        this.allowCrop = allowCrop;
        this.isFixedRatio = isFixedRatio;
        boolean hasCameraPermission = this.checkPermission("android.permission.CAMERA");
        boolean hasStoragePermission = this.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        if (hasCameraPermission && hasStoragePermission) {
            this.selectImageFromCamera();
        } else if (!hasCameraPermission && !hasStoragePermission) {
            this.requestPermissionForCameraStorage();
        } else if (!hasCameraPermission) {
            this.requestPermissionForCamera();
        } else {
            this.requestPermissionForCameraButStorage();
        }

    }

    @SuppressLint("WrongConstant")
    public void selectImageFromCamera() {
        File photoFile = null;
        FileUri fileUri = null;
        if (this.activity != null) {
            fileUri = AppUtils.createImageFile(this.activity, "CAMERA");
        }

        if (fileUri != null) {
            photoFile = fileUri.getFile();
            this.imageUrl = fileUri.getImageUrl();
            if (photoFile != null) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra("output", this.imageUrl);
                intent.putExtra("android.intent.extra.screenOrientation", 1);
                intent.addFlags(1);
                intent.addFlags(2);
                this.startActivityForResult(intent, 10);
            }

        }
    }

    @TargetApi(27)
    private void requestPermissionForCamera() {
        String[] permissions = new String[]{"android.permission.CAMERA"};
        this.requestPermissionWithRationale(permissions, 115, "Camera");
    }

    @TargetApi(27)
    private void requestPermissionForCameraButStorage() {
        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
        this.requestPermissionWithRationale(permissions, 116, "Storage");
    }

    @TargetApi(27)
    private void requestPermissionForExternalStorage() {
        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
        this.requestPermissionWithRationale(permissions, 111, "Storage");
    }

    @TargetApi(27)
    private void requestPermissionForCameraStorage() {
        String[] permissions = new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE"};
        this.requestPermissionWithRationale(permissions, 112, "Camera & Storage");
    }

    private void startActivityForResult(Intent intent, int requestCode) {
        if (this.fragment != null) {
            this.fragment.startActivityForResult(intent, requestCode);
        } else if (this.activity != null) {
            this.activity.startActivityForResult(intent, requestCode);
        }

    }

    private boolean checkPermission(String permission) {
        return VERSION.SDK_INT < 23 || ActivityCompat.checkSelfPermission(this.activity, permission) == 0;
    }

    @RequiresApi(
            api = 23
    )
    private void requestPermissionWithRationale(final String[] permissions, final int requestCode, String rationaleDialogText) {
        boolean showRationale = false;
        String[] var5 = permissions;
        int var6 = permissions.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String permission = var5[var7];
            if (this.activity.shouldShowRequestPermissionRationale(permission)) {
                showRationale = true;
            }
        }

        if (showRationale) {
            AlertDialog.Builder builder = (new AlertDialog.Builder(this.activity)).setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ImagePicker.this.requestPermissions(permissions, requestCode);
                }
            }).setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setMessage("Allow " + this.activity.getString(string.app_name) + " to access " + rationaleDialogText + "?");
            builder.create().show();
        } else {
            this.requestPermissions(permissions, requestCode);
        }

    }

    @RequiresApi(
            api = 23
    )
    private void requestPermissions(String[] permissions, int requestCode) {
        if (this.fragment != null) {
            this.fragment.requestPermissions(permissions, requestCode);
        } else if (this.activity != null) {
            this.activity.requestPermissions(permissions, requestCode);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 111 && grantResults[0] == 0) {
            this.selectImageFromGallery();
        } else if (requestCode == 112 && grantResults[0] == 0 && grantResults[1] == 0) {
            this.selectImageFromCamera();
        } else if (requestCode == 115 && grantResults[0] == 0) {
            this.selectImageFromCamera();
        } else if (requestCode == 116 && grantResults[0] == 0) {
            this.selectImageFromCamera();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean hasStoragePermission = this.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE") && this.checkPermission("android.permission.READ_EXTERNAL_STORAGE");
        if (resultCode == -1 && hasStoragePermission) {
            switch(requestCode) {
                case 10:
                    Uri uri = this.imageUrl;
                    if (this.allowCrop) {
                        this.performCrop(uri);
                    } else {
                        this.performImageProcessing(uri.toString(), ImagePicker.FileType.IMG_FILE);
                    }
                    break;
                case 11:
                    if (this.allowCrop) {
                        this.performCrop(data.getData());
                    } else {
                        this.performImageProcessing(data.getData().toString(), ImagePicker.FileType.IMG_FILE);
                    }
                    break;
                case 203:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    this.performImageProcessing(resultUri.toString(), ImagePicker.FileType.IMG_FILE);
            }
        }

    }

    public void onDestroy() {
        this.activity = null;
        this.fragment = null;
        this.mOnFileChoose = null;
        if (this.fileUrls != null && !this.fileUrls.isEmpty() && this.allowDelete) {
            Iterator var1 = this.fileUrls.iterator();

            while(var1.hasNext()) {
                String fileUrl = (String)var1.next();
                File file = new File(fileUrl);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

    }

    public void onStartActivity() {
    }

    private void performImageProcessing(final String imageUrl, ImagePicker.FileType mFileType) {
        Observable.defer(new Func0<Observable<String>>() {
            public Observable<String> call() {
                return Observable.just(ImagePicker.this.compressImage(imageUrl));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            public void onCompleted() {
            }

            public void onError(Throwable e) {
            }

            public void onNext(String s) {
                ImagePicker.this.onFileChoose(s);
            }
        });
    }

    private String compressImage(String imageUri) {
        String filePath = this.getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 616.0F;
        float maxWidth = 816.0F;
        float imgRatio = (float)(actualWidth / actualHeight);
        float maxRatio = maxWidth / maxHeight;
        Log.d("IMAGE", "actualHeight=" + actualHeight + "actualWidth=" + actualWidth + "");
        if ((float)actualHeight > maxHeight || (float)actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / (float)actualHeight;
                actualWidth = (int)(imgRatio * (float)actualWidth);
                actualHeight = (int)maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / (float)actualWidth;
                actualHeight = (int)(imgRatio * (float)actualHeight);
                actualWidth = (int)maxWidth;
            } else {
                actualHeight = (int)maxHeight;
                actualWidth = (int)maxWidth;
            }
        }

        options.inSampleSize = this.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16384];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError var25) {
            var25.printStackTrace();
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError var24) {
            var24.printStackTrace();
        }

        float ratioX = (float)actualWidth / (float)options.outWidth;
        float ratioY = (float)actualHeight / (float)options.outHeight;
        float middleX = (float)actualWidth / 2.0F;
        float middleY = (float)actualHeight / 2.0F;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - (float)(bmp.getWidth() / 2), middleY - (float)(bmp.getHeight() / 2), new Paint(2));

        try {
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt("Orientation", 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90.0F);
            } else if (orientation == 3) {
                matrix.postRotate(180.0F);
            } else if (orientation == 8) {
                matrix.postRotate(270.0F);
            }

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException var23) {
            var23.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = AppUtils.createImageFile(this.activity, "").getFile().getAbsolutePath();

        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException var22) {
            var22.printStackTrace();
        }

        return filename;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = this.activity.getContentResolver().query(contentUri, (String[])null, (String)null, (String[])null, (String)null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            return index > 0 ? cursor.getString(index) : AppUtils.getWorkingDirectory(this.activity) + "/" + cursor.getString(0);
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round((float)height / (float)reqHeight);
            int widthRatio = Math.round((float)width / (float)reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        float totalPixels = (float)(width * height);

        for(float totalReqPixelsCap = (float)(reqWidth * reqHeight * 2); totalPixels / (float)(inSampleSize * inSampleSize) > totalReqPixelsCap; ++inSampleSize) {
            ;
        }

        return inSampleSize;
    }

    private void onFileChoose(String uri) {
        if (this.mOnFileChoose != null) {
            this.mOnFileChoose.onFileChoose(uri, this.requestCode);
        }

    }

    private void performCrop(Uri uri) {
        FileUri cropFile = AppUtils.createImageFile(this.activity, "CROP");
        if (this.fragment != null) {
            CropImage.activity(uri).setOutputUri(cropFile.getImageUrl()).setGuidelines(Guidelines.ON).setFixAspectRatio(true).start(this.activity, this.fragment);
        } else {
            CropImage.activity(uri).setOutputUri(cropFile.getImageUrl()).setGuidelines(Guidelines.ON).setFixAspectRatio(true).start(this.activity);
        }

    }

    public interface OnFileChoose {
        void onFileChoose(String var1, int var2);
    }

    private static enum FileType {
        IMG_FILE;

        private FileType() {
        }
    }
}

# Image-picker

## Pick image from Gallery and camera.

## Features  
Easy in Implementation. No run time permissions required. Image rotation handling using exif interface. Allow Cropping image. Compress image. No manually handling of image path.

## Installation  
Integrating the Published Library (Client Side)
Once successfully built on JitPack, anyone can use the library by adding it to their Android project:

### In the project-level `build.gradle`:

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### In the app-level `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.AaqibhafeezKhan:Image-picker:0.1.2'
}
```

## Usage  
Start by creating an instance of ImagePicker and ImageCallBackManager can be called in onCreate().
ImagePicker imagePicker = new ImagePicker(this, onFileChoose);; ImageCallBackManager callBackManager = imagePicker.getCallBackManager();

Callback listener
private ImagePicker.OnFileChoose onFileChoose = new ImagePicker.OnFileChoose() { @Override public void onFileChoose(String fileUri, int requestCode) { // here you will get captured or selected image... } };

Call below lines on onRequestPermissionsResult and onActivityResult
@Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { super.onRequestPermissionsResult(requestCode, permissions, grantResults); if (callBackManager != null) { callBackManager.onRequestPermissionsResult(requestCode, permissions, grantResults); } }

@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) { super.onActivityResult(requestCode, resultCode, data); if (callBackManager != null) { callBackManager.onActivityResult(requestCode, resultCode, data); } }

Open Camera picker.
imagePicker.requestImageCamera(CAMERA_PERMISSION, true, true); // pass false if you dont want to allow image crope

Open gallery picker.
imagePicker.requestImageGallery(STORAGE_PERMISSION_IMAGE, true, true);

Add below code to your manifest

@xml/provider_paths
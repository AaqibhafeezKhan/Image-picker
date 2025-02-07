# Image-picker

## Pick image from Gallery and camera.

## Features  
Easy in Implementation. No run time permissions required. Image rotation handling using exif interface. Allow Cropping image. Compress image. No manually handling of image path.

## Installation  
Add repository url and dependency in application module gradle file:

allprojects { repositories { ... maven { url 'https://jitpack.io' } } }

dependencies { implementation 'com.github.manasvii:image_picker_util:0.1.1' }

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
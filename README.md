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

### 1. Initialize ImagePicker
Create an instance of `ImagePicker` and `ImageCallBackManager` in your `onCreate()`.

```java
ImagePicker imagePicker = new ImagePicker(this, onFileChoose);
ImageCallBackManager callBackManager = imagePicker.getCallBackManager();
```

### 2. Implement Callback Listener
Define the callback to receive the captured or selected image path.

```java
private ImagePicker.OnFileChoose onFileChoose = new ImagePicker.OnFileChoose() { 
    @Override 
    public void onFileChoose(String fileUri, int requestCode) { 
        // Here you will get the path of the captured or selected image... 
    } 
};
```

### 3. Handle Permissions and Activity Results
Forward the results to the `callBackManager`.

```java
@Override 
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { 
    super.onRequestPermissionsResult(requestCode, permissions, grantResults); 
    if (callBackManager != null) { 
        callBackManager.onRequestPermissionsResult(requestCode, permissions, grantResults); 
    } 
}

@Override 
protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
    super.onActivityResult(requestCode, resultCode, data); 
    if (callBackManager != null) { 
        callBackManager.onActivityResult(requestCode, resultCode, data); 
    } 
}
```

### 4. Open Picker
You can open either the Camera or the Gallery.

**Open Camera Picker:**
```java
// Pass false as the second argument if you don't want to allow image cropping
imagePicker.requestImageCamera(CAMERA_PERMISSION, true, true); 
```

**Open Gallery Picker:**
```java
imagePicker.requestImageGallery(STORAGE_PERMISSION_IMAGE, true, true);
```

### 5. Update AndroidManifest.xml
Add the `FileProvider` to your manifest and include the following XML resource.

**Manifest Entry:**
```xml
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths" />
</provider>
```

**@xml/provider_paths:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="."/>
</paths>
```
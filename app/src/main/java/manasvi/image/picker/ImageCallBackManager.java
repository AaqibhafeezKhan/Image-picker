package manasvi.image.picker;

import android.content.Intent;
import android.support.annotation.NonNull;

public interface ImageCallBackManager {

    void onActivityResult(int var1, int var2, Intent var3);

    void onRequestPermissionsResult(int var1, @NonNull String[] var2, @NonNull int[] var3);

    void onDestroy();

    void onStartActivity();
}

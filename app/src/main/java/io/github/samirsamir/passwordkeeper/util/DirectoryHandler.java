package io.github.samirsamir.passwordkeeper.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;

public class DirectoryHandler {

    private final String TAG = "DirectoryHandler";

    public void create(Activity activity, String dir){

        String [] pastas = dir.split("/");
        StringBuilder builtDirectory = new StringBuilder("");

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            //create directory if not exist
            for (String pasta : pastas) {

                builtDirectory.append(pasta);
                boolean success;

                File newFolder = new File(Environment.getExternalStorageDirectory() + "/" + builtDirectory);
                if (!newFolder.exists()) {
                    success = newFolder.mkdir();
                    if (success) {
                        Log.i(TAG, "Path '" + builtDirectory + "' has been created.");
                    } else {
                        Log.w(TAG, "Path '" + builtDirectory + "' already exists.");
                    }
                }

                builtDirectory.append("/");
            }

        } else {
            // Request permission from the user
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }
}

package io.github.samirsamir.passwordkeeper.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.github.samirsamir.passwordkeeper.entity.Registration;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelFileHandler {

    private final String FILE_NAME = "TodoList.xls";
    private final String PATH_NAME = "excel";
    private final String SHEET_NAME = "Holy sheet";

    public void exportToExcel(Activity activity, List<Registration> registrations) {

        //Saving file in external storage
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/"+PATH_NAME);

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //create directory if not exist
            if(!directory.isDirectory()){
                directory.mkdir();
            }
        } else {
            // Request permission from the user
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        //file path
        File file = new File(directory, FILE_NAME);

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;
        Log.i("Path=================", file.getAbsolutePath());

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet(SHEET_NAME, 0);

            try {
                sheet.addCell(new Label(0, 0, "site")); // column and row
                sheet.addCell(new Label(1, 0, "login"));
                sheet.addCell(new Label(2, 0, "password"));

                for(int i=0; i < registrations.size(); i++){
                    Registration reg = registrations.get(i);

                    int row = i+1;
                    sheet.addCell(new Label(0, row, reg.getSite()));
                    sheet.addCell(new Label(1, row, reg.getLogin()));
                    sheet.addCell(new Label(2, row, reg.getPassword()));
                }

//                if (cursor.moveToFirst()) {
//                    do {
//                        String title = cursor.getString(cursor.getColumnIndex("site"));
//                        String desc = cursor.getString(cursor.getColumnIndex("login"));
//
//                        int i = cursor.getPosition() + 1;
//                        sheet.addCell(new Label(0, i, title));
//                        sheet.addCell(new Label(1, i, desc));
//                    } while (cursor.moveToNext());
//                }
//                //closing cursor
//                cursor.close();
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

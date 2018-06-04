package io.github.samirsamir.passwordkeeper.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.samirsamir.passwordkeeper.R;
import io.github.samirsamir.passwordkeeper.entity.Registration;
import io.github.samirsamir.passwordkeeper.entity.RegistrationType;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelFileHandler {

    private final String FILE_NAME = "file.xls";
    private final String PATH_NAME = "excel";
    private final String SHEET_NAME = "Holy sheet";

    public void exportToExcel(Activity activity, List<Registration> registrations) {

        //Saving file in external storage
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() +"/"+ activity.getString(R.string.app_name) + "/"+PATH_NAME);

        DirectoryHandler dirHandler = new DirectoryHandler();
        dirHandler.create(activity, activity.getString(R.string.app_name) + "/"+PATH_NAME);

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


    public List<Registration> importFile(Activity activity, File excelFile){

        ArrayList<Registration> registrations = new ArrayList<>();


        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            try {
                InputStream is= new FileInputStream(excelFile);

                Workbook wb = Workbook.getWorkbook(is);
                Sheet sheet = wb.getSheet(0);

                int row = sheet.getRows();

                for(int i=0; i < row; i++){

                    Registration reg = new Registration();
                    reg.setSite(sheet.getCell(0, i).getContents());
                    reg.setLogin(sheet.getCell(1, i).getContents());
                    reg.setPassword(sheet.getCell(2, i).getContents());
                    reg.setRegistrationType(RegistrationType.DEFAULT);
                    registrations.add(reg);
                }
            }

            catch (Exception e){
                e.printStackTrace();
            }

        }else {
            // Request permission from the user
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        return registrations;
    }

}

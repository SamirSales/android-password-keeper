package io.github.samirsamir.passwordkeeper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import io.github.samirsamir.passwordkeeper.R;
import io.github.samirsamir.passwordkeeper.adapter.RegistrationListAdapter;
import io.github.samirsamir.passwordkeeper.database.RegistrationDB;
import io.github.samirsamir.passwordkeeper.dialog.EditAccessDialog;
import io.github.samirsamir.passwordkeeper.dialog.PermissionDialog;
import io.github.samirsamir.passwordkeeper.dialog.RegistrationDialog;
import io.github.samirsamir.passwordkeeper.dialog.RegistrationEditorDialog;
import io.github.samirsamir.passwordkeeper.dialog.RegistrationOptionsDialog;
import io.github.samirsamir.passwordkeeper.entity.Registration;
import io.github.samirsamir.passwordkeeper.entity.RegistrationType;
import io.github.samirsamir.passwordkeeper.util.DirectoryHandler;
import io.github.samirsamir.passwordkeeper.util.ExcelFileHandler;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private TextView textEmptyList;
    private ListView listView;
    private RegistrationListAdapter registrationListAdapter;

    private EditAccessDialog editAccessDialog;

    private final int REQUEST_XLS_FILE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textEmptyList = findViewById(R.id.text_empty_list);
        listView = findViewById(R.id.list_view);

        setFloatingActionButton();
    }

    @Override
    protected void onResume() {
        super.onResume();

        RegistrationDB rDB = new RegistrationDB(this);
        List<Registration> registrations = rDB.getDefaultUsers();
        showTextEmptyList(registrations.size() == 0);
        registrationListAdapter = new RegistrationListAdapter(this, registrations);
        listView.setAdapter(registrationListAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    private void refreshList(){
        RegistrationDB rDB = new RegistrationDB(this);
        List<Registration> registrations = rDB.getDefaultUsers();
        showTextEmptyList(registrations.size() == 0);
        registrationListAdapter.reset(registrations);
    }

    private void showTextEmptyList(boolean show){
        textEmptyList.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Registration registration = (Registration) registrationListAdapter.getItem(position);
        new RegistrationDialog(this, registration).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Registration registration = (Registration) registrationListAdapter.getItem(position);

        new RegistrationOptionsDialog(this, registration, new RegistrationOptionsDialog.RegistrationOptions() {
            @Override
            public boolean onClickEditButton(Registration registration) {
                editRegistration(registration);
                return true;
            }

            @Override
            public boolean onClickRemoveButton(Registration registration) {
                deleteRegistrationAlertConfirm(registration);
                return true;
            }
        }).show();

        return true;
    }

    private void editRegistration(final Registration registration){

        RegistrationEditorDialog dialog = new RegistrationEditorDialog(MainActivity.this,
                 new RegistrationEditorDialog.RegistrationEditor() {
                    @Override
                    public boolean onClickSaveButton(String site, String login, String password) {

                        if(site.trim().isEmpty() || login.trim().isEmpty() || password.trim().isEmpty()){
                            Toast.makeText(MainActivity.this,
                                    R.string.empty_fields_warning,
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }else{
                            //verify if login is not duplicated
                            RegistrationDB rDB = new RegistrationDB(MainActivity.this);
                            List<Registration> duplicatedRegs = rDB.getUsersBySiteAndLogin(site, login);
                            Registration regSaved = duplicatedRegs.size() > 0 ? duplicatedRegs.get(0) : null;

                            if(regSaved == null || regSaved.getId() == registration.getId()){
                                registration.setSite(site);
                                registration.setLogin(login);
                                registration.setPassword(password);
                                rDB.update(registration);

                                Toast.makeText(MainActivity.this,
                                        R.string.successfully_saved,
                                        Toast.LENGTH_SHORT).show();
                                refreshList();
                                return true;
                            }else{
                                Toast.makeText(MainActivity.this,
                                        R.string.repeated_registration_warning,
                                        Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }
                    }
                });

        dialog.setTextTitle(getString(R.string.registration_editing));
        dialog.setFields(registration);
        dialog.show();
    }

    private void deleteRegistrationAlertConfirm(final Registration registration){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(R.string.remove_registration);
        alertDialogBuilder.setIcon(R.drawable.ic_delete_black_24dp);
        alertDialogBuilder.setMessage(getString(R.string.registration_remove_confirmation));

        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RegistrationDB rDB = new RegistrationDB(MainActivity.this);
                rDB.deleteRecord(registration.getId());
                refreshList();
                Toast.makeText(MainActivity.this,
                        R.string.successfully_removed, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_access) {
            editAccessPasswordDialog();
            return true;
        }

        if (id == R.id.action_export) {
            RegistrationDB rDB = new RegistrationDB(MainActivity.this);

            if(rDB.getAppUserAccess() != null){
                exportExcelFileDialogPermission();
            }else{
                exportExcelFile();
            }

            return true;
        }

        if (id == R.id.action_import) {
            importExcelFileDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_XLS_FILE && resultCode == RESULT_OK) {
            Uri selectedFile = data.getData(); //The uri with the location of the file

            DirectoryHandler directoryHandler = new DirectoryHandler();
            String path = directoryHandler.getPath(this, selectedFile);

            File file = new File(path);
            ExcelFileHandler efh = new ExcelFileHandler();
            List<Registration> registrations = efh.importFile(this, file);
            saveImportedDataAlertConfirm(path, registrations);
        }
    }

    private void saveImportedDataAlertConfirm(String path, final List<Registration> registrations){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(getString(R.string.action_import));
        alertDialogBuilder.setIcon(R.drawable.ic_file_download_black_24dp);
        alertDialogBuilder.setMessage(getString(R.string.import_file_confirm_dialog)+"\n\n["+path+"]");

        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveListOfRegistration(registrations);
                refreshList();
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void saveListOfRegistration(List<Registration> registrations){
        RegistrationDB rdb = new RegistrationDB(this);

        for(Registration reg : registrations){
            List<Registration> repeatedRegs = rdb.getUsersBySiteAndLogin(reg.getSite(), reg.getLogin());

            if(repeatedRegs.size() > 0){
                Registration repeated = repeatedRegs.get(0);
                repeated.setPassword(reg.getPassword());
                rdb.update(repeated);
            }else {
                rdb.add(reg);
            }
        }

        Toast.makeText(this,
                R.string.registration_import_finished, Toast.LENGTH_SHORT).show();
    }

    private void importExcelFileDialog(){
        Intent intent = new Intent()
                .setType("application/vnd.ms-excel").setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(
                Intent.createChooser(intent, getString(R.string.select_a_file)), REQUEST_XLS_FILE);
    }

    private void exportExcelFileDialogPermission(){

        new PermissionDialog(this, getString(R.string.export_xls_file), new PermissionDialog.OnPermissionAccess() {
            @Override
            public boolean onClickSaveButton(String password) {

                RegistrationDB rDB = new RegistrationDB(MainActivity.this);
                Registration regAccess = rDB.getAppUserAccess();

                if(regAccess.getPassword().equals(password)){
                    exportExcelFile();
                    return true;
                }

                Toast.makeText(MainActivity.this,
                        R.string.access_denied, Toast.LENGTH_SHORT).show();
                return false;
            }
        }).show();
    }

    private void exportExcelFile(){
        RegistrationDB rDB = new RegistrationDB(MainActivity.this);
        List<Registration> registrations = rDB.getDefaultUsers();
        ExcelFileHandler efh = new ExcelFileHandler();
        efh.exportToExcel(MainActivity.this, registrations);

        Toast.makeText(MainActivity.this,
                R.string.file_successfully_created, Toast.LENGTH_SHORT).show();
    }

    private void editAccessPasswordDialog() {
        final RegistrationDB rdb = new RegistrationDB(this);
        final Registration accessRegistration = rdb.getAppUserAccess();

        final boolean accessEditing = accessRegistration != null;

        editAccessDialog = new EditAccessDialog(this, accessEditing, new EditAccessDialog.OnEditAccess() {
            @Override
            public boolean onClickSaveButton(String newPassword, String oldPassword) {

                if(!accessEditing){
                    if(!newPassword.trim().isEmpty()){

                        // creating new access password
                        Registration registration = new Registration(
                                "","", newPassword, RegistrationType.APP_ACCESS);
                        alertEditAccessPasswordConfirmation(registration, false);
                        return false;
                    } else {
                        Toast.makeText(MainActivity.this,
                                R.string.insert_password_warning,
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {

                    if(!newPassword.trim().isEmpty()){

                        if(oldPassword.equals(accessRegistration.getPassword())){
                            // editing access password
                            Registration reg = accessRegistration.getCopy();
                            reg.setPassword(newPassword);
                            alertEditAccessPasswordConfirmation(reg, true);
                            return false;
                        }else{
                            Toast.makeText(MainActivity.this,
                                    R.string.incorrect_current_access_password,
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }

                    } else {
                        Toast.makeText(MainActivity.this,
                                R.string.insert_password_warning,
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        });

        editAccessDialog.show();
    }

    private void alertEditAccessPasswordConfirmation(final Registration registration, final boolean editing){
        final RegistrationDB rdb = new RegistrationDB(this);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(R.string.save_access_password_dialog_title);
        alertDialogBuilder.setIcon(R.drawable.ic_lock_black_24dp);
        alertDialogBuilder.setMessage(R.string.save_access_password_dialog_message);

        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(editing){
                    rdb.update(registration);
                }else{
                    rdb.add(registration);
                }

                Toast.makeText(MainActivity.this,
                        R.string.access_password_successfully_defined,
                        Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                editAccessDialog.dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
    }

    private void setFloatingActionButton(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RegistrationEditorDialog(MainActivity.this,
                    new RegistrationEditorDialog.RegistrationEditor() {
                        @Override
                        public boolean onClickSaveButton(String site, String login, String password) {

                            if(site.trim().isEmpty() || login.trim().isEmpty() || password.trim().isEmpty()){
                                Toast.makeText(MainActivity.this,
                                        R.string.empty_fields_warning, Toast.LENGTH_SHORT).show();
                                return false;
                            }else{
                                //verify if login is not duplicated
                                RegistrationDB rDB = new RegistrationDB(MainActivity.this);
                                List<Registration> regis = rDB.getUsersBySiteAndLogin(site, login);

                                if(regis.size() == 0){
                                    Registration reg = new Registration(site, login, password, RegistrationType.DEFAULT);
                                    rDB.add(reg);

                                    Toast.makeText(MainActivity.this,
                                            R.string.successfully_saved,
                                            Toast.LENGTH_SHORT).show();
                                    refreshList();
                                    return true;
                                }else{
                                    Toast.makeText(MainActivity.this,
                                            R.string.repeated_registration_warning,
                                            Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            }
                        }
                    }).show();
            }
        });
    }

}

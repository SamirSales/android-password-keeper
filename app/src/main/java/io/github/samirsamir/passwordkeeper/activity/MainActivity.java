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

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private TextView textEmptyList;
    private ListView listView;
    private RegistrationListAdapter registrationListAdapter;
    private EditAccessDialog editAccessDialog;

    private final int REQUEST_XLS_FILE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
    }

    private void setViews(){
        textEmptyList = findViewById(R.id.text_empty_list);
        listView = findViewById(R.id.list_view);
        setToolBar();
        setFloatingActionButton();
    }

    private void setToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListView();
    }

    private void setListView(){
        refreshEmptyListTextWarning();
        List<Registration> registrations = getDefaultUsers();
        registrationListAdapter = new RegistrationListAdapter(this, registrations);
        listView.setAdapter(registrationListAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Registration registration = (Registration) registrationListAdapter.getItem(position);
        new RegistrationDialog(this, registration).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Registration registration = (Registration) registrationListAdapter.getItem(position);
        showRegistrationOptionsDialog(registration);
        return true;
    }

    private void showRegistrationOptionsDialog(Registration registration){
        new RegistrationOptionsDialog(this, registration,
                new RegistrationOptionsDialog.RegistrationOptions() {
            @Override
            public boolean onClickEditButton(Registration registration) {
                showEditRegistrationDialog(registration);
                return true;
            }

            @Override
            public boolean onClickRemoveButton(Registration registration) {
                showDeleteRegistrationAlertConfirm(registration);
                return true;
            }
        }).show();
    }

    private void showEditRegistrationDialog(final Registration registration){
        RegistrationEditorDialog dialog = new RegistrationEditorDialog(
                this, registration, getRegistrationEditor());
        dialog.show();
    }

    private void showDeleteRegistrationAlertConfirm(final Registration registration){
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
                showToastShortlyByCodeString(R.string.successfully_removed);
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

        showToastShortlyByCodeString(R.string.registration_import_finished);
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

                showToastShortlyByCodeString(R.string.access_denied);
                return false;
            }
        }).show();
    }

    private void exportExcelFile(){
        RegistrationDB rDB = new RegistrationDB(MainActivity.this);
        List<Registration> registrations = rDB.getDefaultUsers();
        ExcelFileHandler efh = new ExcelFileHandler();
        efh.exportToExcel(MainActivity.this, registrations);
        showToastShortlyByCodeString(R.string.file_successfully_created);
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
                        showToastShortlyByCodeString(R.string.insert_password_warning);
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
                            showToastShortlyByCodeString(R.string.incorrect_current_access_password);
                            return false;
                        }

                    } else {
                        showToastShortlyByCodeString(R.string.insert_password_warning);
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

                showToastShortlyByCodeString(R.string.access_password_successfully_defined);
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
                showNewRegistrationDialog();
            }
        });
    }

    private void showNewRegistrationDialog(){
        RegistrationEditorDialog dialog = new RegistrationEditorDialog(
                this, getRegistrationEditor());
        dialog.show();
    }

    private RegistrationEditorDialog.RegistrationEditor getRegistrationEditor(){
        return new RegistrationEditorDialog.RegistrationEditor() {
            @Override
            public void onAfterSuccessSaving() {
                refreshList();
            }
        };
    }

    private void refreshList(){
        refreshEmptyListTextWarning();
        List<Registration> registrations = getDefaultUsers();
        registrationListAdapter.reset(registrations);
    }

    private void refreshEmptyListTextWarning(){
        List<Registration> registrations = getDefaultUsers();
        boolean hasNoUser = registrations.size() == 0;
        if(hasNoUser){
            textEmptyList.setVisibility(View.VISIBLE);
        }else{
            textEmptyList.setVisibility(View.INVISIBLE);
        }
    }

    private List<Registration> getDefaultUsers(){
        RegistrationDB registrationDB = new RegistrationDB(this);
        return registrationDB.getDefaultUsers();
    }

    private void showToastShortlyByCodeString(int codeString){
        Toast.makeText(this, codeString, Toast.LENGTH_SHORT).show();
    }
}

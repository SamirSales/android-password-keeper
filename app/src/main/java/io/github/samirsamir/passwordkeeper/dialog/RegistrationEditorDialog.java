package io.github.samirsamir.passwordkeeper.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.github.samirsamir.passwordkeeper.R;
import io.github.samirsamir.passwordkeeper.database.RegistrationDB;
import io.github.samirsamir.passwordkeeper.entity.Registration;

public class RegistrationEditorDialog extends Dialog {

    private EditText editSite, editLogin, editPassword;
    private RegistrationEditor registrationEditor;
    private Registration editRegistration;

    public RegistrationEditorDialog(Activity activity, RegistrationEditor registrationEditor) {
        this(activity, new Registration(), registrationEditor);
    }

    public RegistrationEditorDialog(Activity activity, Registration editRegistration,
                                    RegistrationEditor registrationEditor) {
        super(activity);
        this.editRegistration = editRegistration;
        this.registrationEditor = registrationEditor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_registration);
        setViews();
    }

    private void setViews(){
        editSite = findViewById(R.id.edit_site);
        editLogin = findViewById(R.id.edit_login);
        editPassword = findViewById(R.id.edit_password);

        setSaveButton();
        setTitleTextView();
        fillTextFieldsIfNecessary();
    }

    private void setSaveButton(){
        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave();
            }
        });
    }

    private void setTitleTextView(){
        TextView titleView = findViewById(R.id.dialog_title);

        String textTitle = "";

        if(!textTitle.isEmpty()){
            titleView.setText(textTitle);
        }else{
            titleView.setText(getContext().getText(R.string.new_registration));
        }
    }

    private void fillTextFieldsIfNecessary(){
        if(editRegistration != null){
            editSite.setText(editRegistration.getSite());
            editLogin.setText(editRegistration.getLogin());
            editPassword.setText(editRegistration.getPassword());
        }
    }

    private void onClickSave(){
        if(isAnyTextFieldNotFilled()){
            showToastShortlyByCodeString(R.string.empty_fields_warning);

        }else{
            if(isNotDuplicatedLogin()){
                saveRegistration();
                showToastShortlyByCodeString(R.string.successfully_saved);
                if(registrationEditor != null){
                    registrationEditor.onAfterSuccessSaving();
                }
                dismiss();
            }else{
                showToastShortlyByCodeString(R.string.repeated_registration_warning);
            }
        }
    }

    private boolean isAnyTextFieldNotFilled(){
        return getSite().trim().isEmpty()
            || getLogin().trim().isEmpty()
            || getPassword().trim().isEmpty();
    }

    private boolean isNotDuplicatedLogin(){
        Registration savedRegistration = getOneUserBySiteAndLogin(getSite(), getLogin());
        return savedRegistration == null || savedRegistration.getId() == editRegistration.getId();
    }

    private void saveRegistration(){
        if(isEditingRegistration()){
            updateRegistration();
        }else{
            createRegistration();
        }
    }

    private boolean isEditingRegistration(){
        return false;
    }

    private void updateRegistration(){
        setEditRegistrationByViews();
        RegistrationDB registrationDB = new RegistrationDB(getContext());
        registrationDB.update(editRegistration);
    }

    private void createRegistration(){
        setEditRegistrationByViews();
        RegistrationDB registrationDB = new RegistrationDB(getContext());
        registrationDB.add(editRegistration);
    }

    private void setEditRegistrationByViews(){
        editRegistration.setSite(getSite());
        editRegistration.setLogin(getLogin());
        editRegistration.setPassword(getPassword());
    }

    private String getSite(){
        return editSite.getText().toString();
    }

    private String getLogin(){
        return editLogin.getText().toString();
    }

    private String getPassword(){
        return editPassword.getText().toString();
    }

    private Registration getOneUserBySiteAndLogin(String site, String login){
        RegistrationDB rDB = new RegistrationDB(getContext());
        List<Registration> registrations = rDB.getUsersBySiteAndLogin(site, login);

        if(registrations.size() > 0){
            return registrations.get(0);
        }
        return null;
    }

    private void showToastShortlyByCodeString(int codeString){
        Toast.makeText(getContext(), codeString, Toast.LENGTH_SHORT).show();
    }

    public interface RegistrationEditor{
        void onAfterSuccessSaving();
    }
}
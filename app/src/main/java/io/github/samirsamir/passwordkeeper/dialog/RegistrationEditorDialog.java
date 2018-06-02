package io.github.samirsamir.passwordkeeper.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.github.samirsamir.passwordkeeper.R;
import io.github.samirsamir.passwordkeeper.entity.Registration;

public class RegistrationEditorDialog extends Dialog implements
        android.view.View.OnClickListener {

    private EditText editSite, editLogin, editPassword;

    private RegistrationEditor registrationEditor;

    private String textTitle = "";
    private Registration editRegistration = null;

    public RegistrationEditorDialog(Activity activity, RegistrationEditor registrationEditor) {
        super(activity);
        this.registrationEditor = registrationEditor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_registration);
        Button btnSave = findViewById(R.id.btn_save);
        editSite = findViewById(R.id.edit_site);
        editLogin = findViewById(R.id.edit_login);
        editPassword = findViewById(R.id.edit_password);
        btnSave.setOnClickListener(this);

        if(!textTitle.isEmpty()){
            TextView titleView = findViewById(R.id.text);
            titleView.setText(textTitle);
        }

        if(editRegistration != null){
            editSite.setText(editRegistration.getSite());
            editLogin.setText(editRegistration.getLogin());
            editPassword.setText(editRegistration.getPassword());
        }
    }

    public void setTextTitle(String textTitle){
        this.textTitle = textTitle;
    }

    public void setFields(Registration editRegistration) {
        this.editRegistration = editRegistration;
    }

    @Override
    public void onClick(View v) {

        if(registrationEditor != null
                && registrationEditor.onClickSaveButton(
                    editSite.getText().toString(),
                    editLogin.getText().toString(),
                    editPassword.getText().toString())){
            dismiss();
        }
    }

    public interface RegistrationEditor{
        // it must return true to close the dialog
        boolean onClickSaveButton(String site, String login, String password);
    }
}
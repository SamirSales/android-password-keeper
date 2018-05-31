package io.github.samirsamir.passwordkeeper.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import io.github.samirsamir.passwordkeeper.R;
import io.github.samirsamir.passwordkeeper.entity.Registration;

public class RegistrationOptionsDialog extends Dialog {

    private RegistrationOptions registrationEditor;
    private Registration registration;

    public RegistrationOptionsDialog(Activity activity, Registration registration,
                                     RegistrationOptions registrationEditor) {
        super(activity);
        this.registration = registration;
        this.registrationEditor = registrationEditor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_options_registration);

        Button btnEdit = findViewById(R.id.btn_edit);
        Button btnRemove = findViewById(R.id.btn_remove);

        TextView textSite = findViewById(R.id.text_site);
        TextView textLogin = findViewById(R.id.text_login);
        TextView textPassword = findViewById(R.id.text_password);

        textSite.setText(registration.getSite());
        textLogin.setText(registration.getLogin());
        textPassword.setText(registration.getPassword());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(registrationEditor != null
                        && registrationEditor.onClickEditButton(registration)){
                    dismiss();
                }
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(registrationEditor != null
                        && registrationEditor.onClickRemoveButton(registration)){
                    dismiss();
                }
            }
        });
    }

    public interface RegistrationOptions {
        // it must return true to close the dialog
        boolean onClickEditButton(Registration registration);
        boolean onClickRemoveButton(Registration registration);
    }
}
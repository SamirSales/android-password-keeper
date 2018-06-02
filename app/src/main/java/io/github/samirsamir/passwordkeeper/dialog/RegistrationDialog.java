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

public class RegistrationDialog extends Dialog implements
        android.view.View.OnClickListener {


    private Registration registration;

    public RegistrationDialog(Activity activity, Registration registration) {
        super(activity);
        this.registration = registration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_registration);
        Button buttonOk = findViewById(R.id.btn_ok);
        buttonOk.setOnClickListener(this);

        TextView textSite = findViewById(R.id.text_site);
        TextView textLogin = findViewById(R.id.text_login);
        TextView textPassword = findViewById(R.id.text_password);

        textSite.setText(registration.getSite());
        textLogin.setText(registration.getLogin());
        textPassword.setText(registration.getPassword());
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

}
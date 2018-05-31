package io.github.samirsamir.passwordkeeper.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import io.github.samirsamir.passwordkeeper.R;

public class RegistrationEditorDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Activity activity;
    private Dialog dialog;
    private Button btnSave;

    private RegistrationEditor registrationEditor;

    public RegistrationEditorDialog(Activity activity, RegistrationEditor registrationEditor) {
        super(activity);
        this.activity = activity;
        this.registrationEditor = registrationEditor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_registration);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(registrationEditor != null && registrationEditor.onClickSaveButton()){
            dismiss();
        }
    }

    public interface RegistrationEditor{
        // it must return true to close the dialog
        boolean onClickSaveButton();
    }
}
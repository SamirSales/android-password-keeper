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

public class PermissionDialog extends Dialog implements android.view.View.OnClickListener {

    private EditText passwordEdit;

    private OnPermissionAccess onPermissionAccess;
    private String title;

    public PermissionDialog(Activity activity, String title, OnPermissionAccess onPermissionAccess) {
        super(activity);
        this.title = title;
        this.onPermissionAccess = onPermissionAccess;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_permission);
        Button buttonOk = findViewById(R.id.btn_ok);
        buttonOk.setOnClickListener(this);

        TextView textTitle = findViewById(R.id.dialog_title);
        textTitle.setText(title);

        passwordEdit = findViewById(R.id.edit_password);
    }

    @Override
    public void onClick(View v) {

        if(onPermissionAccess != null && onPermissionAccess.onClickSaveButton(
                passwordEdit.getText().toString())){
            dismiss();
        }

    }

    public interface OnPermissionAccess {
        boolean onClickSaveButton(String password);
    }
}
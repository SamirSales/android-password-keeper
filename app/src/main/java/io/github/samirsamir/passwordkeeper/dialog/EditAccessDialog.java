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

public class EditAccessDialog extends Dialog implements android.view.View.OnClickListener {

    private EditText newPasswordEdit, oldPasswordEdit;

    private OnEditAccess onEditAccess;
    private boolean editing;

    public EditAccessDialog(Activity activity, boolean editing, OnEditAccess onEditAccess) {
        super(activity);
        this.onEditAccess = onEditAccess;
        this.editing = editing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_access);
        Button buttonOk = findViewById(R.id.btn_save);
        buttonOk.setOnClickListener(this);

        TextView textTitle = findViewById(R.id.dialog_title);
        textTitle.setText(R.string.edit_password_access);

        newPasswordEdit = findViewById(R.id.edit_password);
        oldPasswordEdit = findViewById(R.id.edit_password_old);

        oldPasswordEdit.setVisibility(editing ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        String oldPassword = editing ? oldPasswordEdit.getText().toString() : "";

        if(onEditAccess != null && onEditAccess.onClickSaveButton(
                newPasswordEdit.getText().toString(), oldPassword)){
            dismiss();
        }

    }

    public interface OnEditAccess{
        boolean onClickSaveButton(String newPassword, String oldPassword);
    }
}
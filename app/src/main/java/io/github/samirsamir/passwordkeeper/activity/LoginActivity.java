package io.github.samirsamir.passwordkeeper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.github.samirsamir.passwordkeeper.R;
import io.github.samirsamir.passwordkeeper.database.RegistrationDB;
import io.github.samirsamir.passwordkeeper.entity.Registration;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextPwd;

    private Registration appUserAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextPwd = findViewById(R.id.password_et);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RegistrationDB rDB = new RegistrationDB(this);
        appUserAccess = rDB.getAppUserAccess();

        if(appUserAccess == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitPassword(View view) {
        if(editTextPwd.getText().toString().equals(appUserAccess.getPassword())){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            editTextPwd.setText("");
            Toast.makeText(this, R.string.access_denied, Toast.LENGTH_SHORT).show();
        }
    }
}

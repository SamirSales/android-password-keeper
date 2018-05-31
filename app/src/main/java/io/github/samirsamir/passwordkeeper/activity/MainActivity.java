package io.github.samirsamir.passwordkeeper.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.github.samirsamir.passwordkeeper.R;
import io.github.samirsamir.passwordkeeper.RegistrationListAdapter;
import io.github.samirsamir.passwordkeeper.database.RegistrationDB;
import io.github.samirsamir.passwordkeeper.dialog.RegistrationEditorDialog;
import io.github.samirsamir.passwordkeeper.entity.Registration;
import io.github.samirsamir.passwordkeeper.entity.RegistrationType;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private TextView textEmptyList;
    private ListView listView;
    RegistrationListAdapter registrationListAdapter;

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
        Toast.makeText(this, registration.getPassword(), Toast.LENGTH_SHORT).show();
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
                                            Toast.LENGTH_LONG).show();
                                    refreshList();
                                    return true;
                                }else{
                                    Toast.makeText(MainActivity.this,
                                            R.string.repeated_registration_warning,
                                            Toast.LENGTH_LONG).show();
                                    return false;
                                }
                            }
                        }
                    }).show();
            }
        });
    }


}

package io.github.samirsamir.passwordkeeper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.samirsamir.passwordkeeper.R;
import io.github.samirsamir.passwordkeeper.entity.Registration;

public class RegistrationListAdapter extends BaseAdapter{

    private List<Registration> registrations;
    private LayoutInflater layoutInflater;

    public RegistrationListAdapter(Context context, List<Registration> registrations) {
        this.registrations = registrations;
        layoutInflater = LayoutInflater.from(context);
    }

    public void reset(List<Registration> registrations){
        this.registrations = registrations;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return registrations != null ? registrations.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return registrations != null ? registrations.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_list_resgistration, null);
        }

        Registration registration = registrations.get(position);

        TextView textSite = convertView.findViewById(R.id.text_site);
        TextView textLogin = convertView.findViewById(R.id.text_login);
        textSite.setText(registration.getSite());
        textLogin.setText(registration.getLogin());

        return convertView;
    }
}

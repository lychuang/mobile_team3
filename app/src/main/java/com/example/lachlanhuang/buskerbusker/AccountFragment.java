package com.example.lachlanhuang.buskerbusker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;



/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    Toolbar accountToolbar;


    public AccountFragment() {
        // Required empty public constructor
    }


// TODO: This needs to go into the settings menu! Make it after making account page!
//    @Override
//    public void onCreatePreferences(Bundle bundle, String s) {
//        // Load the Preferences from the XML file
//        addPreferencesFromResource(R.xml.app_preferences);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        accountToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//        if (accountToolbar != null) {
//            accountToolbar.setTitle(R.string.app_name);
//            setSupportActionBar(accountToolbar);
//        }
        accountToolbar.setTitle("User Profile");
        ((AppCompatActivity) getActivity()).setSupportActionBar(accountToolbar);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.settings) {
//            //Do your stuff here
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }



}

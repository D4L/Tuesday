package com.maxclique.tuesday.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        DisplayPostsFragment fragment = new DisplayPostsFragment();
        fragmentTransaction.add(R.id.main_content, fragment).commit();
    }
}

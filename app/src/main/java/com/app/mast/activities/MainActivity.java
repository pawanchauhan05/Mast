package com.app.mast.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.app.mast.R;
import com.app.mast.fragments.UserCheckFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Fragment> backStackEntries = new ArrayList<>();
    public static String USER = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListeners();

    }

    private void initListeners() {
        findViewById(R.id.back_image_view).setOnClickListener(this);
    }

    private void initViews() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new UserCheckFragment())
                .commit();
    }

    public void replaceFragment(int containerViewId, Fragment fragment, String addToBackStack) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerViewId, fragment);
            transaction.addToBackStack(addToBackStack);
            if (addToBackStack != null)
                backStackEntries.add(fragment);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        int size = backStackEntries.size();
        if (size > 0) {
            backStackEntries.remove(size - 1);
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_image_view:
                onBackPressed();
                break;
        }
    }

    public void setToolBarTitle(String title) {
        ((TextView)findViewById(R.id.title_text_view)).setText(title);
    }
}

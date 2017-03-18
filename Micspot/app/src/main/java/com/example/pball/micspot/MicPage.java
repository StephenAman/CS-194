package com.example.pball.micspot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MicPage extends AppCompatActivity {
    static public final String PREF_FILE = "SharedPrefs";
    static final int NUM_TABS = 2;
    protected MicSpotService.Mic mic;
    MicPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_page);

        //this is to set the status bar to the same color as our Micspot yellow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#FFC420"));
        }

        // Configure Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(getIntent().getStringExtra("micName"));
        toolbar.setNavigationIcon(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Configure ViewPager
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new MicPageAdapter(getSupportFragmentManager());

        // Add new SignUpFragment to adapter.
        adapter.addFragment(
                SignUpFragment.newInstance(getIntent().getStringExtra("micId")), "Signups"
        );
        // Add new ReviewFragment to adapter.
        adapter.addFragment(
                ReviewFragment.newInstance(getIntent().getStringExtra("micId")), "Reviews"
        );
        pager.setAdapter(adapter);

        // Check if we should show settings button. Note that this is just UX thing. The backend
        // bars unauthorized users from making changes anyway, so it's good enough on the frontend
        // to do a naive id equality check.
        if (getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("userId", "").equals(
           getIntent().getStringExtra("createdById")
        )) {
            ((ImageButton)findViewById(R.id.mic_settings)).setVisibility(View.VISIBLE);
        }

        // Configure TabLayout
        ((TabLayout) findViewById(R.id.tabs)).setupWithViewPager(pager);
    }

    public void openMicSettings(View view) {
        Intent intent = new Intent(this, MicSettings.class);
        if (mic != null) {
            intent.putExtra("MicSpotService.Mic", mic);
        }
        startActivityForResult(intent, 0);
    }

    public void setMic(MicSpotService.Mic mic) {
        this.mic = mic;
    }

    public class MicPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList = new ArrayList<Fragment>();
        private List<String> fragmentTitleList = new ArrayList<String>();

        public MicPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public Fragment getItem(int index) {
            return fragmentList.get(index);
        }

        @Override
        public CharSequence getPageTitle(int index) {
            return fragmentTitleList.get(index);
        }

        public void addFragment(Fragment fr, String title) {
            fragmentList.add(fr);
            fragmentTitleList.add(title);
        }
    }

    /**
     * Refresh signups list after user has edited settings.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            SignUpFragment fragment = (SignUpFragment) adapter.getItem(0);
            fragment.TryRefresh();
        }
    }
}

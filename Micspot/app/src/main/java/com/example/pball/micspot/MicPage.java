package com.example.pball.micspot;

import android.os.Bundle;
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
    static final int NUM_TABS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_page);

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
        MicPageAdapter adapter = new MicPageAdapter(getSupportFragmentManager());

        // Add new SignUpFragment to adapter.
        adapter.addFragment(
                SignUpFragment.newInstance(getIntent().getStringExtra("micId")), "Signups"
        );

        // TODO: Pass ReviewFragment to adapter here. For now, use an extra SignUpFragment instead.
        adapter.addFragment(
                ReviewFragment.newInstance(getIntent().getStringExtra("micId")), "Reviews"
        );
        pager.setAdapter(adapter);

        // Configure TabLayout
        ((TabLayout) findViewById(R.id.tabs)).setupWithViewPager(pager);
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
}

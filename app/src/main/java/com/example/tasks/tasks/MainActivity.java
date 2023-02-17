package com.example.tasks.tasks;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    if (DataKeeper.editId != -1) {
                        ((Toolbar) findViewById(R.id.toolbar)).setTitle("Редактирование");
                    } else {
                        ((Toolbar) findViewById(R.id.toolbar)).setTitle("Добавление");
                    }

                    return;
                }
                TextView title = (TextView) findViewById(R.id.editTitle);
                TextView date = (TextView) findViewById(R.id.editDate);
                TextView description = (TextView) findViewById(R.id.editDescription);
                RadioButton highPriority = (RadioButton) findViewById(R.id.radioPriorityHigh);
                RadioButton middlePriority = (RadioButton) findViewById(R.id.radioPriorityMiddle);
                RadioButton lowPriority = (RadioButton) findViewById(R.id.radioPriorityLow);
                Switch notify = (Switch) findViewById(R.id.switchNotify);
                title.setText("");
                date.setText("");
                description.setText("");
                highPriority.setChecked(false);
                middlePriority.setChecked(false);
                lowPriority.setChecked(true);
                notify.setChecked(false);
                ((Button) findViewById(R.id.buttonAddEdit)).setText("Добавить");
                DataKeeper.editId = -1;
                if (tab.getPosition() == 0) {
                    ((Toolbar) findViewById(R.id.toolbar)).setTitle("Задачи");
                }
                if (tab.getPosition() == 2) {
                    ((Toolbar) findViewById(R.id.toolbar)).setTitle("Настройки");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_show_open) {
            DataKeeper.state = DataKeeper.ST_OPEN;
            PlaceholderFragment.adapter.update();
            ((Toolbar) findViewById(R.id.toolbar)).setTitle("Задачи");
            TabLayout p = findViewById(R.id.tabs);
            TabLayout.Tab tab = p.getTabAt(0);
            if (!tab.isSelected()) {
                tab.select();
            }
            return true;
        }

        if (id == R.id.action_show_done) {
            DataKeeper.state = DataKeeper.ST_DONE;
            PlaceholderFragment.adapter.update();
            ((Toolbar) findViewById(R.id.toolbar)).setTitle("Сделанные");
            TabLayout p = findViewById(R.id.tabs);
            TabLayout.Tab tab = p.getTabAt(0);
            if (!tab.isSelected()) {
                tab.select();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        TabLayout p = findViewById(R.id.tabs);
        TabLayout.Tab tab = p.getTabAt(0);
        if (!tab.isSelected()) {
            tab.select();
        } else {
            super.onBackPressed();
        }
    }

}

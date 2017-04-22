package pt.ulisboa.tecnico.cmov.locmess.outbox;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.model.ListItem;
import pt.ulisboa.tecnico.cmov.locmess.model.Message;
import pt.ulisboa.tecnico.cmov.locmess.model.TestData;

public class OutboxActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Message> centralizedMessages;
    private List<Message> decentralizedMessages;

    public List<Message> getCentralizedMessages() {
        return centralizedMessages;
    }

    public void setCentralizedMessages(List<Message> centralizedMessages) {
        this.centralizedMessages = centralizedMessages;
    }

    public List<Message> getDecentralizedMessages() {
        return decentralizedMessages;
    }

    public void setDecentralizedMessages(List<Message> decentralizedMessages) {
        this.decentralizedMessages = decentralizedMessages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outbox);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Button addItem = (Button) findViewById(R.id.btn_add_item);
        addItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(OutboxActivity.this, PostMessageActivity.class);

                startActivity(i);

            }
        });
    }



    private void setupViewPager(ViewPager viewPager) {
        Bundle centralizedBundle = new Bundle();
        centralizedBundle.putBoolean("isCentralized", true);
        Fragment centralizedTab = new OutboxMessagesFragment();
        centralizedTab.setArguments(centralizedBundle);

        Bundle decentralizedBundle = new Bundle();
        decentralizedBundle.putBoolean("isCentralized", false);
        Fragment decentralizedTab = new OutboxMessagesFragment();
        decentralizedTab.setArguments(decentralizedBundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(centralizedTab, "Centralized");
        adapter.addFragment(decentralizedTab, "Decentralized");

        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

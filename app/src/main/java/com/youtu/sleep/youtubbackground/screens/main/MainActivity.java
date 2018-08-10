package com.youtu.sleep.youtubbackground.screens.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youtu.sleep.youtubbackground.R;
import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.repository.YoutubeVideoRepository;
import com.youtu.sleep.youtubbackground.screens.main.favourite.FavouriteFragment;
import com.youtu.sleep.youtubbackground.screens.main.home.HomeFragment;
import com.youtu.sleep.youtubbackground.screens.main.recent.RecentFragment;
import com.youtu.sleep.youtubbackground.widget.tablayout.IconTabLayoutCustom;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View,
        ViewPager.OnPageChangeListener {
    private IconTabLayoutCustom mTablayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private ViewPagerTabAdapter mTabAdapter;

    private TextView mTextViewTitle;
    private EditText mEditQuery;
    private ImageView mImageSearch;

    private MainPresenter mPresenter;

    private HomeFragment mHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mTextViewTitle.setText(mTabAdapter.getPageTitle(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * setupView
     */
    public void setupView() {
        mTextViewTitle = findViewById(R.id.text_title);
        mEditQuery = findViewById(R.id.edit_query_search);
        mImageSearch = findViewById(R.id.image_search);
        mImageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditQuery.getVisibility() != View.VISIBLE) {
                    mTextViewTitle.setVisibility(View.GONE);
                    mEditQuery.setVisibility(View.VISIBLE);
                    mEditQuery.setFocusable(true);
                } else if (mEditQuery.getText() != null) {
                    YoutubeVideoRepository repository = YoutubeVideoRepository.getInstance(getApplicationContext());
                    mPresenter = new MainPresenter(MainActivity.this, repository);
                    mPresenter.searchVideo(mEditQuery.getText().toString().trim());
                }
            }
        });
        setupViewPager();
        setupTabLayout();
    }

    /**
     * setup Tablayout
     */
    public void setupTabLayout() {
        mTablayout = (IconTabLayoutCustom) findViewById(R.id.tablayout);
        mTablayout.setupWithViewPager(mViewPager);
        mTablayout.setTabsFromPagerAdapter(mTabAdapter);
    }

    /**
     * setup tablayout + view pager
     */
    public void setupViewPager() {
        mViewPager = findViewById(R.id.viewpager);
        mHomeFragment = new HomeFragment();
        mFragments = new ArrayList<>();

        mFragments.add(mHomeFragment);
        mFragments.add(new RecentFragment());
        mFragments.add(new FavouriteFragment());
        mTabAdapter = new ViewPagerTabAdapter(getBaseContext(), getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void showResultSearch(List<Video> videos) {
        mHomeFragment.getAdapter().setData(videos);
    }

    @Override
    public void showFailedSearchMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

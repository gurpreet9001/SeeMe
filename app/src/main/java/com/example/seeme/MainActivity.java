package com.example.seeme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ViewPager mviewpager;
    private MainPagerAdapter mPa;
    private TabLayout mTabLayout;
    private FloatingActionButton fab;

    private DatabaseReference mUserref;

    public String MY_PREFS_NAME = "socio_prefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_appbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.app_name);



        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        mAuth = FirebaseAuth.getInstance();


        if(mAuth.getCurrentUser() != null)
            mUserref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(mAuth.getCurrentUser().getUid());
        mviewpager = (ViewPager) findViewById(R.id.tab_pager);
        mPa = new MainPagerAdapter(getSupportFragmentManager());

        mviewpager.setAdapter(mPa);

///SET FLOATING BUTTON AS ADD POST WHEN APP IS LAUNCHED

        mTabLayout.setupWithViewPager(mviewpager);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(intent);
            }
        });
        fab.show();



        mviewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        ///SET FLOATING BUTTON AS ADD POST WHEN TAB IS SCROLLED TO TIMELINE
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                                startActivity(intent);
                            }
                        });
                        fab.show();
                        break;
                    case 2:
                        ///SET FLOATING BUTTON AS SEARCH USER WHEN TAB IS SCROLLED TO FRIENDS
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               // Intent intent = new Intent(MainActivity.this, UsersActivity.class);
                               // startActivity(intent);
                            }
                        });
                        fab.show();
                        break;

                    default:
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

}

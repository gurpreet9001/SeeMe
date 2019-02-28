package com.example.seeme;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


class MainPagerAdapter extends FragmentPagerAdapter {


    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

//
//YET TO IMPLEMENT THESE THREE
//
        switch (position){
//            case 0: PostsFragment postsFragment = new PostsFragment();
//                return postsFragment;
//            case 1: ChatsFragment chatsFragment = new ChatsFragment();
//                return chatsFragment;
//            case 2: FriendsFragment friendsFragment = new FriendsFragment();
//                return friendsFragment;

            default: return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position){
            case 1: return "Chats";
            case 2: return "Friends";
            case 0: return "Timeline";
            default: return null;
        }

    }
}

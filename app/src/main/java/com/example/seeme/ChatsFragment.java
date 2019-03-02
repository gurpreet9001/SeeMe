package com.example.seeme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {


    private String Uid;
    private RecyclerView chats_rv;
    private DatabaseReference chatRef;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference messageRef;
    private LinearLayoutManager llm;
    private FirebaseAuth mAuth;
    public String MY_PREFS_NAME = "socio_prefs";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chats,container,false);

        chats_rv= (RecyclerView) v.findViewById(R.id.chats_rv);
        mAuth = FirebaseAuth.getInstance();


        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rootRef = FirebaseDatabase.getInstance().getReference();
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chat");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        messageRef = FirebaseDatabase.getInstance().getReference().child("messages").child(Uid);


        chatRef.child(Uid).keepSynced(true);
        userRef.keepSynced(true);
        messageRef.keepSynced(true);

        llm = new LinearLayoutManager(getActivity());
        chats_rv.setHasFixedSize(true);
        chats_rv.setLayoutManager(llm);

        return v;
    }

    //LASTEST ONLINE STATUS
    @Override
    public void onPause() {
        super.onPause();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        if (curr_user != null) {
            rootRef.child("Users").child(curr_user.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseUser curr_user = mAuth.getCurrentUser();
        if (curr_user != null) {
            rootRef.child("Users").child(curr_user.getUid()).child("online").setValue("true");
        }

        //MAKE AND SORT THE LIST OF CHATS IN ORDER OF TIME
        FirebaseRecyclerAdapter<Chats,ChatsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(
                Chats.class,
                R.layout.user_item,
                ChatsViewHolder.class,
                chatRef.child(Uid).orderByChild("timestamp")
        ) {
            @Override
            protected void populateViewHolder(final ChatsViewHolder viewHolder, Chats model, int position) {

                final String chat_user_id = getRef(position).getKey();

                //PRESS AND HOLD ON CHAT LIST VIEW ITEM OF ANY CHAT TO GET OPTION TO DELETE CHAT
                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setTitle("Clear Chat?").setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Map delchatmap = new HashMap();
                                delchatmap.put("Chat"+"/"+Uid+"/"+chat_user_id,null);
                                delchatmap.put("messages"+"/"+Uid+"/"+chat_user_id,null);

                                rootRef.updateChildren(delchatmap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(getContext(), "Chat Cleared!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                        return true;
                    }
                });

                //CHANGE THE VALUE OF ONLINE STATUS IN LIST IF USER IS ONLINE
                userRef.child(chat_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String username;
                        username = dataSnapshot.child("name").getValue().toString();
                        String thumbimage = dataSnapshot.child("thumb_image").getValue().toString();
                        if(dataSnapshot.hasChild("online")) {
                            String useronline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(useronline);
                        }
                        viewHolder.setName(username);
                        viewHolder.setDp(thumbimage,getContext());

                   //PRESS ON LIST ITEM TO CHAT WITH USER
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);
//                                chatIntent.putExtra("from_user_id",chat_user_id);
//                                chatIntent.putExtra("from_username",username);
//                                startActivity(chatIntent);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }
        };

        chats_rv.setAdapter(firebaseRecyclerAdapter);
        llm.smoothScrollToPosition(chats_rv,null,0);
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chats_rv.smoothScrollToPosition(positionStart+1);
                llm.setReverseLayout(true);
                llm.setStackFromEnd(true);
            }
        });
    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setLastmessage(String lastmessage){
            TextView user_message_tv = (TextView) mView.findViewById(R.id.user_item_status);
            user_message_tv.setText(lastmessage);
        }

        public void setName(String name){
            TextView friendtextview = (TextView) mView.findViewById(R.id.user_item_name);
            friendtextview.setText(name);
        }
        public void setDp(String thumb_image, Context ctx){
            CircleImageView userdp = (CircleImageView) mView.findViewById(R.id.user_item_dp);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.avatar).into(userdp);
        }

        public void setUserOnline(String online){
            ImageView onlineView = (ImageView)  mView.findViewById(R.id.user_item_online);
            if(online.equals("true")){
                onlineView.setVisibility(View.VISIBLE);
            }else{
                onlineView.setVisibility(View.INVISIBLE);
            }
        }


    }
}
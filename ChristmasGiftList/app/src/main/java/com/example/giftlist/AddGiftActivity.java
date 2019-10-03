package com.example.giftlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.giftlist.utils.Gift;
import com.example.giftlist.utils.GiftsAdapter;
import com.example.giftlist.utils.Person;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddGiftActivity extends AppCompatActivity {
    final String TAG = "demo";
    ListView listView;
    GiftsAdapter giftsAdapter;
    ArrayList<Gift> gifts = new ArrayList<>();
    Person person = new Person();
    int budgetRemaining;
    FirebaseDatabase database;
    DatabaseReference myRef, personRef, giftRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gift);
        setTitle(R.string.add_gift);

        if(getIntent()!=null){
            person = (Person) getIntent().getSerializableExtra("person");
            user = getIntent().getParcelableExtra("user");
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("gifts");
        personRef = database.getReference("Items/"+user.getUid());
        //giftRef = database.getReference("Items/"+user.getUid()+"/"+person.id+"/"+"gifts");

        budgetRemaining = person.totalBudget - person.totalBought;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gifts.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("gift", child.getValue().toString());
                    Gift value = child.getValue(Gift.class);
                    value.setId(child.getKey());
                    if(value.getPrice() <= budgetRemaining) {
                        gifts.add(value);
                    }
                }

                listView = findViewById(R.id.listview);
                giftsAdapter = new GiftsAdapter(getBaseContext(), R.layout.gift_item, gifts);
                listView.setAdapter(giftsAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Gift gf = (Gift) listView.getItemAtPosition(position);
                        person.totalBought = person.totalBought+ gf.getPrice();
                        person.giftCount = person.giftCount + 1;
                        person.gifts.add(gf);
                        personRef.child(person.id).setValue(person);
                        Intent intent = new Intent(AddGiftActivity.this, PersonGiftsActivity.class);
                        intent.putExtra("person", person);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PersonGiftsActivity.class);
        intent.putExtra("person", person);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}

package com.example.giftlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

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

public class PersonGiftsActivity extends AppCompatActivity {
    final String TAG = "demo";
    ListView listView;
    GiftsAdapter giftsAdapter;
    ArrayList<Gift> gifts = new ArrayList<>();
    Person person = new Person();
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_gifts);

        if(getIntent()!=null){
            person = (Person) getIntent().getSerializableExtra("person");
            user = getIntent().getParcelableExtra("user");
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Items/"+user.getUid()+"/"+person.id+"/"+"gifts");
        setTitle(person.name);
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gifts.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Gift value = child.getValue(Gift.class);
                    gifts.add(value);
                    Log.d("Demo", "Value is: " + value);
                }
                listView = findViewById(R.id.listview);
                giftsAdapter = new GiftsAdapter(getBaseContext(), R.layout.gift_item, gifts);
                listView.setAdapter(giftsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.person_gifts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_gift_menu_item:
                Log.d(TAG, "onOptionsItemSelected: ");
                if((person.totalBudget - person.totalBought)==0){
                    Toast.makeText(this, "The remaining Budget is $0", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(this, AddGiftActivity.class);
                    intent.putExtra("person", person);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

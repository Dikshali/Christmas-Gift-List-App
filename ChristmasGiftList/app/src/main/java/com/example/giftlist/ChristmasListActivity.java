package com.example.giftlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.giftlist.utils.Person;
import com.example.giftlist.utils.PersonsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChristmasListActivity extends AppCompatActivity {
    final String TAG = "demo";
    ListView listView;
    PersonsAdapter personsAdapter;
    ArrayList<Person> persons = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_christmas_list);
        setTitle(R.string.main_name);
        mAuth = FirebaseAuth.getInstance();
        if (getIntent() != null) {
            user = getIntent().getParcelableExtra("user");
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Items/"+user.getUid());
        Query query = myRef.orderByChild("name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                persons.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Person value = child.getValue(Person.class);
                    persons.add(value);
                    Log.d("Demo", "Value is: " + value);
                }

                listView = findViewById(R.id.listview);
                personsAdapter = new PersonsAdapter(getBaseContext(), R.layout.person_item, persons);
                listView.setAdapter(personsAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Intent intent = new Intent(ChristmasListActivity.this, PersonGiftsActivity.class);
                        Person p = (Person) listView.getItemAtPosition(position);
                        intent.putExtra("person", p);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.christmas_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_person_menu_item:
                Log.d(TAG, "onOptionsItemSelected: add person");
                Intent intent = new Intent(this, AddPersonActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                return true;
            case R.id.logout_menu_item:
                //logout
                // go to the login activity
                // finish this activity
                mAuth.signOut();
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                Log.d(TAG, "onOptionsItemSelected: logout");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package com.example.giftlist;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private Gift gf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_gifts);

        if(getIntent()!=null){
            person = (Person) getIntent().getSerializableExtra("person");
            user = getIntent().getParcelableExtra("user");
        }
        database = FirebaseDatabase.getInstance();
        listView = findViewById(R.id.listview);
        myRef = database.getReference("Items/"+user.getUid()+"/"+person.id+"/"+"gifts");
        //personRef = database.getReference("Items/"+user.getUid());
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

                giftsAdapter = new GiftsAdapter(getBaseContext(), R.layout.gift_item, gifts);
                listView.setAdapter(giftsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                gf = (Gift) listView.getItemAtPosition(i);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PersonGiftsActivity.this);
                alertDialog.setTitle("Delete Entry");
                alertDialog.setMessage("Are you sure you want to delete this entry?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(person.totalBought - gf.getPrice() < 0){
                            person.totalBought = 0;
                        }else {
                            person.totalBought = person.totalBought - gf.getPrice();
                        }
                        if(person.giftCount - 1 <0 ){
                            person.giftCount = 0;
                        }else {
                            person.giftCount = person.giftCount - 1;
                        }
                        for(int j=0;j<person.gifts.size();j++){
                            if(person.gifts.get(j).getId().equals(gf.getId())){
                                person.gifts.remove(j);
                            }
                        }
                        database.getReference("Items/"+user.getUid()).child(person.id).setValue(person);
                        giftsAdapter.notifyDataSetChanged();
                    }
                });
                alertDialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                                        dialog.cancel();
                                    }
                                });
                alertDialog.setCancelable(false);
                AlertDialog a = alertDialog.create();
                a.show();
                return false;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ChristmasListActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}

package com.example.giftlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.giftlist.utils.Person;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPersonActivity extends AppCompatActivity {
    final String TAG = "demo";
    EditText editTextBudget, editTextName;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        setTitle(R.string.add_person);
        editTextBudget = findViewById(R.id.editTextBudget);
        editTextName = findViewById(R.id.editTextName);
        if (getIntent() != null) {
            user = (FirebaseUser) getIntent().getParcelableExtra("user");
            Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Items/"+user.getUid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_person_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit_person_menu_item:
                String name = editTextName.getText().toString();
                String budgetString = editTextBudget.getText().toString();

                if(name == null || name.equals("")){
                    Toast.makeText(this, "Enter Name !!", Toast.LENGTH_SHORT).show();
                } else if(budgetString == null || budgetString.equals("")){
                    Toast.makeText(this, "Enter Budget !!", Toast.LENGTH_SHORT).show();
                } else{
                    try{
                        int budget = Integer.valueOf(budgetString);
                        if(budget > 0 ){
                            String id = myRef.push().getKey();
                            Person person = new Person(name, Integer.parseInt(budgetString), 0,0, id);
                            myRef.child(id).setValue(person);
                            Intent intent = new Intent(this, ChristmasListActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        } else{
                            Toast.makeText(this, "Enter Valid Budget !!", Toast.LENGTH_SHORT).show();
                            editTextBudget.setText("");
                        }
                    } catch (NumberFormatException ex){
                        Toast.makeText(this, "Enter Valid Budget !!", Toast.LENGTH_SHORT).show();
                    }

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

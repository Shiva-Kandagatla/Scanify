package com.majorproject.scanify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    TextView fullName,email,phone;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    private Button scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone = findViewById(R.id.profilePhone);
        fullName = findViewById(R.id.profileName);
        email    = findViewById(R.id.profileEmail);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                phone.setText(documentSnapshot.getString("phone"));
                fullName.setText(documentSnapshot.getString("fName"));
                email.setText(documentSnapshot.getString("email"));
            }
        });


        scan = (Button) findViewById(R.id.scan);
        final Activity activity = this;
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode  , resultCode,data);

        if(result!=null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled scanning", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, result.getContents() , Toast.LENGTH_SHORT).show();
            }

        }
        else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}

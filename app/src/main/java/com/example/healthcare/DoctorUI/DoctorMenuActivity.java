package com.example.healthcare.DoctorUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcare.MainActivity;
import com.example.healthcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorMenuActivity extends AppCompatActivity {
    TextView fullName, speciality;
    CircleImageView profilePicture;
    DatabaseReference databaseReference;
    FirebaseUser user;
    SharedPreferences sp;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_menu);
        user = FirebaseAuth.getInstance().getCurrentUser();
        sp = getSharedPreferences("login",MODE_PRIVATE);
        fullName = findViewById(R.id.fullName);
        speciality = findViewById(R.id.speciality);
        profilePicture = findViewById(R.id.profile_image);
        uid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fullName.setText(dataSnapshot.child(uid).child("fullName").getValue(String.class));
                speciality.setText(dataSnapshot.child(uid).child("speciality").getValue(String.class));
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference profileRef = storageReference.child("Profile pictures").child(FirebaseAuth.getInstance().getCurrentUser().getEmail() + ".jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePicture);
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

        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        dialog.setConfirmText("Yes");
        dialog.setCancelText("No");
        dialog.setContentText("Are you sure want to close HealthCare ?");
        dialog.setTitleText("Close application");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                finishAffinity();
                System.exit(0);
            }
        });
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
            }
        });
        dialog.show();
    }

    public void searchDoctor(View view) {
        Intent intent = new Intent(DoctorMenuActivity.this, SearchPatientActivity.class);
        startActivity(intent);
    }

    public void myPatients(View view) {
        Intent intent = new Intent(DoctorMenuActivity.this, MyPatientsActivity.class);
        startActivity(intent);
    }

    public void profileInfo(View view) {
        Intent intent = new Intent(DoctorMenuActivity.this, DisplayDoctorProfileInfo.class);
        startActivity(intent);
    }

    public void myAppointments(View view) {
        Intent intent = new Intent(DoctorMenuActivity.this, DoctorAppointments.class);
        startActivity(intent);
    }
    public void logOut(View view) {
        sp.edit().putBoolean("loggedDoctor",false).apply();
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(DoctorMenuActivity.this, MainActivity.class));

    }
}

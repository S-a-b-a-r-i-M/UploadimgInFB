package com.example.uploadimginfb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {
    private RecyclerView recycleView;
    private MyAdapter adapter;

    private DatabaseReference databaseReference;
    private List<Upload> uploadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        recycleView=findViewById(R.id.recyclview);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(this));

        uploadList=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("Uploads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap:snapshot.getChildren())
                {
                    //TRAVERSE ALL DATA FROM FB TO STORE IN OUR LIST
                    Upload upload=snap.getValue(Upload.class);
                    uploadList.add(upload);
                }
                adapter=new MyAdapter(ImagesActivity.this,uploadList);
                recycleView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error occured while fetching images from FB",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
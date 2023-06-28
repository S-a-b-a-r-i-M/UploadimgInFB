package com.example.uploadimginfb;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST=1;

    Button btn_chooseFile,btn_upload;
    EditText et_fileName;
    TextView tv_showUploads;
    ImageView image;
    ProgressBar progressBar;

    Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_chooseFile=findViewById(R.id.btn_chooseFile);
        btn_upload=findViewById(R.id.btn_upload);
        et_fileName=findViewById(R.id.et_fileName);
        tv_showUploads=findViewById(R.id.tv_showUploads);
        image=findViewById(R.id.image);
        progressBar=findViewById(R.id.progressbar);

        storageReference= FirebaseStorage.getInstance().getReference("Uploads");
        databaseReference= FirebaseDatabase.getInstance().getReference("Uploads");

        btn_chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launchSomeActivity.launch(intent);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IT PREVETNS MULTIPLE GLICKS ON THE UPLOAD BUTTON
                if(storageTask!=null && storageTask.isInProgress())
                    Toast.makeText(MainActivity.this,"Upload in progress",Toast.LENGTH_SHORT).show();
                else
                    uploadFile();
            }
        });

    }

    public void showUploads(View view)
    {
        if(view.getId()==R.id.tv_showUploads)
        {
            Intent intent=new Intent(this,ImagesActivity.class);
            startActivity(intent);
        }
    }

     
    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null
                            && data.getData() != null) {
                        imageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    imageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        image.setImageBitmap(
                                selectedImageBitmap);
                    }
                }
            });


    private String getFileExtension(Uri uri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void uploadFile()
    {
        if(imageUri != null)
        {
            StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."
                    +getFileExtension(imageUri));

            //UPLOADING IMAGE COMMAND FB CLOUD STORAGE
            storageTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            },500);


                            Toast.makeText(MainActivity.this,"Upload succefull",Toast.LENGTH_SHORT).show();


                            Upload upload=new Upload(et_fileName.getText().toString().trim(), taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            //UPLOADING IMAGE NAME AND URL INTO REAL TIME DATABASE IN FB
                            String id=databaseReference.push().getKey();
                            databaseReference.child(id).setValue(upload);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,"Its fsiled",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                             double progress=(100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                             progressBar.setProgress((int) progress);
                        }
                    });
        }
        else
        {
            Toast.makeText(this,"No file is selected",Toast.LENGTH_SHORT).show();
        }
    }

}
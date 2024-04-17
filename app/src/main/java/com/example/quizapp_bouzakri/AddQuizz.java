package com.example.quizapp_bouzakri;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.quizapp_bouzakri.Models.Quizz;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import javax.annotation.Nullable;

public class AddQuizz extends AppCompatActivity {

    EditText txtQuestion, txtRep1,txtRep2,txtBonneRep;
    Button btImport,btAdd;
    ImageView imageView;
    Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    ActivityResultLauncher<Intent> resultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quizz);

        txtQuestion = findViewById(R.id.txtQuestion);
        txtRep1 = findViewById(R.id.txtrep1);
        txtRep2 = findViewById(R.id.txtrep2);

        txtBonneRep = findViewById(R.id.txtbonnerep);
        btImport = findViewById(R.id.btnImportImage);
        btAdd = findViewById(R.id.btnAddQuestion);
        imageView = findViewById(R.id.imageView);
        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();



        btImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(txtQuestion.getText())|| TextUtils.isEmpty(txtRep1.getText())  || TextUtils.isEmpty(txtRep2.getText()) || TextUtils.isEmpty(txtBonneRep.getText()) ){
                    Toast.makeText(AddQuizz.this, "Veuillez remplir les champs", Toast.LENGTH_SHORT).show();
                }else if(imageUri == null){
                    Toast.makeText(AddQuizz.this, "Veuillez importer une image", Toast.LENGTH_SHORT).show();
                }else
                {

                    Quizz quizz = new Quizz();
                    quizz.setQuestion(txtQuestion.getText().toString());
                    quizz.getReponses().add(txtRep1.getText().toString());
                    quizz.getReponses().add(txtRep2.getText().toString());
                    if(quizz.getReponses().contains(txtBonneRep.getText().toString())){
                        quizz.setRepCorrect(txtBonneRep.getText().toString());
                        uploadImageToFireBaseStorage(imageUri,quizz);
                    }else{
                        Toast.makeText(AddQuizz.this, "La bonne réponse ne correspond à aucune des réponses saisit", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });



    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choisir une image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            }catch (Exception e){
                Log.e("AddQuizzActivity","Erreur lors de la récupération de l'image",e);

            }
        }
    }
    private void uploadImageToFireBaseStorage(Uri imageUri, Quizz quizz){
        StorageReference fileRef = storageReference.child("images/"+quizz.getId());
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        quizz.setImageUrl(uri.toString());
                        saveQuizzToFireStore(quizz);
                    });
                })
                .addOnFailureListener(e->{
                    Toast.makeText(this, "Echec du téléversement de l'image", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveQuizzToFireStore(Quizz quizz) {
        firestore.collection("quizzs")
                .add(quizz)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Quizz ajouté avec succès : "+quizz.getId(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->{
                            Toast.makeText(this, "Echec de l'ajout du quizz" , Toast.LENGTH_SHORT).show();
                        }

                );
    }
}

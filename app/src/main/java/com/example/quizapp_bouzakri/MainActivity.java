package com.example.quizapp_bouzakri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    // Déclaration des objets utilisés par le layout
    EditText etLogin;
    EditText etPassword;
    Button bLogin;
    TextView tvRegister;
    TextView bAdd;

    // Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation de Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Récupération des IDs des vues
        etLogin = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        bLogin = findViewById(R.id.bLogin);
        tvRegister = findViewById(R.id.tvRegister);
        bAdd = findViewById(R.id.bAdd);

        // Association des listeners
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddQuizz.class));
            }
        });
    }

    private void loginUser() {
        String email = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Vérification des champs
        if (email.isEmpty()) {
            etLogin.setError("Veuillez entrer votre e-mail");
            etLogin.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Veuillez entrer votre mot de passe");
            etPassword.requestFocus();
            return;
        }

        // Authentification avec Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login réussi, rediriger vers l'écran de quiz
                            startActivity(new Intent(MainActivity.this, Quiz1.class));
                        } else {
                            // Login échoué, afficher un message d'erreur
                            Toast.makeText(getApplicationContext(),
                                    "Echec de l'authentification, veuillez vérifier vos identifiants",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

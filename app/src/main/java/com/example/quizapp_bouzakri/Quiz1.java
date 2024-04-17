package com.example.quizapp_bouzakri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizapp_bouzakri.Models.Quizz;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Quiz1 extends AppCompatActivity {

    TextView txtQuestion;
    ImageView imgQuizz;
    RadioGroup rg;
    RadioButton rb;
    RadioButton rb1;
    RadioButton rb2;
    Button bNext;
    int score = 0;
    List<Quizz> quizzList = new ArrayList<>();
    int currentQuizIndex = 0;  // Index de la question actuelle
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz1);

        // Récupération des composants de la vue
        rg = findViewById(R.id.rg);
        bNext = findViewById(R.id.bNext);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        txtQuestion = findViewById(R.id.txtQuestion);
        imgQuizz = findViewById(R.id.ImgQz);

        String userEmail = getIntent().getStringExtra("userEmail");
        TextView userEmailTextView = findViewById(R.id.userEmailTextView);
        userEmailTextView.setText(userEmail);

        // Charger les questions
        loadQuizzes();

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Merci de choisir une réponse S.V.P !", Toast.LENGTH_SHORT).show();
                } else {
                    checkAnswer();
                    currentQuizIndex++;
                    if (currentQuizIndex < quizzList.size()) {
                        setQuestion(currentQuizIndex);  // Charger la prochaine question
                    } else {
                        finishQuiz();  // Finir le quiz
                    }
                }
            }
        });
    }

    private void loadQuizzes() {
        db.collection("quizzs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        Quizz quizz = doc.toObject(Quizz.class);
                        quizzList.add(quizz);
                    }
                    setQuestion(currentQuizIndex);  // Définir la première question
                } else {
                    Log.e("Quiz", "Erreur lors de la récupération des quizzs", task.getException());
                }
            }
        });
    }

    private void setQuestion(int index) {
        Quizz quizz = quizzList.get(index);
        txtQuestion.setText(quizz.getQuestion());
        rb1.setText(quizz.getReponses().get(0));
        rb2.setText(quizz.getReponses().get(1));
        Picasso.get().load(quizz.getImageUrl()).into(imgQuizz);
        rg.clearCheck();  // Effacer la sélection précédente
    }

    private void checkAnswer() {
        int selectedId = rg.getCheckedRadioButtonId();
        rb = findViewById(selectedId);
        if (rb.getText().toString().equals(quizzList.get(currentQuizIndex).getRepCorrect())) {
            score++;
        }
    }

    private void finishQuiz() {
        Intent resultIntent = new Intent(Quiz1.this, Score.class);
        resultIntent.putExtra("score", score);
        startActivity(resultIntent);
        finish();
    }
}

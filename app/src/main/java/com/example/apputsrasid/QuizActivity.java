package com.example.apputsrasid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion, tvScore, tvLives, tvTimer;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int lives = 3;
    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_IN_MILLIS = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initializeViews();
        loadQuestions();
        displayNextQuestion();
    }

    private void initializeViews() {
        tvQuestion = findViewById(R.id.tv_question);
        tvScore = findViewById(R.id.tv_score);
        tvLives = findViewById(R.id.tv_lives);
        tvTimer = findViewById(R.id.tv_timer);
        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);
        btnOption3 = findViewById(R.id.btn_option3);
        btnOption4 = findViewById(R.id.btn_option4);
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();
        questionList.add(new Question("Komponen Android yang merepresentasikan satu layar tunggal dengan antarmuka pengguna disebut...", new String[]{"Service", "Activity", "Intent", "Layout"}, 1));
        questionList.add(new Question("Layout modern yang paling direkomendasikan Google untuk membangun UI yang kompleks dan fleksibel adalah...", new String[]{"LinearLayout", "RelativeLayout", "FrameLayout", "ConstraintLayout"}, 3));
        questionList.add(new Question("Objek yang digunakan untuk meminta tindakan dari komponen aplikasi lain, seperti berpindah halaman, adalah...", new String[]{"Intent", "Fragment", "View", "Adapter"}, 0));
        questionList.add(new Question("Untuk menampilkan daftar data yang besar secara efisien, komponen manakah yang sebaiknya digunakan?", new String[]{"ListView", "ScrollView", "RecyclerView", "GridView"}, 2));
        questionList.add(new Question("Di file manakah kita mendaftarkan semua komponen aplikasi seperti Activity, Service, dan permission?", new String[]{"build.gradle", "themes", "strings", "AndroidManifest"}, 3));
        Collections.shuffle(questionList);
    }

    private void displayNextQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            resetTimer();
            Question currentQuestion = questionList.get(currentQuestionIndex);
            tvQuestion.setText(currentQuestion.getQuestionText());
            btnOption1.setText(currentQuestion.getOptions()[0]);
            btnOption2.setText(currentQuestion.getOptions()[1]);
            btnOption3.setText(currentQuestion.getOptions()[2]);
            btnOption4.setText(currentQuestion.getOptions()[3]);

            setupButtonListeners();
            updateStats();
        } else {
            gameOver("Selamat! Kamu telah menyelesaikan semua soal.");
        }
    }

    private void setupButtonListeners() {
        View.OnClickListener listener = v -> {
            Button clickedButton = (Button) v;
            int selectedOptionIndex = -1;
            int id = clickedButton.getId();
            if (id == R.id.btn_option1) selectedOptionIndex = 0;
            else if (id == R.id.btn_option2) selectedOptionIndex = 1;
            else if (id == R.id.btn_option3) selectedOptionIndex = 2;
            else if (id == R.id.btn_option4) selectedOptionIndex = 3;
            checkAnswer(selectedOptionIndex);
        };
        btnOption1.setOnClickListener(listener);
        btnOption2.setOnClickListener(listener);
        btnOption3.setOnClickListener(listener);
        btnOption4.setOnClickListener(listener);
    }

    private void checkAnswer(int selectedOptionIndex) {
        countDownTimer.cancel();
        Question currentQuestion = questionList.get(currentQuestionIndex);
        if (selectedOptionIndex == currentQuestion.getCorrectAnswerIndex()) {
            score += 10;
            Toast.makeText(this, "Jawaban Benar!", Toast.LENGTH_SHORT).show();
        } else {
            lives--;
            Toast.makeText(this, "Jawaban Salah!", Toast.LENGTH_SHORT).show();
        }
        currentQuestionIndex++;
        if (lives > 0) {
            displayNextQuestion();
        } else {
            gameOver("Nyawa Kamu Habis!");
        }
    }

    private void resetTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(COUNTDOWN_IN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.format(Locale.getDefault(), "Waktu: %d", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                lives--;
                Toast.makeText(QuizActivity.this, "Waktu Habis!", Toast.LENGTH_SHORT).show();
                currentQuestionIndex++;
                if (lives > 0) {
                    displayNextQuestion();
                } else {
                    gameOver("Nyawa Kamu Habis!");
                }
            }
        }.start();
    }

    private void updateStats() {
        tvScore.setText(String.format(Locale.getDefault(), "Skor: %d", score));
        tvLives.setText(String.format(Locale.getDefault(), "❤️ x %d", lives));
    }

    private void gameOver(String message) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        btnOption1.setEnabled(false);
        btnOption2.setEnabled(false);
        btnOption3.setEnabled(false);
        btnOption4.setEnabled(false);

        new AlertDialog.Builder(this)
                .setTitle("Kuis Selesai")
                .setMessage(message + "\n\nSkor Akhir Anda: " + score)
                .setPositiveButton("Main Lagi", (dialog, which) -> {
                    recreate();
                })
                .setNegativeButton("Keluar", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
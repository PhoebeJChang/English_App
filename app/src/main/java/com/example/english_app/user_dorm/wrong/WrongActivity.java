package com.example.english_app.user_dorm.wrong;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_app.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WrongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong);

        RecyclerView rcvWrong = findViewById(R.id.wrong_rcv);
        WrongRcvAdapter wrongRcvAdapter = new WrongRcvAdapter(getListWrong());
        rcvWrong.setAdapter(wrongRcvAdapter);
        rcvWrong.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ImageView backBtn = findViewById(R.id.wrongBackBtn);
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private ArrayList<WrongRcvModel> getListWrong() {
        ArrayList<WrongRcvModel> list = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("Position", MODE_PRIVATE);
        int position = sharedPreferences.getInt("position", 0);
        int title = sharedPreferences.getInt("title", 0);
        ExecutorService executor = Executors.newSingleThreadExecutor(); // 建立新的thread
        executor.execute(() -> {
            try {
                String s1 = "jdbc:jtds:sqlserver://myenglishserver.database.windows.net:1433/englishapp_db;user=englishapp@myenglishserver;password=English1234@@;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;ssl=request;"; //訪問azure的db的網址
                Connection connection = DriverManager.getConnection(s1); //建立連線
                SharedPreferences sharedPreferences1 = getSharedPreferences("User", MODE_PRIVATE);
                String phone = sharedPreferences1.getString("user_phone", "");
                System.out.println(position);
                System.out.println(title);
                String query = "";
                if (title == 0) {
                    switch (position) {
                        case 0:
                            query = "select Vocabulary, Chinese from wrong where Title = 'voc_elem' AND user_phone = ?";
                            break;
                        case 1:
                            query = "select Vocabulary, Chinese from wrong where Title = 'voc_jhs' AND user_phone = ?";
                            break;
                        case 2:
                            query = "select Vocabulary, Chinese from wrong where Title = 'voc_shs' AND user_phone = ?";
                            break;
                        case 3:
                            query = "select Vocabulary, Chinese from wrong where Title = 'voc_toeic' AND user_phone = ?";
                            break;
                        case 4:
                            query = "select Vocabulary, Chinese from wrong where Title = 'voc_toefl' AND user_phone = ?";
                            break;
                        case 5:
                            query = "select Vocabulary, Chinese from wrong where Title = 'voc_gsat' AND user_phone = ?";
                            break;
                        case 6:
                            query = "select Vocabulary, Chinese from wrong where Title = 'voc_ast' AND user_phone = ?";
                            break;
                    }
                } else {
                    switch (position) {
                        case 0:
                            query = "select Vocabulary, Chinese from wrong where Title = 'phrase_all' AND user_phone = ?";
                            break;
                        case 1:
                            query = "select Vocabulary, Chinese from wrong where Title = 'phrase_gsat' AND user_phone = ?";
                            break;
                        case 2:
                            query = "select Vocabulary, Chinese from wrong where Title = 'phrase_ast' AND user_phone = ?";
                            break;
                    }
                }

                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, phone);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    list.add(new WrongRcvModel(resultSet.getString(1), resultSet.getString(2)));
                }
                executor.shutdown();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        try {
            boolean e = executor.awaitTermination(20, TimeUnit.SECONDS); // await會有錯誤
            if (!e) {
                System.out.println("time out");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }
}
package com.example.english_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText user_name, user_phone, user_birthday, user_password, user_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user_name = findViewById(R.id.user_name);
        user_phone = findViewById(R.id.user_phone);
        user_birthday = findViewById(R.id.user_birthday);
        user_password = findViewById(R.id.user_password);
        user_check = findViewById(R.id.user_check);

        MaterialButton enrollBtn = findViewById(R.id.enrollBtn);
        enrollBtn.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try { //試跑try有問題就跑catch
                    boolean isCorrect = true;
                    String s1 = "jdbc:jtds:sqlserver://myenglishserver.database.windows.net:1433/englishapp_db;user=englishapp@myenglishserver;password=English1234@@;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;ssl=request;"; //訪問azure的db的網址
                    Connection connection = DriverManager.getConnection(s1); //建立連線

                    String nameText = Objects.requireNonNull((user_name).getText()).toString();
                    String namePattern = "^(?=.*[a-z]).{6,20}$";

                    String phoneText = Objects.requireNonNull((user_phone).getText()).toString();
                    String phonePattern = "^09\\d{8}$";

                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.TAIWAN);
                    String birthdayText = Objects.requireNonNull((user_birthday).getText()).toString();
                    String birthdayPattern = "^((0?[1-9]|1[012])(0?[1-9]|[12][0-9]|3[01])(19|20)?[0-9]{2})*$";

                    String passwordText = Objects.requireNonNull((user_password).getText()).toString();
                    String checkText = Objects.requireNonNull((user_check).getText()).toString();
                    String passwordPattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$";

                    String query = "select * from account where user_phone = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, phoneText);

                    if(nameText.isEmpty() ) {
                        runOnUiThread(() -> (user_name).setError("Field can't be empty"));
                        isCorrect = false;
                    }

                    if (!nameText.matches(namePattern)) {
                        runOnUiThread(() -> (user_name).setError("User name format error"));
                        isCorrect = false;
                    }

                    if(phoneText.isEmpty()) {
                        runOnUiThread(() -> (user_phone).setError("Field can't be empty"));
                        isCorrect = false;
                    }
                    if (!phoneText.matches(phonePattern)) {
                        runOnUiThread(() -> (user_phone).setError("User Phone format error"));
                        isCorrect = false;
                    }

                    if(birthdayText.isEmpty()) {
                        runOnUiThread(() -> (user_birthday).setError("Field can't be empty"));
                        isCorrect = false;
                    }
                    if(!birthdayText.matches(birthdayPattern)){
                        runOnUiThread(() -> (user_birthday).setError("User Birthday format error"));
                        isCorrect = false;
                    }

                    if (passwordText.isEmpty()) {
                        runOnUiThread(() -> (user_password).setError("Field can't be empty"));
                        isCorrect = false;
                    }
                    if (!passwordText.matches(passwordPattern)) {
                        runOnUiThread(() -> (user_password).setError("User Password format error"));
                        isCorrect = false;
                    }

                    if (checkText.isEmpty()) {
                        runOnUiThread(() -> (user_check).setError("Field can't be empty"));
                        isCorrect = false;
                    }
                    if (!checkText.matches(passwordPattern)) {
                        runOnUiThread(() -> (user_check).setError("User Password format error"));
                        isCorrect = false;
                    }

                    if(!passwordText.equals(checkText)){
                        runOnUiThread(() -> (user_check).setError("User Password not equal"));
                        isCorrect = false;
                    }

                    if (!isCorrect) {
                        return;
                    }

                    ResultSet resultSet = statement.executeQuery();
                    if(resultSet.next()){
                        runOnUiThread(() -> (user_check).setError("User Phone exist"));
                        isCorrect = false;
                    }

                    if (!isCorrect) {
                        return;
                    }

                    query = "select count(*) from account";
                    statement = connection.prepareStatement(query);
                    resultSet = statement.executeQuery();
                    resultSet.next();
                    int number = resultSet.getInt(1);
                    query = "insert into account values (?, ?, ?, ?, ?);";
                    statement = connection.prepareStatement(query);
                    statement.setInt(1, (((number + 1) * 19379 + 62327) % 89989) + 10000);
                    statement.setString(2, nameText);
                    statement.setString(3, phoneText);
                    statement.setDate(4, new java.sql.Date(Objects.requireNonNull(dateFormat.parse(birthdayText)).getTime()));
                    statement.setString(5, passwordText);
                    statement.execute();
                    System.out.println("Successful");
                    Intent mainIntent = new Intent(this, LoginActivity.class);
                    startActivity(mainIntent);
                    Looper.prepare();
                    Toast.makeText(this, "註冊成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                catch (SQLException e) {
                    System.out.println("sql failed");
                    System.out.println(e.getMessage());
                }
                catch (ParseException e){
                    System.out.println("parse failed");
                    System.out.println(e.getMessage());
                }
            });
        });
    }
}
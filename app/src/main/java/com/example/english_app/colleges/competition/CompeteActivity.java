package com.example.english_app.colleges.competition;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_app.R;
import com.example.english_app.colleges.competition.competitionInterface.CheckWhatComInterface;
import com.example.english_app.colleges.competition.competitionInterface.RecyclerComViewInterface;
import com.example.english_app.colleges.competition.competitionInterface.UpdateComRecyclerView;
import com.example.english_app.colleges.competition.quiz.ClozeQuizActivity;
import com.example.english_app.colleges.competition.quiz.MatchingTenQuizActivity;
import com.example.english_app.colleges.competition.quiz.MatchingTwelveQuizActivity;
import com.example.english_app.colleges.competition.quiz.PhraseQuizActivity;
import com.example.english_app.colleges.competition.quiz.VocabExamActivity;
import com.example.english_app.colleges.competition.quiz.VocabQuizActivity;

import java.util.ArrayList;

public class CompeteActivity extends AppCompatActivity implements UpdateComRecyclerView, RecyclerComViewInterface, CheckWhatComInterface {

    private RecyclerView rcvComGame;
    private ComDynamicRcvAdapter comDynamicRcvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compete);

        ArrayList<ComStaticRcvModel> catItem = new ArrayList<>();
        catItem.add(new ComStaticRcvModel(R.drawable.house_vocabulary, "單字測驗", "10關"));
        catItem.add(new ComStaticRcvModel(R.drawable.house_phrase, "片語測驗", "6關"));
        catItem.add(new ComStaticRcvModel(R.drawable.house_test, "歷屆測驗", "5關"));

        RecyclerView rcvComTitle = findViewById(R.id.com_rcv_cat);
        ComStaticRcvAdapter comStaticRcvAdapter = new ComStaticRcvAdapter(catItem, this, this, this);
        rcvComTitle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvComTitle.setAdapter(comStaticRcvAdapter);


        ArrayList<ComDynamicRcvModel> gameItem = new ArrayList<>();
        gameItem.add(new ComDynamicRcvModel("國小單字 - 中翻英", "#CEB443"));
        gameItem.add(new ComDynamicRcvModel("國小單字 - 英翻中", "#CEB443"));
        gameItem.add(new ComDynamicRcvModel("國中單字 - 中翻英", "#CEB443"));
        gameItem.add(new ComDynamicRcvModel("國中單字 - 英翻中", "#CEB443"));
        gameItem.add(new ComDynamicRcvModel("高中單字 - 中翻英", "#CEB443"));
        gameItem.add(new ComDynamicRcvModel("高中單字 - 英翻中", "#CEB443"));
        gameItem.add(new ComDynamicRcvModel("多益單字 - 中翻英", "#CEB443"));
        gameItem.add(new ComDynamicRcvModel("多益單字 - 英翻中", "#CEB443"));
        gameItem.add(new ComDynamicRcvModel("托福單字 - 中翻英", "#CEB443"));
        gameItem.add(new ComDynamicRcvModel("托福單字 - 英翻中", "#CEB443"));

        rcvComGame = findViewById(R.id.com_rcv_unit);
        comDynamicRcvAdapter = new ComDynamicRcvAdapter(gameItem, this);
        rcvComGame.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvComGame.setAdapter(comDynamicRcvAdapter);
    }

    @Override
    public void callback(int position, ArrayList<ComDynamicRcvModel> items) {
        comDynamicRcvAdapter = new ComDynamicRcvAdapter(items, this);
        comDynamicRcvAdapter.notifyDataSetChanged();
        rcvComGame.setAdapter(comDynamicRcvAdapter);
    }

    @Override
    public void onClicked(int position) {
        SharedPreferences sharedPreferences = getSharedPreferences("Position", MODE_PRIVATE);
        sharedPreferences.edit().putInt("title", position).apply();
    }

    @Override
    public void onItemClicked(int position) {
        SharedPreferences sharedPreferences = getSharedPreferences("Position", MODE_PRIVATE);
        sharedPreferences.edit().putInt("position", position).apply();
        int title = sharedPreferences.getInt("title", 0);
        if (title == 0) {//單字
            Intent intent;
            intent = new Intent(CompeteActivity.this, VocabQuizActivity.class);

            startActivity(intent);
        } else if (title == 1) {//片語
            Intent intent;
            intent = new Intent(CompeteActivity.this, PhraseQuizActivity.class);
            startActivity(intent);
        }else if (title == 2) {//歷屆
            if (position == 0 | position == 1){
                //學測 指考單字
                Intent intent;
                intent = new Intent(CompeteActivity.this, VocabExamActivity.class);
                startActivity(intent);
            }
            else if(position == 2){//cloze
                Intent intent;
                intent = new Intent(CompeteActivity.this, ClozeQuizActivity.class);
                startActivity(intent);
            }
            else if(position == 3){//matching 10學測
                Intent intent;
                intent = new Intent(CompeteActivity.this, MatchingTenQuizActivity.class);
                startActivity(intent);
            }
            else if(position == 4){//matching 12學測
                Intent intent;
                intent = new Intent(CompeteActivity.this, MatchingTwelveQuizActivity.class);
                startActivity(intent);
            }

        }


    }
}
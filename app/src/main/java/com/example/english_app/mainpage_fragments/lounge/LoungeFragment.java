package com.example.english_app.mainpage_fragments.lounge;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoungeFragment extends Fragment {
    private Socket clientSocket;
    private EditText EditTextMsg;

    public LoungeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 從資源檔裡取得位址後強制轉型成文字方塊

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lounge, container, false);

        FloatingActionButton loungeInfo = view.findViewById(R.id.lounge_info);
        loungeInfo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
            View view2 = LayoutInflater.from(getContext()).inflate(
                    R.layout.friend_hint_dialog, findViewById(R.id.layoutHintDialog)
            );
            builder.setView(view2);
            ((TextView) view2.findViewById(R.id.dialogTextTitle)).setText("注意事項!!!");
            ((TextView) view2.findViewById(R.id.dailogText)).setText(String.format("*此為交誼廳'留言板'\n*送出訊息後請重新進入此頁面，稍待更新，造成不便敬請見諒\n*此為公開留言板，會顯示ID以及帳號名稱喔!\n*若無法傳送訊息，請稍等片刻，伺服器關閉中"));
            ((Button) view2.findViewById(R.id.backComBtn)).setText("我了解了!");
            ((ImageView) view2.findViewById(R.id.megaPhoneImg)).setImageResource(R.drawable.ic_megaphone);

            final AlertDialog alertDialog = builder.create();
            builder.setCancelable(false);
            //取消
            view2.findViewById(R.id.backComBtn).setOnClickListener(v2 -> {
                alertDialog.cancel();
            });

            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            alertDialog.show();
        });

        EditTextMsg = view.findViewById(R.id.EditTextMsg);

        RecyclerView rcvMessage = view.findViewById(R.id.message_rcv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(false);
        rcvMessage.setLayoutManager(linearLayoutManager);
        MesRcvAdapter mesRcvAdapter = new MesRcvAdapter();
        mesRcvAdapter.setData(getListMessage());
        rcvMessage.setAdapter(mesRcvAdapter);

        ExecutorService executor1 = Executors.newSingleThreadExecutor(); // 建立新的thread
        executor1.execute(() -> {
            try {
                clientSocket = new Socket("20.243.200.205", 1098);
            } catch (IOException e) {
                e.printStackTrace();
                //getActivity().runOnUiThread(() -> TextView01.setText("伺服器暫時關閉，請稍後再試"));
            }
        });

        ExecutorService executor2 = Executors.newSingleThreadExecutor(); // 建立新的thread
        executor2.execute(() -> {
            try {
                while (clientSocket == null) {
                    TimeUnit.MILLISECONDS.sleep(300);
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while (clientSocket.isConnected()) {
                    String inputMsg = br.readLine();
                    if (inputMsg == null) {
                        clientSocket.close();
                        break;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        ImageButton sendBtn = view.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(v -> {
            ExecutorService executor3 = Executors.newSingleThreadExecutor(); // 建立新的thread
            executor3.execute(() -> {
                try {
                    while (clientSocket == null) {
                        TimeUnit.MILLISECONDS.sleep(300);
                    }
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    if (clientSocket.isConnected()) {
                        String msg = (EditTextMsg).getText().toString();
                        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("User", MODE_PRIVATE);
//                        if (msg.trim().equals("")) {
//                            return;
//                        }
                        String name = sharedPreferences.getString("user_name", "");
                        String phone = sharedPreferences.getString("user_phone", "");
                        pw.println(phone + name + " : " + msg);
                        pw.flush();
                        getActivity().runOnUiThread(() -> (EditTextMsg).getText().clear());
                    }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });

        return view;
    }

    private ViewGroup findViewById(int layoutHintDialog) {
        return null;
    }

    private ArrayList<MesRcvModel> getListMessage() {
        ArrayList<MesRcvModel> mesList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor(); // 建立新的thread
        executor.execute(() -> {
            try {
                String s1 = "jdbc:jtds:sqlserver://myenglishserver.database.windows.net:1433/englishapp_db;user=englishapp@myenglishserver;password=English1234@@;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;ssl=request;"; //訪問azure的db的網址
                Connection connection = DriverManager.getConnection(s1); //建立連線
                String query = "select account.user_name, account.user_id, message.msg, message.receive_time from message, account where message.user_phone = account.user_phone order by receive_time desc";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    mesList.add(new MesRcvModel(resultSet.getString(1), resultSet.getInt(2), resultSet.getString(3), resultSet.getTimestamp(4)));
                }
                executor.shutdown();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        try {
            boolean e = executor.awaitTermination(10, TimeUnit.SECONDS); // await會有錯誤
            if (!e) {
                System.out.println("time out");
            }
        } catch (InterruptedException i) {
            i.printStackTrace();
        }
        return mesList;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        clientSocket = new Socket();
    }



    private void showAlertDialog() {

    }
}
package com.example.smartmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartmanagement.Logic.AuthenticationLogic;
import com.example.smartmanagement.dto.MUserDto;
import com.example.smartmanagement.util.CommonConst;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class AuthenticationActivity extends AppCompatActivity {

    TextView txt01;
    TextView txt02;
    Button btn01;
    Button btn02;

    BluetoothAdapter bluetoothAdapter;
    BTClientThread btClientThread;

    // 遷移先
    String transition;
    // ロッカー番号
    String lockerNo;
    // NFCアダプター
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn01 = findViewById(R.id.btn01);
        btn02 = findViewById(R.id.btn02);
        txt01 = findViewById(R.id.txt01);
        txt02 = findViewById(R.id.txt02);

        // 「結果」テキストは非表示にする
        txt02.setVisibility(View.INVISIBLE);

        /**
         * 前画面よりデータ受け取り
         * TODO 渡してもらうデータは下記でよいか
         * 遷移先情報(貸出or返却),ロッカーマスタ.ロッカー番号
         */
        Intent intent = getIntent();
        // intentから遷移先情報取得とロッカー番号を取得
        transition = intent.getStringExtra("TRANSITION");
        lockerNo = intent.getStringExtra("LOCKER_NO");


        // nfcAdapter初期化
//        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        if (nfcAdapter == null) {
//            Log.e("NFC_ADAPTER","NFCが有効化されていません");
//            return;
//        }

        txt01.setText(R.string.user_info_text_before);

        //Reader Mode Offボタンを非活性にする
        btn02.setEnabled(false);

        // "Reader Mode ON"が押されたらDB接続
        btn01.setOnClickListener(v -> {

            //トグル機能
            btn01.setEnabled(false);
            btn02.setEnabled(true);

//            nfcAdapter.enableReaderMode(this, new CustomReaderCallback(), NfcAdapter.FLAG_READER_NFC_A, null);

            // NFCAdapterを経由せずに動確したい場合、下記２行のコメントアウトを外し、
            // NfcAdapter関連のソースをコメントアウトする
            DbConnect task = new DbConnect(AuthenticationActivity.this);
            task.execute();

        });

        // "Reader Mode OFF"が押されたら最初から実行
        btn02.setOnClickListener(v -> {

            //トグル機能
            btn01.setEnabled(true);
            btn02.setEnabled(false);

            //Readermode Off
            nfcAdapter.disableReaderMode(AuthenticationActivity.this);

            txt01.setText(R.string.user_info_text_before);
            txt02.setVisibility(View.INVISIBLE);

        });
    }

    private class CustomReaderCallback implements NfcAdapter.ReaderCallback {
        @Override
        public void onTagDiscovered(Tag tag) {
            byte[] rawid = tag.getId();
            final String idm = bytesToString(rawid);
            Log.d("TAG", "Tag ID: " + idm);
            runOnUiThread(() -> {
                DbConnect task = new DbConnect(AuthenticationActivity.this);
                //TODO 非同期処理に渡すものはカードリーダーから読み取ったユーザーIDと
                // 前画面から渡されたロッカー番号と遷移先情報(貸出/返却)が必要
                task.execute(idm);
            });
        }
    }

    private String bytesToString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte bt : bytes) {
            int i = 0xFF & (int)bt;
            String str = Integer.toHexString(i);
            sb.append(str);
        }
        return sb.toString();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();
        nfcAdapter.disableReaderMode(this);

    }


    /** インナークラス */

    /**
     * 非同期でDB接続
     *
     * 引数1 … Activityからスレッド処理へ渡したい変数の型
     *        ※ Activityから呼び出すexecute()の引数の型
     *        ※ doInBackground()の引数の型
     *
     * 引数2 … 進捗度合を表示する時に利用したい型
     *        ※ onProgressUpdate()の引数の型
     *
     * 引数3 … バックグラウンド処理完了時に受け取る型
     *        ※ doInBackground()の戻り値の型
     *        ※ onPostExecute()の引数の型
     *
     */
    class DbConnect extends AsyncTask<String, Void, Boolean> {

        Activity activity = null;

        public DbConnect(Activity act) {

            activity = act;
        }

        @Override
        protected Boolean doInBackground(String... idmString) {

            // 処理結果
            Boolean result = false;

            try {

                AuthenticationLogic authLogic = new AuthenticationLogic();
                // ユーザーマスタ情報を取得する
                List<MUserDto> muserList = authLogic.selectAllMUserData();

                // 動作検証では仮の値で対応
                String dummyUserIdAcquiredByCardReader = "104546";
                MUserDto matchedMUser = null;
                for (MUserDto target: muserList) {
                    // カードリーダーより読み取ったユーザー情報と一致するものがあるか比較する
                    if (target.getUserId().equals(dummyUserIdAcquiredByCardReader)) {
//                        if (target.getUserId().equals(idmString)) {
                        matchedMUser = target;
                        break;
                    }
                }

                if (matchedMUser == null) {
                    Log.e("NO_MATCH_DATA","一致するユーザー情報が見つかりませんでした");
                }

                // 貸出処理 or　返却処理(正常終了すればtrue)

                // Bluetoothの初期化
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if( bluetoothAdapter == null ){
                    Log.d("BLUETOOTH_CONNECTION", "This device doesn't support Bluetooth.");
                }
                // BTが設定で有効になっているかチェック
                if (!bluetoothAdapter.isEnabled()) {
                    Log.d("BLUETOOTH_CONNECTION", "This device is disabled Bluetooth.");
                }

                btClientThread = new BTClientThread();
                btClientThread.start();

                System.out.println("成功");

            } catch (ClassNotFoundException e) {
                Log.e("DBCONNECTION", "クラスが見つかりません", e);
                e.printStackTrace();
            } catch (SQLException e) {
                Log.e("DBCONNECTION", "SQLが不正です", e);
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                // 画面表示の制御（-ユーザー照会完了-）
                // ロッカー〇〇を開錠しました
                txt01.setText(R.string.user_info_text_after1);
                txt02.setText(R.string.user_info_text_after2);
                txt02.setVisibility(View.VISIBLE);

            } else {
                // 画面表示の制御（ユーザー情報が見つかりませんでした）
                txt01.setText(R.string.user_info_text_after_failure);

            }
        }
    }


    /**
     * Bluetooth接続時処理スレッド
     */
    public class BTClientThread extends Thread {

        InputStream inputStream;
        OutputStream outputStream;
        BluetoothSocket bluetoothSocket;

        public void run() {

            BluetoothDevice bluetoothDevice = null;
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            for(BluetoothDevice device : devices){
                if(device.getName().equals(CommonConst.BT_DEVICE)) {
                    bluetoothDevice = device;
                    break;
                }
            }

            if(bluetoothDevice == null){
                Log.d("BLUETOOTH_CONNECTION", "No device found.");
                return;
            }

            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(CommonConst.BT_UUID);

                while(true) {
                    if (Thread.interrupted()) {
                        break;
                    }

                    try {
                        // Bluetoothデバイス接続
                        bluetoothSocket.connect();

                        inputStream = bluetoothSocket.getInputStream();
                        outputStream = bluetoothSocket.getOutputStream();

                        while (true) {
                            if (Thread.interrupted()) {
                                break;
                            }

                            String str = null;
                            if (transition.equals("貸出")) {
                                str = "1";
                            } else if (transition.equals("返却")) {
                                str = "2";
                            }

                            String mergedStr = lockerNo + "," + str;

                            // Bluetoothモジュールへ「ロッカー番号」「貸出/返却」を送信する
                            outputStream.write(mergedStr.getBytes());

                            byte[] incomingBuff = new byte[64];

                            // 送信結果読み取り
                            int incomingBytes = inputStream.read(incomingBuff);
                            byte[] buff = new byte[incomingBytes];
                            System.arraycopy(incomingBuff, 0, buff, 0, incomingBytes);
                            String s = new String(buff, StandardCharsets.UTF_8);

                            Thread.sleep(3000);

                        }

                    } catch (IOException | InterruptedException e) {
                        Log.d("BLUETOOTH_CONNECTION", e.getMessage());
                    }
                    Thread.sleep(3000);
                }
            } catch (IOException | InterruptedException e) {
                Log.d("BLUETOOTH_CONNECTION", e.getMessage());
            }

            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.close();
                } catch (IOException e) {
                }
                    bluetoothSocket = null;
            }
        }
    }
}

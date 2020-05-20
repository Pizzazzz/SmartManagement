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
    // ユーザーID
    String userId;
    // 端末番号
    String deviceNo;

    // NFCアダプター
    NfcAdapter nfcAdapter;

    // 処理結果
    static Boolean finalResult = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 処理結果がfalseの場合、trueに初期化
        if (!finalResult) finalResult = true;

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
         * 遷移先情報(貸出or返却),
         * ロッカーマスタ.ロッカー番号
         */
        Intent intent = getIntent();
        // intentから遷移先情報,ロッカー番号,ユーザーID,端末番号を取得
        transition = intent.getStringExtra("TRANSITION");
        lockerNo = intent.getStringExtra("LOCKER_NO");
        userId = intent.getStringExtra("USER_ID");
        deviceNo = intent.getStringExtra("DEVICE_NO");

        // nfcAdapter初期化
//        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        if (nfcAdapter == null) {
//            Log.d"NFC_ADAPTER","NFCが有効化されていません");
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

            // NFCAdapterを経由せずにDB接続を動確したい場合、下記２行のコメントアウトを外し、
            // NFCAdapter関連のソースをコメントアウトする
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

    /**
     * バイトを文字列に変換
     * @param bytes
     * @return
     */
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

    //---------------------------------------------------------------------------------------
    /** 以下はインナークラス */

    /**
     * リーダーコールバック
     * NFCタグを検出すると
     * その情報を受け取る
     */
    private class CustomReaderCallback implements NfcAdapter.ReaderCallback {
        @Override
        public void onTagDiscovered(Tag tag) {
            byte[] rawid = tag.getId();
            final String idm = bytesToString(rawid);
            Log.d("TAG", "Tag ID: " + idm);
            runOnUiThread(() -> {
                DbConnect task = new DbConnect(AuthenticationActivity.this);
                task.execute(idm);
            });
        }
    }

    //---------------------------------------------------------------------------------------
    /**
     * 非同期でDB接続処理
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
                        // 実処理は下記のコメントアウトを外す
//                        if (target.getUserId().equals(idmString)) {
                        matchedMUser = target;
                        break;
                    }
                }

                if (matchedMUser == null) {
                    Log.d("NO_MATCH_DATA","一致するユーザー情報が見つかりませんでした");
                    finalResult = false;
                }

                /** --------- 貸出処理 or 返却処理 --------- */
                // Bluetoothの初期化
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if( bluetoothAdapter == null ){
                    Log.d("BLUETOOTH_CONNECTION", "This device doesn't support Bluetooth.");
                    finalResult = false;
                }
                // BTが設定で有効になっているかチェック
                if (!bluetoothAdapter.isEnabled()) {
                    Log.d("BLUETOOTH_CONNECTION", "This device is disabled Bluetooth.");
                    finalResult = false;
                }

                btClientThread = new BTClientThread();
                btClientThread.start();

                if (!finalResult) {
                    // 処理結果がfalseだった場合、後続処理は行わない
                    return finalResult;
                }

                // DB更新処理
                authLogic.execute(transition, lockerNo, deviceNo, userId, matchedMUser.getUserName());

            } catch (ClassNotFoundException e) {
                Log.e("DBCONNECTION", "クラスが見つかりません", e);
                finalResult = false;
            } catch (SQLException e) {
                Log.e("DBCONNECTION", "SQLが不正です", e);
                finalResult = false;
            }

            return finalResult;
        }

        /**
         * doInBackgroundの処理結果受け取り
         * 処理結果に応じて画面表示を制御する
         * @param result 処理結果
         */
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                // 画面表示の制御（-ユーザー照会完了-）
                // ロッカー〇〇を開錠しました
                txt01.setText(R.string.user_info_text_after1);
                txt02.setText(R.string.user_info_text_after2 + lockerNo + R.string.user_info_text_after3);
                txt02.setVisibility(View.VISIBLE);

            } else {
                // 画面表示の制御（ユーザー情報が見つかりませんでした）
                txt01.setText(R.string.user_info_text_after_failure);

            }
        }
    }

    //---------------------------------------------------------------------------------
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
                finalResult = false;
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
                        Log.e("BLUETOOTH_CONNECTION", e.getMessage(), e);
                        finalResult = false;
                    }
                    Thread.sleep(3000);
                }
            } catch (IOException | InterruptedException e) {
                Log.e("BLUETOOTH_CONNECTION", e.getMessage(), e);
                finalResult = false;
            }

            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.close();
                } catch (IOException e) {
                    Log.e("BLUETOOTH_CONNECTION", e.getMessage(), e);
                    finalResult = false;
                }
                    bluetoothSocket = null;
            }
        }
    }
}

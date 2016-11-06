package com.example.im_client;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	  private TextView tv_msg = null;
	  private EditText ed_msg = null;
      private Button btn_send = null;
	  // private Button btn_login = null;
	  private static final String HOST = "10.0.2.2";
	  private static final int PORT = 9999;
	  private Socket socket = null;
	  private BufferedReader in = null;
	  private PrintWriter out = null;
	  private String content = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //�����̷߳��͹�����Ϣ������TextView��ʾ
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_msg.setText(content);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tv_msg = (TextView) findViewById(R.id.TextView);
        ed_msg = (EditText) findViewById(R.id.EditText01);
        btn_send = (Button) findViewById(R.id.Button02);

        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())), true);
        } catch (IOException ex) {
            ex.printStackTrace();
            ShowDialog("login exception" + ex.getMessage());
        }
        btn_send.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String msg = ed_msg.getText().toString();
                if (socket.isConnected()) {
                    if (!socket.isOutputShutdown()) {
                        out.println(msg);
                    }
                }
            }
        });
        //�����̣߳����շ��������͹���������
        new Thread(SocketDemo.this).start();
    }
    /**
     * ������ӳ����쳣������AlertDialog��
     */
    public void ShowDialog(String msg) {
        new AlertDialog.Builder(this).setTitle("notification").setMessage(msg)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    /**
     * ��ȡ��������������Ϣ����ͨ��Handler����UI�߳�
     */
    public void run() {
        try {
            while (true) {
                if (!socket.isClosed()) {
                    if (socket.isConnected()) {
                        if (!socket.isInputShutdown()) {
                            if ((content = in.readLine()) != null) {
                                content += "\n";
                                mHandler.sendMessage(mHandler.obtainMessage());
                            } else {

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}

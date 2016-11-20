package com.example.im_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	  private EditText ed_msg = null;
	  private EditText port_id = null;
	  private EditText rec_msg = null;
      private Button btn_connect = null;
      private Button btn_send = null;
	  private static String HOST = null;
	  private static int PORT = 0;
	  private static int enable = 0;
	  private Socket socket = null;
	  private BufferedReader in = null;
	  private PrintWriter out = null;
	  private String content = "";
  
      public Handler mHandler = new Handler() {
          public void handleMessage(Message msg) {
              super.handleMessage(msg);
              if(enable == 1) {
                  btn_send.setEnabled(true);
                  btn_connect.setText("Connected");
                  btn_connect.setEnabled(false);
                  enable = 0;
              }
              rec_msg.setText(rec_msg.getText().toString()+"\n"+content);
          }
      };

      @Override
      public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ed_msg = (EditText) findViewById(R.id.editText1);
        port_id = (EditText) findViewById(R.id.editText2);
        rec_msg = (EditText) findViewById(R.id.editText3);
        btn_connect = (Button) findViewById(R.id.button1);
        btn_send = (Button) findViewById(R.id.button2);
        btn_send.setEnabled(false);
        rec_msg.setGravity(Gravity.TOP);
        rec_msg.setSingleLine(false);
        rec_msg.setMaxLines(5);
        
        btn_connect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
	              ClientConnect uConnect = new ClientConnect();
	              uConnect.start();
            }
        });
        
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
    }
    /**
     * notification AlertDialog£¡
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
     * read server message
     */
	public class ClientConnect extends Thread{
	    public void run() {
	        try {
            	  HOST = ed_msg.getText().toString();
              	  PORT = Integer.parseInt(port_id.getText().toString());
                  socket = new Socket(HOST, PORT);
                  in = new BufferedReader(new InputStreamReader(socket
                          .getInputStream()));
                  out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                          socket.getOutputStream())), true);
	              ClientListener uClient = new ClientListener();
	              uClient.start();
	              enable = 1;
	              mHandler.sendMessage(mHandler.obtainMessage());
	        } catch (IOException ex) {
                ex.printStackTrace();
                ShowDialog("login exception" + ex.getMessage());
            }
	    } 
    }
    /**
     * read server message
     */
	public class ClientListener extends Thread{
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
}

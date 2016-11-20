package com.example.im_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;

public class MainActivity extends Activity {
    private static final int PORT = 4000;
    private final String SERVER_TAG="IM_Server";
    private List<Socket> mList = new ArrayList<Socket>();
    private ServerSocket server = null;
    private ExecutorService mExecutorService = null; //thread pool
    private Button m_Button1 = null;
    private EditText m_EditText1 = null;
    private EditText m_EditText2 = null;
    private String ipAdress = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    m_Button1=(Button)findViewById(R.id.button1);
		m_EditText1=(EditText)findViewById(R.id.editText1);
		m_EditText2=(EditText)findViewById(R.id.editText2);
		m_EditText1.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		m_EditText2.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		m_EditText1.setGravity(Gravity.TOP);
		m_EditText2.setGravity(Gravity.TOP);
		m_EditText1.setSingleLine(false);
		m_EditText2.setSingleLine(false);
		m_EditText1.setMaxLines(5);
		m_EditText2.setMaxLines(5);
		m_Button1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
			    m_Button1.setText("Listening");
			    ipAdress = getLocalIpAddress();
			    //ipAdress = getWIFIIpAddress();
			    m_EditText1.setText("ServerIP:"+ipAdress);
			    ServerStart uServerThread = new ServerStart(); 
			    //time-costing API should not be put in the main thread
			    uServerThread.start();
			}
		});
		
	}    
	
	private String intToIp(int i) {  
		return ((i & 0xFF) + "." +  
		((i >> 8) & 0xFF) + "." +  
		((i >> 16) & 0xFF) + "." +  
		(i >> 24 & 0xFF));  
	}  
	
	public String getWIFIIpAddress(){
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE); 
		WifiInfo wifiInfo = wifiManager.getConnectionInfo(); 
		int ipAddress = wifiInfo.getIpAddress(); 
		Log.e("SERVER_TAG",Integer.toString(ipAddress));
		return intToIp(ipAddress);
	}
	
	public String getLocalIpAddress() { 
      try { 
   	    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
   	      NetworkInterface intf = en.nextElement(); 
   	      for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
   	        InetAddress inetAddress = enumIpAddr.nextElement(); 
   	        Log.e(SERVER_TAG, inetAddress.getHostAddress().toString());
   	        if (!inetAddress.isLoopbackAddress()&&(inetAddress instanceof Inet4Address)) { 
   	            return inetAddress.getHostAddress().toString(); 
   	        } 
   	      } 
   	    } 
	  } catch (SocketException ex) { 
		ex.printStackTrace();
	  } 
      return null; 
    } 
	
	private Handler mHandler = new Handler() {
		//Update UI in the UI created thread
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
					Bundle b = msg.getData(); 
					if(b.containsKey("clientIP")){
						String text = b.getString("clientIP");
						m_EditText1.setText(m_EditText1.getText().toString()+"\n"+text);
					} else if(b.containsKey("msgFromClient")){
						String text = b.getString("msgFromClient");
						m_EditText2.setText(m_EditText2.getText().toString()+"\n"+text);
					}
				
			} catch (Exception e) {
			  Log.e(SERVER_TAG,e.toString());
			}
			
		}
	};
	 
	public class ServerStart extends Thread{
		@Override  
		public void run() { 
	        try {
	            server = new ServerSocket(PORT);
	            mExecutorService = Executors.newCachedThreadPool();  //create a thread pool
	            Socket client = null;
	            while(true) {
	                client = server.accept();
	    			String msg = "";
	    			Message msg_UI = new Message(); 
	    			Bundle bundle_UI = new Bundle();
	                msg = client.getInetAddress().toString()+":"+client.getPort()+" comes";
	            
	                bundle_UI.putString("clientIP", msg);
	                msg_UI.setData(bundle_UI);
	                mHandler.sendMessage(msg_UI);
	                
	                mList.add(client);
	                mExecutorService.execute(new Service(client)); //start a new thread to handle the connection
	            }
	        }catch (Exception e) {
	            e.printStackTrace();
	        }	
		}
    }
    
    class Service implements Runnable {
            private Socket socket;
            private BufferedReader in = null;
            private String msg = null;
            
            public Service(Socket socket) {
                this.socket = socket;
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    msg = "[Server reply]:"+this.socket.getInetAddress() + " comes, total:" + mList.size();
                    this.sendmsg();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }

            @Override
            public void run() {
                try {
                    while(true) {
                    	//break when line followed by '\n', '\r', "\r\n"
                        if((msg = in.readLine()) != null) {
                            //close the connection
                            if(msg.equals("exit")) {
                            	//object synchronization
                            	synchronized(this) {
	                                System.out.println("disconnect");
	                                mList.remove(socket);
	                                in.close();
	                                msg = "[Server reply]:" + socket.getInetAddress()+":"+socket.getPort()
	                                    + " exit, remaining:" + mList.size();
	                                socket.close();
	                                this.sendmsg();
                            	}
                                break;
                            } else {
                                msg = socket.getInetAddress()+":"+socket.getPort()+ ":" + msg; 
                         		Message msg_UI = new Message(); 
                    			Bundle bundle_UI = new Bundle();
                                bundle_UI.putString("msgFromClient", msg);
                                msg_UI.setData(bundle_UI);
                                
                 	            mHandler.sendMessage(msg_UI);
                                this.sendmsg();
                            }
                      }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("out");
            }
          /**
           * Send msg to every client
           */
           public void sendmsg() {
               System.out.println(msg);
               int num =mList.size();
               for (int index = 0; index < num; index ++) {
                   Socket mSocket = mList.get(index);
                   PrintWriter pout = null;
                   try {
                       pout = new PrintWriter(new BufferedWriter(
                               new OutputStreamWriter(mSocket.getOutputStream())),true);
                       pout.println(msg);
                   }catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
        }    
}

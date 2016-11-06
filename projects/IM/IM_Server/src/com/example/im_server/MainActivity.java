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
import android.util.Log;
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
    private String ipAdress = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    m_Button1=(Button)findViewById(R.id.button1);
		m_EditText1=(EditText)findViewById(R.id.editText1);
		m_Button1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
			    m_Button1.setText("Listening");
			    //ipAdress = getLocalIpAddress();
			    //ipAdress = getWIFIIpAddress();
			    m_EditText1.setText("aaaa");
			    //ServerStart();
			}
		});
		
	}    
	
//	private String intToIp(int i) {  
//		return (i & 0xFF) + "." +  
//		((i >> 8) & 0xFF) + "." +  
//		((i >> 16) & 0xFF) + "." +  
//		(i >> 24 & 0xFF);  
//	}  
//	
//	public String getWIFIIpAddress(){
//		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE); 
//		WifiInfo wifiInfo = wifiManager.getConnectionInfo(); 
//		int ipAddress = wifiInfo.getIpAddress(); 
//		return intToIp(ipAddress);
//	}
	
//	public String getLocalIpAddress() { 
//      try { 
//   	    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
//   	      NetworkInterface intf = en.nextElement(); 
//   	      for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
//   	        InetAddress inetAddress = enumIpAddr.nextElement(); 
//   	        Log.e(SERVER_TAG, inetAddress.getHostAddress().toString());
//   	        if (!inetAddress.isLoopbackAddress()&&(inetAddress instanceof Inet4Address)) { 
//   	            return inetAddress.getHostAddress().toString(); 
//   	        } 
//   	      } 
//   	    } 
//	  } catch (SocketException ex) { 
//		ex.printStackTrace();
//	  } 
//      return null; 
//    } 
//	 
//    public void ServerStart() {
//        try {
//            server = new ServerSocket(PORT);
//            mExecutorService = Executors.newCachedThreadPool();  //create a thread pool
//            m_EditText1.setText("Server started");
//            Socket client = null;
//            while(true) {
//                client = server.accept();
//                //把客户端放入客户端集合中
//                mList.add(client);
//                mExecutorService.execute(new Service(client)); //start a new thread to handle the connection
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    
//    class Service implements Runnable {
//            private Socket socket;
//            private BufferedReader in = null;
//            private String msg = "";
//            
//            public Service(Socket socket) {
//                this.socket = socket;
//                try {
//                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    //客户端只要一连到服务器，便向客户端发送下面的信息。
//                    msg = "服务器地址：" +this.socket.getInetAddress() + "come toal:"
//                        +mList.size()+"（服务器发送）";
//                    this.sendmsg();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                
//            }
//
//            @Override
//            public void run() {
//                try {
//                    while(true) {
//                        if((msg = in.readLine())!= null) {
//                            //当客户端发送的信息为：exit时，关闭连接
//                            if(msg.equals("exit")) {
//                                System.out.println("ssssssss");
//                                mList.remove(socket);
//                                in.close();
//                                msg = "user:" + socket.getInetAddress()
//                                    + "exit total:" + mList.size();
//                                socket.close();
//                                this.sendmsg();
//                                break;
//                                //接收客户端发过来的信息msg，然后发送给客户端。
//                            } else {
//                                msg = socket.getInetAddress() + ":" + msg+"（服务器发送）";
//                                this.sendmsg();
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//          /**
//           * 循环遍历客户端集合，给每个客户端都发送信息。
//           */
//           public void sendmsg() {
//               System.out.println(msg);
//               int num =mList.size();
//               for (int index = 0; index < num; index ++) {
//                   Socket mSocket = mList.get(index);
//                   PrintWriter pout = null;
//                   try {
//                       pout = new PrintWriter(new BufferedWriter(
//                               new OutputStreamWriter(mSocket.getOutputStream())),true);
//                       pout.println(msg);
//                   }catch (IOException e) {
//                       e.printStackTrace();
//                   }
//               }
//           }
//        }    
}

package com.example.juanso.control_app;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanTask extends AsyncTask {

    private Context context;
    private static final int NB_THREADS = 256;
    private ArrayList reachableHosts = new ArrayList();
    private ArrayList hosts = new ArrayList();
    private int port;

    public ScanTask(Context context) {
        this.context = context;
    }

    public ArrayList getReachableHost(int port){
        this.port = port;
        try {
            this.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return hosts;
    }

    @Override
    protected Void doInBackground(Object[] params) {
        doScan();
        return null;
    }

    private void doScan(){
        WifiManager wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        int last_period = 0;
        for (int i = 0, n = ip.length(); i < n; i++) {
            char c = ip.charAt(i);
            if (c == '.') {
                last_period = i;
            }
        }
        ip = ip.substring(0, (last_period + 1));
        ExecutorService executor = Executors.newFixedThreadPool(NB_THREADS);
        for (int dest = 0; dest < 255; dest++) {
            String host = ip + dest;
            executor.execute(pingRunnable(host));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(60*1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        checkHosts();
        Log.d("todos los hosts!", reachableHosts.toString());
        Log.i("Ping", "Scan finished");
    }

    private Runnable pingRunnable(final String host) {
        return new Runnable() {
            public void run() {
                boolean reachable = isReachable(host, port,500);
                if (reachable){
                    Log.d("Ping", "=> Result: " + host + "reachable" );
                    reachableHosts.add(host);
                }
            }
        };
    }
    private void checkHosts(){
        if(reachableHosts.size() == 0){
            ((MainActivity)context).updateUrl(hosts);
            return;
        }
        String host = (String) reachableHosts.get(0);
        String url = "http://" + host + ":"+port+"/itson";
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                hosts.add(reachableHosts.get(0));
                Log.d("response", (String)reachableHosts.get(0));
                reachableHosts.remove(0);
                checkHosts();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", "error");
                reachableHosts.remove(0);
                checkHosts();
            }
        };
        StringRequest request = new StringRequest(Request.Method.POST,url,responseListener,errorListener);
        ConnectionManager.getInstance(context).add(request);
    }

    private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

}

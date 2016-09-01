package com.example.juanso.control_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import butterknife.ButterKnife;
import butterknife.Bind;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.play) Button playButton;
    @Bind(R.id.volUp) Button volUpButton;
    @Bind(R.id.volDown) Button volDownButton;
    @Bind(R.id.forward) Button forwardButton;
    @Bind(R.id.rewind) Button rewindButton;
    @Bind(R.id.platform) Button platformButton;
    @Bind(R.id.refresh) Button refreshButton;
    @Bind(R.id.platformTitle) TextView platformTitleTextView;

    int LONG_SLEEP = 500;
    int SHORT_SLEEP= 100;
    int port = 9000;

    Flag playPressed = new Flag(false);
    Flag volUpPressed = new Flag(false);
    Flag volDownPressed = new Flag(false);
    Flag forwardPressed = new Flag(false);
    Flag rewindPressed = new Flag(false);

    String platform = "Netflix";
    String host;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchHosts();
        ButterKnife.bind(this);
        platformTitleTextView.setText(platform);

        playButton.setOnTouchListener(getOnTouchListener(playPressed,"play", LONG_SLEEP));
        volUpButton.setOnTouchListener(getOnTouchListener(volUpPressed,"volUp",SHORT_SLEEP));
        volDownButton.setOnTouchListener(getOnTouchListener(volDownPressed,"volDown",SHORT_SLEEP));
        forwardButton.setOnTouchListener(getOnTouchListener(forwardPressed,"forward",SHORT_SLEEP));
        rewindButton.setOnTouchListener(getOnTouchListener(rewindPressed,"rewind",SHORT_SLEEP));

        platformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlataform();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                searchHosts();
            }
        });
    }

    private void makePost(final String action){
        String url = "http://"+host+ ":"+port+"/control";
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {}
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        };
        StringRequest request = new StringRequest(Request.Method.POST,url,responseListener,errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("platform", platform);
                params.put("action", action);
                return params;

            }
        };
        ConnectionManager.getInstance(this).add(request);
    }
    private void searchHosts(){
        new ScanTask(this).getReachableHost(port);
    }
    public void updateUrl(ArrayList<String> hosts){
        if(hosts.size() == 0){
            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this,"No hay hosts disponibles",Toast.LENGTH_LONG).show();
                }
            });

        }
        else if(hosts.size() == 1){
            host = hosts.get(0);
            Toast.makeText(this,"usando host: "+host,Toast.LENGTH_SHORT).show();
        }
        else{
            setHost(hosts);
            Log.d("update URL", "mas de un host disponible");
        }
    }

    private void setPlataform(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Elegir Plataforma");
        final CharSequence[] platforms = new CharSequence[]{"VLC","Popcorn Time","Youtube","Netflix"};
        builder.setItems(platforms, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                platform = platforms[i].toString();
                platformTitleTextView.setText(platform);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setHost(ArrayList<String> hosts){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Elegir Host");
        final CharSequence[] options = hosts.toArray(new CharSequence[hosts.size()]);
        builder.setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                host = options[i].toString();
                Toast.makeText(MainActivity.this,"usando host: "+host,Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    class Flag{
        private boolean flag;
        public Flag(boolean flag) {
            this.flag = flag;
        }
        public boolean getFlag() {
            return flag;
        }
        public void setFlag(boolean flag) {
            this.flag = flag;
        }
    }
    View.OnTouchListener getOnTouchListener(final Flag flag,final String action,final int sleep){
        class Task extends AsyncTask<Void, Void, Void> {
            private String action;
            public Task(String action) {
                this.action = action;
            }
            @Override
            protected Void doInBackground(Void... arg0) {
                while (flag.getFlag()) {
                    makePost(action);
                    SystemClock.sleep(sleep);
                }
                return null;
            }
        }
        View.OnTouchListener listener =  new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        flag.setFlag(true);
                        new Task(action).execute();
                        break;
                    case MotionEvent.ACTION_UP:
                        flag.setFlag(false);
                }
                return true;
            }
        };
        return listener;
    }
}

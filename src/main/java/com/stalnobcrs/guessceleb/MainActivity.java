package com.stalnobcrs.guessceleb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView img;
    Button a0,a1,a2,a3;
    ArrayList<String> celebImgUrl=new ArrayList<>();
    ArrayList<String> celebNames=new ArrayList<>();
    int choosenCeleb=0;
    int locationOfCorrectAnswer=0;
    String[] answers=new String[4];
    public class DownloadImg extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url;
            HttpURLConnection httpURLConnection;
            Bitmap bp=null;
            try {
                url=new URL(strings[0]);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                InputStream in=httpURLConnection.getInputStream();
                bp= BitmapFactory.decodeStream(in);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bp;
        }
    }
    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection httpURLConnection;
            String result="";
            try {
                url=new URL(strings[0]);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                InputStream  in=httpURLConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char cur=(char)data;
                    result+=cur;
                    data=reader.read();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    public void toast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
    public void checkAns(View view){
        if(view.getTag().toString().equals(locationOfCorrectAnswer)){
            toast("Correct");
        }
        else{
            toast("Wrong Correct ans is "+answers[locationOfCorrectAnswer]);

        }
        loadques();
    }
    public void inialize(){
        img=(ImageView)findViewById(R.id.image);
        a0=(Button)findViewById(R.id.button);
        a1=(Button)findViewById(R.id.button1);
        a2=(Button)findViewById(R.id.button2);
        a3=(Button)findViewById(R.id.button3);
    }
    public void setButtonText(){
        a0.setText(answers[0]);
        a1.setText(answers[1]);
        a2.setText(answers[2]);
        a3.setText(answers[3]);

    }
    public void loadques(){
        Random r=new Random();
        choosenCeleb=r.nextInt(celebImgUrl.size());
        DownloadImg imgTask=new DownloadImg();
        Bitmap celebImg= null;
        try {
            celebImg = imgTask.execute(celebImgUrl.get(choosenCeleb)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        img.setImageBitmap(celebImg);
        locationOfCorrectAnswer=r.nextInt(4);
        int wrongAns;
        for(int i=0;i<4;i++){
            if(i==locationOfCorrectAnswer){
                answers[i]=celebNames.get(choosenCeleb);
            }
            else{
                wrongAns=r.nextInt(celebImgUrl.size());
                while(wrongAns==locationOfCorrectAnswer){
                    wrongAns=r.nextInt(celebImgUrl.size());
                }
                answers[i]=celebNames.get(wrongAns);
            }
        }
        setButtonText();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inialize();
        DownloadTask task=new DownloadTask();
        try {
            String result=task.execute("http://www.posh24.se/kandisar").get();
            //Log.i("Web Content",result);
            String[] splitString=result.split("<div class=\"sidebarContainer\">");
            Pattern p=Pattern.compile("img src=\"(.*?)\"");
            Matcher m=p.matcher(splitString[0]);
            while(m.find()){
                //Log.i("celebImg",m.group(1));
                celebImgUrl.add(m.group(1));
            }
            p=Pattern.compile("alt=\"(.*?)\"");
            m=p.matcher(splitString[0]);
            while(m.find()){
                //Log.i("celebName",m.group(1));
                celebNames.add(m.group(1));
            }
           loadques();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}

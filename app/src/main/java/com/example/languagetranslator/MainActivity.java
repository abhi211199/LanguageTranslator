package com.example.languagetranslator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, AdapterView.OnItemSelectedListener {

    ProgressDialog progressDialog;
    TextView out;
    EditText inp;
    Spinner spinner;
    String[] langnames={"Translate in...","English","Bengali","Hindi","Tamil","Marathi","Urdu","French","Russian","Spanish","Chinese"};
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public String inpmsg;
    public TextToSpeech tts;
    String destlang,tolang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inp=(EditText)findViewById(R.id.inp);
        out=(TextView)findViewById(R.id.out);
        tts = new TextToSpeech(this, this);
        spinner=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,langnames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        destlang=langnames[position];
//        Toast.makeText(getApplicationContext(), langnames[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class translate extends AsyncTask<Void, Void, Void>  {
        String str;
        @Override
        protected Void doInBackground(Void... voids) {
            Translate translate = TranslateOptions.newBuilder().setApiKey("api_key").build().getService();
            Detection detection = translate.detect(inp.getText().toString());
            String detectedLanguage = detection.getLanguage();
            Translation translation =
                    translate.translate(
                            inp.getText().toString(),
                            Translate.TranslateOption.sourceLanguage(detectedLanguage),
                            Translate.TranslateOption.targetLanguage(tolang));
            str= translation.getTranslatedText();
//                Toast.makeText(MainActivity.this,txt,Toast.LENGTH_SHORT).show();
            return  null;
        }



        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            detectlang();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Translating! Hold on");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            out.setText(str);
            progressDialog.dismiss();
        }

    }

    public void askSpeechInput(View view) {
        inpmsg=null;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hello!\uD83D\uDE00 I am listening...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    // Receiving speech input

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    inpmsg=result.get(0);
                    String abc= inpmsg.toLowerCase();
                    inpmsg=abc;
                    inp.setText(inpmsg);
//                    Toast.makeText(getBaseContext(),inpmsg,Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();

    }

    public void onInit(int status) {
//            String str="Locale."+destlang;
        int result = 0;
        if (status == TextToSpeech.SUCCESS) {
            result = tts.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    public void speak(View view)
    {
        if(destlang.equals("English")||destlang.equals("French")){
        tts.speak(out.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);}
        else
        { tts.speak("Sorry!    I can't read out"+destlang, TextToSpeech.QUEUE_FLUSH, null);}
    }

    public void tr(View view)
    {
        if(destlang.equals("Translate in..."))
            Toast.makeText(MainActivity.this,"Please choose a language!",Toast.LENGTH_SHORT).show();
        else
        new translate().execute();
    }

    public void detectlang()
    {
        if(destlang.equals("English"))
           tolang="en";
        else if(destlang.equals("Bengali"))
            tolang="bn";
        else if(destlang.equals("Hindi"))
            tolang="hi";
        else if(destlang.equals("Tamil"))
            tolang="ta";
        else if(destlang.equals("Marathi"))
            tolang="mr";
        else if(destlang.equals("Urdu"))
            tolang="ur";
        else if(destlang.equals("French"))
            tolang="fr";
        else if(destlang.equals("Russian"))
            tolang="ru";
        else if(destlang.equals("Spanish"))
            tolang="es";
        else if(destlang.equals("Chinese"))
            tolang="zh";
    }

}

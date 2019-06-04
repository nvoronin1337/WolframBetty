package com.projects.nikita.wolframbetty.View;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.projects.nikita.wolframbetty.Model.FullResult;
import com.projects.nikita.wolframbetty.Model.FullResultsWrapper;
import com.projects.nikita.wolframbetty.Fetchers.FullResultsFetcher;
import com.projects.nikita.wolframbetty.Fetchers.ShortAnswerFetcher;
import com.projects.nikita.wolframbetty.Fetchers.QueryRecognizerFetcher;
import com.projects.nikita.wolframbetty.R;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;


public class MainBettyActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "BETTY";
    private static final String KEY = "LIST";
    private static final String QUERY = "query";
    private static final String ANSWER = "answer";
    private static final String QUERY_HINT = "Your query here...";
    private static final String ANSWER_HINT = "Your answer here...";
    private static final int TTS_REQUEST = 1337;

    private EditText queryET;
    private TextView answerTV;
    private Button btnDetails;
    private Boolean isAccepted;
    private CheckBox isSpeech;

    private String query;
    private String result;

    private FullResultsWrapper allResults;
    private TextToSpeech mTextToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_betty);


        queryET = findViewById(R.id.query);
        Button btnSubmit = findViewById(R.id.btn_submit);
        Button btnClear = findViewById(R.id.btn_clear);
        Button btnExit = findViewById(R.id.btn_exit);
        btnDetails = findViewById(R.id.btn_see_details);
        answerTV = findViewById(R.id.answer);
        isSpeech = findViewById(R.id.speakCheckBox);

        answerTV.setClickable(false);

        btnDetails.setEnabled(false);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryET.clearFocus();
                queryET.setEnabled(false);
                query = queryET.getText().toString();
                new FetchQueryRecognizerTask(getContext()).execute();

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = "";
                result = "";
                queryET.clearFocus();
                queryET.setText("");
                answerTV.setText("");
                isAccepted = false;
                btnDetails.setEnabled(false);

                queryET.setHint(QUERY_HINT);
                answerTV.setHint(ANSWER_HINT);
            }
        });

        /* Exit button to go back to the home screen */
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                answerTV.setText(R.string.computing_full);
                //new FetchFullAnswerTask(getContext(),query).execute();
                if(!(getContext()).isFinishing())
                {
                    answerTV.setText(result);
                    Intent myIntent = new Intent(getContext(), DetailsActivity.class);
                    myIntent.putExtra(QUERY, query);
                    Log.d(TAG, "about to start activity");
                    startActivity(myIntent);
                }
            }
        });

        if(savedInstanceState != null){
            queryET.setText(savedInstanceState.getString(QUERY));
            answerTV.setText(savedInstanceState.getString(ANSWER));
        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(QUERY, this.query);
        savedInstanceState.putString(ANSWER, this.result);
    }

    private MainBettyActivity getContext(){
        return this;
    }

    public void performSpeech(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, TTS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if((requestCode == TTS_REQUEST) && (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)){
            mTextToSpeech = new TextToSpeech(this, this);
        }else{
            Intent install = new Intent();
            install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(install);
        }
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int language = mTextToSpeech.setLanguage(Locale.US);
            if((language == TextToSpeech.LANG_MISSING_DATA) || (language == TextToSpeech.LANG_NOT_SUPPORTED)){
                Toast.makeText(this, "English language is not supported", Toast.LENGTH_SHORT).show();
            }else{
                int speechStatus = mTextToSpeech.speak(result, TextToSpeech.QUEUE_FLUSH, null);
                if(speechStatus == TextToSpeech.ERROR){
                    Toast.makeText(this, "Error playing sound", Toast.LENGTH_SHORT).show();
                }
            }

        }else{
            Toast.makeText(this, "Error reading the text", Toast.LENGTH_SHORT).show();
        }
    }


    /* ************************************************************************************************************ */
    /** Inner private static class extends AsyncTask to check if user entered query can be recognized by Wolfram Alpha.
     *
     *  It is important to note that class is static in order to prevent memory leaks.
     *  This leaks may occur in non-static class because Task will not be destroyed
     *  when the Activity is destroyed.
     *
     *  Context of the MainSubwayTrackerActivity is passed as the only argument in the constructor
     *  for this inner class. That is done in order to give this class access to the views of the
     *  Activity without any memory leaks.
     *  */
    private static class FetchQueryRecognizerTask extends AsyncTask<Void,Void,Boolean> {
        private WeakReference<MainBettyActivity> weakActivityRef;

        FetchQueryRecognizerTask(MainBettyActivity context){
            weakActivityRef = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MainBettyActivity context = weakActivityRef.get();
            return new QueryRecognizerFetcher().fetchRequest(context.queryET.getText().toString());
        }

        @Override
        protected void onPostExecute(Boolean accepted) {
            MainBettyActivity context = weakActivityRef.get();
            context.isAccepted = accepted;
            Log.d(context.TAG, "accepted: " + accepted);

            if(context.isAccepted){
                Log.d(context.TAG, "accepted");
                new FetchShortAnswerTask(context).execute();
            }else{
                Log.d(context.TAG, "Not accepted");
                Toast.makeText(context, "Query can't be recognized", Toast.LENGTH_SHORT).show();
            }
            context.queryET.setEnabled(true);
            Looper.loop();
            Looper.myLooper().quit();
        }
    }

    /* ************************************************************************************************************ */
    /** Inner private static class extends AsyncTask to get a SHORT ANSWER from Wolfram Alpha SHORT ANSWER API..
     *
     *  It is important to note that class is static in order to prevent memory leaks.
     *  This leaks may occur in non-static class because Task will not be destroyed
     *  when the Activity is destroyed.
     *
     *  Context of the MainSubwayTrackerActivity is passed as the only argument in the constructor
     *  for this inner class. That is done in order to give this class access to the views of the
     *  Activity without any memory leaks.
     *  */
    private static class FetchShortAnswerTask extends AsyncTask<Void,Void,String> {
        private WeakReference<MainBettyActivity> weakActivityRef;

        FetchShortAnswerTask(MainBettyActivity context){
            weakActivityRef = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Void... params) {
            MainBettyActivity context = weakActivityRef.get();
            return new ShortAnswerFetcher().fetchRequest(context.queryET.getText().toString());
        }

        @Override
        protected void onPreExecute(){
            MainBettyActivity context = weakActivityRef.get();
            context.answerTV.setText(R.string.computing);
        }

        @Override
        protected void onPostExecute(String answer) {
            MainBettyActivity context = weakActivityRef.get();

            Log.d(TAG, "answer: " + answer);

            if(answer.equals("Null")){
                Log.d(TAG, "answer is null");
                context.btnDetails.performClick();
            }else{
                Log.d(TAG, "answer is not null");
                context.result = answer;
                context.answerTV.setText(context.result);
                if(context.isSpeech.isChecked()){
                    Log.d(TAG, "checked");
                    context.performSpeech();
                }
            }
            context.queryET.setEnabled(true);
            context.btnDetails.setEnabled(true);
            Looper.loop();
            Looper.myLooper().quit();
        }
    }

    /* ************************************************************************************************************ */
    /** Inner private static class extends AsyncTask to get a FULL RESULT from Wolfram Alpha FULL RESULT API.
     *
     *  It is important to note that class is static in order to prevent memory leaks.
     *  This leaks may occur in non-static class because Task will not be destroyed
     *  when the Activity is destroyed.
     *
     *  Context of the MainSubwayTrackerActivity is passed as the only argument in the constructor
     *  for this inner class. That is done in order to give this class access to the views of the
     *  Activity without any memory leaks.
     *  */
    private static class FetchFullAnswerTask extends AsyncTask<Void, Void, ArrayList<FullResult>> {
        private WeakReference<MainBettyActivity> weakActivityRef;
        private String query;

        FetchFullAnswerTask(MainBettyActivity context, String strQuery){
            weakActivityRef = new WeakReference<>(context);
            query = strQuery;
        }

        @Override
        protected ArrayList<FullResult> doInBackground(Void... params) {
            return new FullResultsFetcher().fetchRequest(query);
        }

        @Override
        protected void onPostExecute(ArrayList<FullResult> results) {
            MainBettyActivity context = weakActivityRef.get();
            context.allResults = new FullResultsWrapper(results);

            Log.d(context.TAG, context.allResults.getResults().get(0).toString());
            Intent myIntent = new Intent(context, DisplayResultActivity.class);
            myIntent.putExtra(context.KEY, context.allResults);
            context.startActivity(myIntent);
            context.answerTV.setText(context.result);
            Looper.loop();
            Looper.myLooper().quit();
        }
    }
}

package com.voice.controller.client;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.voice.controller.adapter.MyCustomAdapter;

public class MyActivity extends Activity
{
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    public static TCPClient mTcpClient = null;
    private connectTask conctTask = null;
    private ImageButton mbtSpeak;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.ip_address)
        {
            Intent intent = new Intent(this, IPAddress.class);
            startActivity(intent);
        }
        else if (id == R.id.about)
        {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }
        else if (id == R.id.help)
        {
            Intent intent = new Intent(this, Help.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        arrayList = new ArrayList<String>();
        arrayList.add("Welcome to the Voice Controller App!\n\nSetup Steps:\n\n1. Start the VoiceController.jar program on your computer and press the start button. (See Google Play description to download the file.)\n\n2. On your phone, make sure that you are connected to the same Wifi as your computer and start the Voice Controller App.\n\n3. On the app screen, please click the icon that looks like a wifi signal and follow the on-screen instructions.\n\n4. If you reach this step then something went wrong and you should go back to step 1.");
        final EditText editText = (EditText) findViewById(R.id.editText);
        ImageButton send = (ImageButton) findViewById(R.id.send_button);

        // relate the listView from java to the one created in xml
        mList = findViewById(R.id.list);
        mbtSpeak = findViewById(R.id.btSpeak);
        checkVoiceRecognition();

        mAdapter = new MyCustomAdapter(this, arrayList);
        mList.setAdapter(mAdapter);

        mTcpClient = null;
        // connect to the server
        conctTask = new connectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String message = editText.getText().toString();
                // add the text in the arrayList
                arrayList.add("You: " + message);
                // sends the message to the server
                if (mTcpClient != null)
                {
                    mTcpClient.sendMessage("Android Client: " + message);
                }
                // refresh the list
                mAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        });
    }

    public void checkVoiceRecognition()
    {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            mbtSpeak.setEnabled(false);
            Toast.makeText(this, "Voice recognizer not present", Toast.LENGTH_SHORT).show();
        }
    }

    public void speak(View view)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            // If Voice recognition is successful then it returns RESULT_OK
            if (resultCode == RESULT_OK)
            {

                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty())
                {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    // if (textMatchList.get(0).contains("search")) {

                    String searchQuery = textMatchList.get(0);
                    // searchQuery = searchQuery.replace("search", "");
                    // Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    // search.putExtra(SearchManager.QUERY, searchQuery);
                    // startActivity(search);

                    // add the text in the arrayList
                    arrayList.add("You: " + searchQuery);
                    // sends the message to the server
                    if (mTcpClient != null)
                    {
                        mTcpClient.sendMessage("Android Client: " + searchQuery);
                        // }
                        // refresh the list
                        mAdapter.notifyDataSetChanged();

                        // Result code for various error.
                    }
                    else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR)
                    {
                        showToastMessage("Audio Error");
                    }
                    else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR)
                    {
                        showToastMessage("Client Error");
                    }
                    else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR)
                    {
                        showToastMessage("Network Error");
                    }
                    else if (resultCode == RecognizerIntent.RESULT_NO_MATCH)
                    {
                        showToastMessage("No Match");
                    }
                    else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR)
                    {
                        showToastMessage("Server Error");
                    }
                }
                // Result code for various error.
            }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public class connectTask extends AsyncTask<String, String, TCPClient>
    {
        @Override
        protected TCPClient doInBackground(String... message)
        {
            // we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived()
            {
                @Override
                // here the messageReceived method is implemented
                public void messageReceived(String message)
                {
                    try
                    {
                        // this method calls the onProgressUpdate
                        publishProgress(message);
                        if (message != null)
                        {
                            System.out
                                    .println("Return Message from Socket::::: >>>>> "
                                            + message);
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            mTcpClient.run();
            if (mTcpClient != null)
            {
                mTcpClient
                        .sendMessage("Initial Message when connected with Socket Server");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);

            // in the arrayList we add the messaged received from server
            arrayList.clear();
            arrayList.add(values[0]);

            // notify the adapter that the data set has changed. This means that
            // new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            System.out.println("onDestroy.");
            mTcpClient.sendMessage("App closed");
            conctTask.cancel(true);
            conctTask = null;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    void showToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
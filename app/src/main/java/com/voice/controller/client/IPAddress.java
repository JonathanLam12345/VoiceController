package com.voice.controller.client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class IPAddress extends Activity implements OnClickListener
{
    Button connect;
    Button buttonShowMe;
    EditText enterIP;
    WebView webView;
    boolean showWebView;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_address);
        this.setTitle("Voice Controller");
        connect = (Button) findViewById(R.id.button1);
        buttonShowMe = (Button) findViewById(R.id.buttonShowMe);
        enterIP = (EditText) findViewById(R.id.editText1);
        webView = (WebView) findViewById(R.id.webView);
        webView.setVisibility(View.GONE);
        showWebView = false;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddress = sp.getString("IP", "New User");
        enterIP.setText(ipAddress);
        int textLength = enterIP.getText().length();
        enterIP.setSelection(textLength, textLength);// place cursor at the end of enterIP
        connect.setOnClickListener(this);
		buttonShowMe.setOnClickListener(this);
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ip_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.about)
        {
            finish();
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }
        else if (id == R.id.help)
        {
            finish();
            Intent intent = new Intent(this, Help.class);
            startActivity(intent);
        }
        else if (id == R.id.main)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean validIP(String ip)
    {
        try
        {
            if (ip == null || ip.isEmpty())
            {
                return false;
            }
            String[] parts = ip.split("\\.");
            if (parts.length != 4)
            {
                return false;
            }

            for (String s : parts)
            {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255))
                {
                    return false;
                }
            }
            if (ip.endsWith("."))
            {
                return false;
            }
            return true;
        } catch (NumberFormatException nfe)
        {
            return false;
        }
    }

    public void onClick(View v)
    {
        if (v.getId() == R.id.buttonShowMe)
        {
            if (!showWebView)
            {
                webView.loadUrl("https://sites.google.com/view/voice-controller-app/how-to-config-ipv4-address");

                // Force links and redirects to open in the WebView instead of in a browser
                webView.setWebViewClient(new WebViewClient());

                webView.setVisibility(View.VISIBLE);
                buttonShowMe.setText("Hide Website.");
                showWebView = true;
            }
            else if (showWebView)
            {
                buttonShowMe.setText("Show Me How.");
                webView.setVisibility(View.GONE);
                showWebView = false;

            }
        }


        if (v.getId() == R.id.button1)
        {
            if (validIP(enterIP.getText().toString()) == true)
            {
                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(this);
                Editor edit = sp.edit();
                edit.putString("IP", enterIP.getText().toString());
                edit.commit();
                TCPClient.SERVERIP = enterIP.getText().toString();
                // Toast.makeText(IPAddress.this,"Your ip address is now: "+TCPClient.SERVERIP.toString(),Toast.LENGTH_SHORT).show();
                Intent myintent = new Intent(v.getContext(), MyActivity.class);
                this.startActivity(myintent);
            }
            else
            {
                Toast.makeText(IPAddress.this, "InValid IPv4 Address",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}

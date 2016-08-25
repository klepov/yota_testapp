package io.klep.yota_testapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String HTML = "HTML";
    private TextView textHtml;
    private EditText textUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textHtml = (TextView) findViewById(R.id.text_html);
        textUrl = (EditText) findViewById(R.id.text_url);
        Button show = (Button) findViewById(R.id.show);

        show.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String url = textUrl.getText().toString();
                if (!TextUtils.isEmpty(url) && URLUtil.isValidUrl(url) && Patterns.WEB_URL.matcher(url).matches())
                    new ParseTask().execute(url);
                else
                    Toast.makeText(getApplicationContext(), getText(R.string.invalid_url), Toast.LENGTH_SHORT)
                            .show();
            }
        });
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(HTML, textHtml.getText().toString());
    }

    @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            textHtml.setText(savedInstanceState.getString(HTML));
    }

    private class ParseTask extends AsyncTask<String, Void, String> {

        private HttpURLConnection urlConnection = null;
        private BufferedReader reader = null;
        private String htmlResult = "";
        private boolean isSuccess = true;
        private ProgressDialog dialog;

        @Override protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog
                    .show(MainActivity.this, "", getString(R.string.wait));
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                URL url = new URL(strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                htmlResult = buffer.toString();

            } catch (Exception e) {
                isSuccess = false;
            }
            return htmlResult;
        }


        @Override
        protected void onPostExecute(String html) {
            super.onPostExecute(html);
            dialog.dismiss();
            if (isSuccess)
                textHtml.setText(html);
            else
                Toast.makeText(getApplicationContext(), getText(R.string.domain_not_found), Toast.LENGTH_SHORT)
                        .show();
        }
    }
}

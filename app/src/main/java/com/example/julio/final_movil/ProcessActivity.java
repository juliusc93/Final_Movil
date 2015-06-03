package com.example.julio.final_movil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ProcessActivity extends ActionBarActivity {

    private List<Group> arraySpinner;
    private ProgressDialog pDialog;
    private static String url;
    private static String newurl;
    private JSONArray steps;
    private Spinner spinner;
    private TextView title;
    private TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        title = (TextView) findViewById(R.id.title);
        desc = (TextView) findViewById(R.id.description);
        Intent i = getIntent();
        url = i.getStringExtra("procurl");
        new GetData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_process, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getSteps(View view) {

        Intent i = new Intent(this, StepsActivity.class);
        i.putExtra("stepurl", newurl);
        i.putExtra("start", 0);
        i.putExtra("title", title.getText());
        startActivity(i);

    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ProcessActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            if (jsonStr != null) {
                try {

                    steps = new JSONArray(jsonStr);
                    arraySpinner = new ArrayList<Group>();
                    for(int i = 0; i< steps.length(); i++){
                        JSONObject obj = steps.getJSONObject(i);
                        Group g = new Group(obj.getString("name"), obj.getString("procedure_id"), obj.getString("description"));
                        arraySpinner.add(g);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            spinner = (Spinner) findViewById(R.id.content);
            ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(getBaseContext(), R.layout.spinner_style, arraySpinner);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Group g = (Group) adapterView.getItemAtPosition(i);
                    String procedure_id = g.getProcedure();
                    newurl = "https://dynamicformapi.herokuapp.com/steps/by_procedure/" + procedure_id+ ".json";
                    title.setText(g.getName());
                    desc.setText(g.getDescription());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }
}

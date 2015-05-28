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
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private List<Process> arraySpinner;
    private Spinner spinner;
    private ProgressDialog pDialog;
    private static String url = "http://dynamicformapi.herokuapp.com/groups.json";
    private static String newurl = "";
    private JSONArray groups;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.buttonLeft);

        new GetData().execute();
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.FragmentView, new StepsFragment())
                    .commit();
        }*/




    }
    public void getProcesses(View view) {

        Intent i = new Intent(this, ProcessActivity.class);
        i.putExtra("procurl", newurl);
        startActivity(i);

    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
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

                    groups = new JSONArray(jsonStr);
                    arraySpinner = new ArrayList<Process>();
                    for(int i = 0; i< groups.length(); i++){
                        JSONObject obj = groups.getJSONObject(i);
                        Process p = new Process(obj.getString("name"), obj.getString("group_id"));
                        arraySpinner.add(p);
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


            spinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<Process> adapter = new ArrayAdapter<Process>(getBaseContext(), R.layout.spinner_style, arraySpinner);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String group_id = ((Process) adapterView.getItemAtPosition(i)).getGroup();
                    newurl = "https://dynamicformapi.herokuapp.com/procedures/by_group/" + group_id+ ".json";
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            /*Fragment newFragment;
            newFragment = StepsFragment.newInstance(0,dataEntry.gettitle(),dataEntry.getinstruction());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.FragmentView, newFragment,String.valueOf(0));
            ft.commit();*/

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}

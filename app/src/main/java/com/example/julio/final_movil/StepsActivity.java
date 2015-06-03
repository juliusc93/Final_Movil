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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class StepsActivity extends ActionBarActivity {

    private List<Step> arraySpinner;
    private List<Step> array2;
    private TextView title;
    private TextView desc;
    private int step;
    private ProgressDialog pDialog;
    private Spinner spinner;
    private Spinner spinner2;
    private String url;
    private String newurl;
    private JSONArray steps;
    private String caption;
    private String caption2;
    private TextView desc2;
    private boolean multibranch;
    private boolean multistep;
    private JSONArray decisions;
    private JSONArray fields;
    private int cont = 0;

    private int majorBranch=0b00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        title = (TextView) findViewById(R.id.title);
        desc = (TextView) findViewById(R.id.description);
        desc2 = (TextView) findViewById(R.id.desc2);
        Intent i = getIntent();
        step = i.getIntExtra("start", 0);
        title.setText(i.getStringExtra("title"));
        desc.setText("hola");
        url = i.getStringExtra("stepurl");
        new GetData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_steps, menu);
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

    public void goToNext(View view){

        if(multibranch){
            int index = majorBranch;
            boolean x = true;
            try {
                JSONObject decision = decisions.getJSONObject(index);
                step = Integer.parseInt(decision.getString("go_to_step")) - 0b01; //stupid offset
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        revert();
        if(multistep){
            try{
                JSONObject field = fields.getJSONObject(cont);
                desc.setText(field.getString("caption"));
                if(cont == fields.length() - 1){
                    multistep = false;
                    cont = 0;
                }
                else cont++;
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        else{
            if(step < 0){
                desc.setText("Finished!");
                spinner.setVisibility(View.GONE);
            }
            else new GetData().execute();
        }

    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StepsActivity.this);
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
                    arraySpinner = new ArrayList<Step>();
                    array2 = new ArrayList<Step>();
                    JSONObject obj = steps.getJSONObject(step);
                    fields = new JSONObject(obj.getString("content")).getJSONArray("Fields");
                    decisions = new JSONObject(obj.getString("content")).getJSONArray("Decisions");
                    if(fields.length() > 1){
                        if(decisions.length() > 1) multibranch = true;
                        else{ multibranch = false; multistep = true; }
                    }
                    else{ multibranch = false; multistep = false; }
                    if(!multistep) {
                        for (int i = 0; i < fields.length(); i++) {
                            JSONObject field = fields.getJSONObject(i);
                            if (i == 0) caption = field.getString("caption");
                            else caption2 = field.getString("caption");
                            int type = Integer.parseInt(field.getString("field_type"));
                            Step s;
                            if (type != 3) {
                                JSONArray values = field.getJSONArray("possible_values");

                                for (int j = 0; j < values.length(); j++) {
                                    try {
                                        String name = values.getString(j), decision = "-1";
                                        JSONArray ja = new JSONObject(obj.getString("content")).getJSONArray("Decisions");
                                        for (int k = 0; k < ja.length(); k++) {
                                            JSONObject jo = ja.getJSONObject(k);
                                            if (jo.getJSONArray("branch").getJSONObject(0).getString("value").equals(name)) {
                                                if (!multibranch)
                                                    decision = jo.getString("go_to_step");
                                                else decision = "-1";
                                                break;
                                            }
                                        }
                                        s = new Step(name, Integer.parseInt(decision));
                                    } catch (JSONException e) {
                                        s = new Step(values.getString(j), -1);
                                    }
                                    if (i == 0) arraySpinner.add(s);
                                    else {
                                        array2.add(s);
                                    }
                                }
                            } else {
                                String decision = new JSONObject(obj.getString("content")).getJSONArray("Decisions").getJSONObject(0).getString("go_to_step");
                                s = new Step("This step has no branch. Just click the button below", Integer.parseInt(decision));
                                arraySpinner.add(s);
                            }
                        }
                    }
                    else{
                        String decision = decisions.getJSONObject(0).getString("go_to_step");
                        Step s = new Step("This step has no branch. Just click the button below", Integer.parseInt(decision));
                        arraySpinner.add(s);
                    }

                    /*JSONObject test = new JSONObject(str);
                    JSONObject prueba = test.getJSONArray("Fields").getJSONObject(0);
                    JSONArray test2 = prueba.getJSONArray("possible_values");
                    String value = test2.getString(0);
                    str.substring(2);*/
                    //Group g = new Group(obj.getString("name"), obj.getString("procedure_id"), obj.getString("description"));
                    //arraySpinner.add(g);
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
            spinner2 = (Spinner) findViewById(R.id.spinner2);
            if(multibranch)
                prepare();
            if(!multistep) desc.setText(caption);
            else{
                try {
                    desc.setText(fields.getJSONObject(0).getString("caption"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                cont++;
            }
            ArrayAdapter<Step> adapter = new ArrayAdapter<Step>(getBaseContext(), R.layout.spinner_style, arraySpinner);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Step s = (Step) adapterView.getItemAtPosition(i);
                    if (!multibranch) step = s.getNext() - 1; // stupid offset
                    else majorBranch = i * (int)Math.pow(2,i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    private void prepare(){
        desc2.setVisibility(View.VISIBLE);
        spinner2.setVisibility(View.VISIBLE);
        title.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 3f));
        desc2.setText(caption2);
        ArrayAdapter<Step> adapter2 = new ArrayAdapter<Step>(getBaseContext(), R.layout.spinner_style, array2);
        spinner2.setAdapter(adapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Step s = (Step) adapterView.getItemAtPosition(i);
                majorBranch += i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void revert(){
        desc2.setVisibility(View.GONE);
        spinner2.setVisibility(View.GONE);
        title.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 2.23f));
        desc2.setText("");
        spinner2.setAdapter(null);
        multibranch = false;
    }
}

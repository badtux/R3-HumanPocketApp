package com.rype3.pocket_hrm;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

class ProcressAsyncTask extends AsyncTask<Void, Void, String> {

    private Activity activity;
    private String url, email, pin, epf, HTTP_TYPE, version, token,deviceId,uid,checked_at,checkout_at,meta;
    private ProgressDialog dialog;
    private final static String TAG = "Procress AsyncTask";
    private int type;
    private Context context;

    public ProcressAsyncTask(Context context) {
        this.context = context;
    }

    ProcressAsyncTask(Activity activity,
                      String url,
                      String email,
                      String pin,
                      String epf,
                      String HTTP_TYPE,
                      int type,
                      String version,
                      String token,
                      String deviceId,
                      String uid,
                      String checked_at,
                      String checkout_at,
                      String meta) {
        super();
        this.activity = activity;
        this.url = url;
        this.email = email;
        this.pin = pin;
        this.epf = epf;
        this.HTTP_TYPE = HTTP_TYPE;
        this.type = type;
        this.version = version;
        this.token = token;
        this.deviceId = deviceId;
        this.uid = uid;
        this.checked_at = checked_at;
        this.checkout_at = checkout_at;
        this.meta = meta;
    }



    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait...");
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return loadJSON(
                    this.url,
                    this.email,
                    this.pin,
                    this.epf,
                    this.HTTP_TYPE,
                    this.type,
                    this.version,
                    this.token,
                    this.deviceId,
                    this.uid,
                    this.checked_at,
                    this.checkout_at,
                    this.meta).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final String result) {
        dialog.dismiss();
        switch (this.type) {
            case 0:
                ((PINnumberActivity) activity).parseJsonResponse(result);//1567234
                break;

            case 1:
                ((PINnumberActivity) activity).parseJsonRegisterResponse(result);
                break;

            case 2:
                ((Sign_inActivity) activity).parseJsonRegisterResponse(result);
                break;

            case 3:
                ((MainActivity) activity).parseJsonResponse(result);
                break;

            case 4:
                ((Sign_inActivity) activity).parseJsonRegisterForAttendandeResponse(result);
                break;

            case 5:
                ((HistoryActivity) activity).parseJsonResponseHistory(result);
                break;
        }
    }

    private JSONObject loadJSON(String url,
                                String email,
                                String pin,
                                String epf,
                                String HTTP_TYPE,
                                int type,
                                String version,
                                String token,
                                String deviceId,
                                String uid,
                                String checked_at,
                                String checkout_at,
                                String meta) {
        // Creating JSON Parser instance
        JSONParser jParser = new JSONParser();

        // getting JSON string from URL

        return jParser.getJSONFromUrl(url, email, pin, epf, HTTP_TYPE, type, version, token,deviceId,uid,checked_at,checkout_at ,meta);
    }

    private class JSONParser {
        private InputStream is = null;
        private JSONObject jObj = null;
        private String json = "";

        // constructor
        JSONParser() {
        }

        JSONObject getJSONFromUrl(String url,
                                  String email,
                                  String pin,
                                  String epf,
                                  String HTTP_TYPE,
                                  int type,
                                  String version,
                                  String token,
                                  String deviceId,
                                  String uid,
                                  String checked_at, String checkout_at, String meta) {
            Log.e("URL ", url);
            switch (HTTP_TYPE) {
                case "POST":
                    Log.e("HTTP TYPE ", "POST");
                    // Making HTTP POST request
                    try {
                        // defaultHttpClient
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(url);
                        if (token != null) {
                            httpPost.addHeader("Oauth-TOken", token);
                        }
                        try {
                            List<BasicNameValuePair> nameValuePairs = new LinkedList<BasicNameValuePair>();

                            if (email != null) {
                                nameValuePairs.add(new BasicNameValuePair("email", email));
                            }

                            if (pin != null) {
                                nameValuePairs.add(new BasicNameValuePair("pin", pin));
                                nameValuePairs.add(new BasicNameValuePair("password", pin));
                            }

                            if (epf != null) {
                                nameValuePairs.add(new BasicNameValuePair("epf", epf));
                            }

                            if (token != null) {
                                nameValuePairs.add(new BasicNameValuePair("token", token));
                            }

                            if (uid != null) {
                                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                            }

                            if (checked_at != null) {
                                nameValuePairs.add(new BasicNameValuePair("checked_at", checked_at));
                                Log.e("checked_at LOG ", checked_at);
                            }

                            if (checkout_at != null) {
                                nameValuePairs.add(new BasicNameValuePair("checkout_at", checkout_at));
                                Log.e("checkout_at LOG ", checkout_at);
                            }

                            if (deviceId != null) {
                                nameValuePairs.add(new BasicNameValuePair("device_id", deviceId));
                                nameValuePairs.add(new BasicNameValuePair("imei", deviceId));
                                nameValuePairs.add(new BasicNameValuePair("device_name", deviceId));
                            }

                            if (meta != null){
                                nameValuePairs.add(new BasicNameValuePair("meta", meta));

                                Log.e("LOG ", meta);
                            }

                            nameValuePairs.add(new BasicNameValuePair("description", ""));


                            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        is = httpEntity.getContent();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        is.close();
                        json = sb.toString();
                    } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result " + e.toString());
                    }

                    // try parse the string to a JSON object
                    try {
                        jObj = new JSONObject(json);
                    } catch (JSONException e) {
                        Log.e("JSON Parser", "Error parsing data " + e.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//19711031 rmncrajaguru@malkey.lk
                    break;

                case "GET":
                    Log.e("HTTP TYPE ", "GET");
                    StringBuilder stringBuilder = new StringBuilder();
                    HttpClient httpClient = new DefaultHttpClient();

                    List<BasicNameValuePair> nameValuePairs = new LinkedList<BasicNameValuePair>();

                    if (email != null) {
                        nameValuePairs.add(new BasicNameValuePair("email", email));
                    }

                    if (pin != null) {
                        nameValuePairs.add(new BasicNameValuePair("pin", pin));
                        nameValuePairs.add(new BasicNameValuePair("password", pin));
                    }

                    if (epf != null) {
                        nameValuePairs.add(new BasicNameValuePair("epf", epf));
                    }

                    if (token != null) {
                        nameValuePairs.add(new BasicNameValuePair("token", token));
                    }

                    if (deviceId != null) {
                        nameValuePairs.add(new BasicNameValuePair("device_id", deviceId));
                    }

                    if (uid != null) {
                        nameValuePairs.add(new BasicNameValuePair("uid", uid));
                    }

                    if (checked_at != null) {
                        nameValuePairs.add(new BasicNameValuePair("checked_at", checked_at));
                    }

                    if (checkout_at != null) {
                        nameValuePairs.add(new BasicNameValuePair("checkout_at", checkout_at));
                    }

                    if (meta != null){
                        nameValuePairs.add(new BasicNameValuePair("meta", meta));
                    }

                    nameValuePairs.add(new BasicNameValuePair("description", ""));

                    HttpGet httpget = new HttpGet(url + "?" + URLEncodedUtils.format(nameValuePairs, "utf-8"));
                    httpget.addHeader("Oauth-TOken", token);
                    try {
                        HttpResponse response = httpClient.execute(httpget);
                        StatusLine statusLine = response.getStatusLine();
                        int statusCode = statusLine.getStatusCode();

                        Log.e(TAG, " statusCode " + String.valueOf(statusCode));

                        if (statusCode == 200) {
                            HttpEntity entity = response.getEntity();
                            InputStream inputStream = entity.getContent();
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(inputStream));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line);
                                jObj = new JSONObject(stringBuilder.toString());
                            }
                            inputStream.close();
                        } else if (statusCode == 400) {
                        } else {
                            Log.d("JSON", "Failed to download file");
                        }
                    } catch (Exception e) {
                        Log.d("readJSONFeed", e.getLocalizedMessage());
                    }
                    break;
            }
            return jObj;
        }
    }
//    public static File getImage(String imagename) {
//
//        File mediaImage = null;
//        try {
//            String root = Environment.getExternalStorageDirectory().toString();
//            File myDir = new File(root);
//            if (!myDir.exists())
//                return null;
//
//            mediaImage = new File(myDir.getPath() + "/.your_specific_directory/"+imagename);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return mediaImage;
//    }
}



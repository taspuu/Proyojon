package bd.com.taspi.apps;

import static bd.com.taspi.apps.CustomTools.alert;
import static bd.com.taspi.apps.CustomTools.log;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.webkit.CookieManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@SuppressLint("all")
public class Internet3 extends AsyncTask<Void, Void, JSONObject> {
    private final TaskListener taskListener;
    private final String boundary = "*****";
    private final String lineEnd = "\r\n";
    String twoHyphens = "--";
    private final Activity activity;
    private final String url;
    private Integer code = -1;
    private Map<String, String> inputs = null;
    private Map<String, Bitmap> files = null;

    private String allLines = "";



    public Internet3(Activity activity, String url, TaskListener listener) {
        this.activity = activity;
        this.url = url;
        this.taskListener = listener;
        this.inputs = null;
        this.files = null;
    }


    public Internet3(Activity activity, String url, Map<String, String> inputs, TaskListener listener) {
        this.activity = activity;
        this.url = url;
        this.taskListener = listener;
        this.inputs = inputs;
        this.files = null;
    }

    public Internet3(Activity activity, String url, Map<String, String> inputs, Map<String, Bitmap> files, TaskListener listener) {
        this.activity = activity;
        this.url = url;
        this.taskListener = listener;
        this.inputs = inputs;
        this.files = files;
    }

    public static boolean isConnected(Activity activity) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public Internet3 connect() {
        this.executeOnExecutor(Internet3.THREAD_POOL_EXECUTOR);
        return this;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            super.onPostExecute(result);
            if (this.taskListener != null) {
                this.taskListener.onFinished(code, result);
            }
            if (code != 200 && code != -1) {
//                alert(activity, code + " ", allLines);
            }
//            if (result.has("error") && activity != null) {
//                CustomTools customTools = new CustomTools(activity);
////                customTools.toast(result.getString("error"));
//            }


        } catch (Exception e) {
            log(e.getMessage(), true);
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            if (activity != null && !isConnected(activity)) {
                alert(activity, activity.getString(R.string.warning), activity.getString(R.string.no_internet));
            } else {
                URL newLink = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) newLink.openConnection();
                // Fetch and set cookies in requests
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(httpURLConnection.getURL().toString());
                if (cookie != null) {
                    httpURLConnection.setRequestProperty("Cookie", cookie);
                }
                httpURLConnection.setRequestMethod("POST");

                String userAgent = activity == null ? CustomTools.getUserAgent() : CustomTools.getUserAgent(activity);

                httpURLConnection.setRequestProperty("User-Agent", userAgent);
                httpURLConnection.setRequestProperty("App-Version-Code", String.valueOf(BuildConfig.VERSION_CODE));
                httpURLConnection.setRequestProperty("Referer", "free-palestine");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                // add parameters
                if (inputs != null) {
                    for (Map.Entry<String, String> entry : inputs.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (key != null && value != null && !key.isEmpty() && !value.isEmpty()) {
                            addFormField(key, value, dataOutputStream);
                        }
                    }
                }

                // add images
                if (files != null) {
                    for (Map.Entry<String, Bitmap> entry : files.entrySet()) {
                        String key = entry.getKey();
                        Bitmap value = entry.getValue();
                        if (key != null && value != null && !key.isEmpty()) {
                            addFilePart(key, value, dataOutputStream);
                        }
                    }
                }

                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dataOutputStream.flush();
                dataOutputStream.close();
                outputStream.close();
                this.code = httpURLConnection.getResponseCode();
                // Get cookies from responses and save into the cookie manager
                List<String> cookieList = httpURLConnection.getHeaderFields().get("Set-Cookie");
                if (cookieList != null) {
                    for (String cookieTemp : cookieList) {
                        cookieManager.setCookie(httpURLConnection.getURL().toString(), cookieTemp);
                    }
                }
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                allLines = stringBuilder.toString();
                try {
                    return new JSONObject(allLines);
                } catch (Exception e) {
                    log(allLines, true);
                }
            }
        } catch (Exception e) {
            log(this.url + " - Internet3 error:" + e.getMessage(), true);
        }
        code = -1;
        return new JSONObject();
    }

    private void addFormField(String fieldName, String fieldValue, OutputStream outputStream) {
        try {
            String builder = twoHyphens + boundary + lineEnd +
                    "Content-Disposition: form-data; name=\"" + fieldName + "\"" + lineEnd +
                    lineEnd +
                    fieldValue + lineEnd;

            outputStream.write(builder.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            log(e.getMessage(), true);
        }
    }

    private void addFilePart(String paramName, Bitmap bitmap, OutputStream outputStream) {
        try {
            String fileName = "image-" + Math.random() + ".png";
            String contentType = "image/png";

            // Create the file part header
            String sb = twoHyphens + boundary + lineEnd +
                    "Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"" + lineEnd +
                    "Content-Type: " + contentType + lineEnd +
                    lineEnd;
            outputStream.write(sb.getBytes());

            // Write the bitmap data to the output stream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageData = baos.toByteArray();
            outputStream.write(imageData);

            // Add the closing boundary
            outputStream.write(lineEnd.getBytes());
            outputStream.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
        } catch (Exception e) {
            log(e.getMessage(), true);
        }
    }


    public interface TaskListener {
        void onFinished(Integer code, JSONObject result);
    }


}

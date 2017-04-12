package Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil extends Thread{
    private String baseUrl;
    private String response = "";

    public HttpUtil(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void run() {
        try {
            //String baseUrl = "http://katarinar.top/tt/server/";
            URL url = new URL(baseUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader buff = new BufferedReader(inputStreamReader);
            String readLine;
            while ((readLine = buff.readLine()) != null) {
                response += readLine;

            }
            inputStreamReader.close();
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResponse() {
        return response;
    }
}

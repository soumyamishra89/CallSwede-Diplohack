package diplohack.callswede.view;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by SOMU on 30/05/16.
 */
public class OpenDataUrl {

    public static final String IMPORT = "Import";
    public static final String EXPORT = "Export";
    public static final String GIRLNAME = "GirlName";
    public static final String ISLAND = "Island";
    public static final String INFO = "Info";
    public static final String PLACEHOLDER = "#placeholder";
    private String urlString;
    private  String json;
    private String displayInfo;
    private String identifier;
    private String[] boldText;

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public OpenDataUrl(String url, String json, String identifier, String displayInfo, String[] boldText){
        this.urlString = url;
        this.json = json;
        this.identifier = identifier;
        this.displayInfo = displayInfo;
        this.boldText = boldText;
    }

    public HttpURLConnection getHttpConnection() throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(json);

        writer.flush();
        writer.close();
        os.close();
        return conn;
    }

    public static SpannableStringBuilder extractData(JSONArray data, String identifier, String displayInfo, List<String> boldText) throws JSONException{
        String result = null;

        for(int i=0; i<data.length(); i++) {
            JSONObject object = data.getJSONObject(i);
            if(GIRLNAME.equals(identifier)) {
                if(object.getJSONArray("values").get(0).toString().equals("1"))
                    result = object.getJSONArray("key").get(0).toString().substring(2);
            } else {
                String number = object.getJSONArray("values").get(0).toString();
                result =  numberFormat.format(Integer.parseInt(number));
            }
        }
        List<String> textToBold = new ArrayList<>(boldText);
        textToBold.add(result);
        return makeSectionOfTextBold(displayInfo.replace(PLACEHOLDER, result), textToBold);
    }

    public String getIdentifier(){
        return identifier;
    }

    public String getDisplayInfo(){
        return displayInfo;
    }

    public String[] getBoldText(){
        return boldText;
    }

    public static SpannableStringBuilder makeSectionOfTextBold(String text, List<String> textBold){

        SpannableStringBuilder builder=new SpannableStringBuilder();
        String testText = text.toLowerCase(Locale.US);
        builder.append(text);

        for(int i = 0; i<textBold.size(); i++) {
            String textToBold = textBold.get(i);
            if (textToBold.length() > 0 && !textToBold.trim().equals("")) {

                //for counting start/end indexes

                String testTextToBold = textToBold.toLowerCase(Locale.US);
                int startingIndex = testText.indexOf(testTextToBold);
                int endingIndex = startingIndex + testTextToBold.length();
                //for counting start/end indexes

                if (startingIndex < 0 || endingIndex < 0) {
                    return builder.append(text);
                } else if (startingIndex >= 0 && endingIndex >= 0) {
                    builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
                }
            } else {
                return builder.append(text);
            }
        }
        return builder;
    }
}

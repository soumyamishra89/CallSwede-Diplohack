package diplohack.callswede;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import diplohack.callswede.view.OpenDataUrl;

/**
 * Created by SOMU on 30/05/16.
 */
public class SnicksnackApplication extends Application {

    List<OpenDataUrl> openDataUrls = new ArrayList<OpenDataUrl>(4);

    public static Random random = new Random();

    @Override
    public void onCreate(){
        super.onCreate();
        openDataUrls.clear();
        openDataUrls.add(new OpenDataUrl("http://api.scb.se/OV0104/v1/doris/en/ssd/START/HA/HA0201/HA0201A/ImportExportSnabbAr", "{\n" +
                "  \"query\": [\n" +
                "    {\n" +
                "      \"code\": \"ImportExport\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"item\",\n" +
                "        \"values\": [\n" +
                "          \"ITOT\"\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"Tid\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"item\",\n" +
                "        \"values\": [\n" +
                "          \"2015\"\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"response\": {\n" +
                "    \"format\": \"json\"\n" +
                "  }\n" +
                "}",OpenDataUrl.IMPORT, "The total imports of Sweden in 2015 was worth "+OpenDataUrl.PLACEHOLDER+" million SEK", new String[]{"imports"}));
        openDataUrls.add(new OpenDataUrl("http://api.scb.se/OV0104/v1/doris/en/ssd/START/HA/HA0201/HA0201A/ImportExportSnabbAr", "{\n" +
                "  \"query\": [\n" +
                "    {\n" +
                "      \"code\": \"ImportExport\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"item\",\n" +
                "        \"values\": [\n" +
                "          \"ETOT\"\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"Tid\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"item\",\n" +
                "        \"values\": [\n" +
                "          \"2015\"\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"response\": {\n" +
                "    \"format\": \"json\"\n" +
                "  }\n" +
                "}", OpenDataUrl.EXPORT, "The total exports of Sweden in 2015 was worth "+OpenDataUrl.PLACEHOLDER+" million SEK", new String[]{"exports"}));

        openDataUrls.add(new OpenDataUrl("http://api.scb.se/OV0104/v1/doris/en/ssd/START/MI/MI0812/OarArealOmk", "{\n" +
                "  \"query\": [\n" +
                "    {\n" +
                "      \"code\": \"Region\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"vs:RegionRiket99\",\n" +
                "        \"values\": [\n" +
                "          \"00\"\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ContentsCode\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"item\",\n" +
                "        \"values\": [\n" +
                "          \"MI0812AV\"\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"response\": {\n" +
                "    \"format\": \"json\"\n" +
                "  }\n" +
                "}", OpenDataUrl.ISLAND, "The total number of islands in Sweden is "+OpenDataUrl.PLACEHOLDER, new String[]{"islands"}));

        openDataUrls.add(new OpenDataUrl("http://api.scb.se/OV0104/v1/doris/en/ssd/START/BE/BE0001/BE0001T05AR","{\n" +
                "  \"query\": [\n" +
                "    {\n" +
                "      \"code\": \"Tilltalsnamn\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"vs:Flickor10\",\n" +
                "        \"values\": [\n" +
                "          \"20Agnes\",\n" +
                "          \"20Alice\",\n" +
                "          \"20Alicia\",\n" +
                "          \"20Alva\",\n" +
                "          \"20Amanda\",\n" +
                "          \"20Ebba\",\n" +
                "          \"20Elin\",\n" +
                "          \"20Ella\",\n" +
                "          \"20Elsa\",\n" +
                "          \"20Emma\",\n" +
                "          \"20Hanna\",\n" +
                "          \"20Ida\",\n" +
                "          \"20Johanna\",\n" +
                "          \"20Julia\",\n" +
                "          \"20Klara\",\n" +
                "          \"20Lilly\",\n" +
                "          \"20Linnéa\",\n" +
                "          \"20Maja\",\n" +
                "          \"20Matilda\",\n" +
                "          \"20Moa\",\n" +
                "          \"20Molly\",\n" +
                "          \"20Olivia\",\n" +
                "          \"20Saga\",\n" +
                "          \"20Wilma\"\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ContentsCode\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"item\",\n" +
                "        \"values\": [\n" +
                "          \"BE0001AI\"\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"Tid\",\n" +
                "      \"selection\": {\n" +
                "        \"filter\": \"item\",\n" +
                "        \"values\": [\n" +
                "          \"2015\"\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"response\": {\n" +
                "    \"format\": \"json\"\n" +
                "  }\n" +
                "}", OpenDataUrl.GIRLNAME, "In 2015 the most common girl child name in Sweden was "+OpenDataUrl.PLACEHOLDER, new String[]{"common"} ));
    }

    public List<OpenDataUrl> getOpenDataUrls(){
        return openDataUrls;
    }
}

package it.unical.demacs.informatica.unicareermanager.model;

import it.unical.demacs.informatica.unicareermanager.util.ExecutorServiceManager;
import javafx.concurrent.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UniversityDownloaderHandler {
    private static final UniversityDownloaderHandler instance = new UniversityDownloaderHandler();
    // all universities names are in english
    private static final String UNIVERSITY_API_LINK = "http://universities.hipolabs.com/";
    // daily restriction of translation requests
    private static final String MYMEMORY_API_LINK = "https://api.mymemory.translated.net/";
    // MUR API
    private static final String ITALIAN_UNIVERSITY_API_LINK = "https://dati-ustat.mur.gov.it/api/3/action/";
    // ID of the resources
    private static final String RESOURCE_ID = "a332a119-6c4b-44f5-80eb-3aca45a9e8e8";
    // timeout for the connection
    private static final int timeout = 5000;


    private UniversityDownloaderHandler() {
    }

    public static UniversityDownloaderHandler getInstance() {
        return instance;
    }

    private String capitalizeEachWord(String str) {
        // the API for translation can give strings with poorly capitalized letters
        if (str == null || str.isBlank()) {
            return str;
        }
        String[] words = str.split(" +");
        StringBuilder capitalizedWords = new StringBuilder();
        for (String word : words) {
            if (!word.isBlank()) {
                capitalizedWords.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return capitalizedWords.toString().trim();
    }

    private StringBuilder readString(InputStreamReader streamReader) throws IOException {
        // building string
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(streamReader);
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            return builder;
        } catch (IOException e) {
            // debug
            // e.printStackTrace();
            return null;
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    private String translateToItalian(String text) {
        // italian translation of a text
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String toTranslate = "get?q=" + encodedText;
        String langPair = URLEncoder.encode("en|it", StandardCharsets.ISO_8859_1);
        HttpURLConnection connection = null;
        try {
            URL url = URI.create(MYMEMORY_API_LINK + toTranslate + "&langpair=" + langPair).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            StringBuilder builder = readString(new InputStreamReader(connection.getInputStream()));
            if (builder != null) {
                JSONObject object = new JSONObject(builder.toString());
                String universityName = object.getJSONObject("responseData").getString("translatedText");
                return capitalizeEachWord(universityName);
            }
        } catch (IOException ignoredException) {
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return capitalizeEachWord(text);
    }

    public Task<List<String>> getHipolabsUniversities() {
        // a task to get all italian universities from Hipolabs university API
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            URL url = URI.create(UNIVERSITY_API_LINK + "search?country=Italy").toURL();
            StringBuilder builder = readString(new InputStreamReader(url.openStream()));
            if (builder == null)
                return null;
            JSONArray response = new JSONArray(builder.toString());
            List<String> universitiesList = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject object = response.getJSONObject(i);
                String universityName = object.getString("name");
                universitiesList.add(translateToItalian(universityName));
            }
            return universitiesList;
        });
    }

    public Task<List<String>> getUniversities() {
        // a task to get all italian universities from MUR
        return ExecutorServiceManager.getInstance().asyncCall(() -> {
            List<String> universitiesList = new ArrayList<>();
            URL url = URI.create(ITALIAN_UNIVERSITY_API_LINK + "datastore_search?resource_id=" + RESOURCE_ID).toURL();
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);
                StringBuilder builder = readString(new InputStreamReader(connection.getInputStream()));
                if (builder == null) {
                    return null;
                }
                JSONObject result = new JSONObject(builder.toString()).getJSONObject("result");
                JSONArray response = result.getJSONArray("records");
                for (int i = 0; i < response.length(); i++) {
                    JSONObject object = response.getJSONObject(i);
                    if (object.getString("status").matches("Attivo +")) {
                        universitiesList.add(object.getString("NomeEsteso"));
                    }
                }
                return universitiesList;
            } catch (Exception ignoredException) {
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            return universitiesList;
        });
    }
}

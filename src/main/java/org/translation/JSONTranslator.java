package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final Map<String, Map<String, String>> countriesLanguages = new HashMap<>();

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     *
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject country = jsonArray.getJSONObject(i);
                Set<String> langs = country.keySet();
                langs.remove("id");
                langs.remove("alpha2");
                String countryCode = country.getString("alpha3");
                langs.remove("alpha3");
                Map<String, String> translations = new HashMap<>();
                List<String> langList = new ArrayList<>(langs);
                for (String s : langList) {
                    translations.put(s, country.getString(s));
                }
                countriesLanguages.put(countryCode, translations);
            }
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        Map<String, String> translations = countryLanguages.get(country);
        Set<String> languages = translations.keySet();
        return new ArrayList<>(languages);
    }

    @Override
    public List<String> getCountries() {
        Set<String> countries = countriesLanguages.keySet();
        return new ArrayList<>(countries);
    }

    @Override
    public String translate(String country, String language) {
        Map<String, String> countryMap = countriesLanguages.get(country);
        return countryMap.getOrDefault(language, null);
    }
}

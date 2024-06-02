package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Reponse;
import com.twd.SpringSecurityJWT.repository.ReponseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class ReponseServiceImp implements IReponseService{
    ReponseRepository reponseRepository;
    private final List<String> badWords = Arrays.asList("tunis");

    @Override
    public Reponse addReponse(Reponse reponse) {
        return reponseRepository.save(reponse);
    }

    @Override
    public Reponse updateReponse(Reponse reponse) {
        return reponseRepository.save(reponse);
    }

    @Override
    public void removeReponseById(int idReponse) {
        reponseRepository.deleteById(idReponse);
    }

    @Override
    public Reponse getReponse(int idReponse) {
        return reponseRepository.findById(idReponse).get();
    }

    @Override
    public List<Reponse> getAllReponses() {return reponseRepository.findAll(); }

    @Override
    public String translateReponseToArabic(String text) {
        String langPair = "en|ar";
        /*
        English to Arabic: String langPair = "en|ar";
        English to Spanish: String langPair = "en|es";
        English to French: String langPair = "en|fr";
        English to German: String langPair = "en|de";
        English to Chinese : String langPair = "en|zh-CN";
        English to Russian: String langPair = "en|ru";
         */
        String encodedText = URLEncoder.encode(text);

        try {
            URL url = new URL("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + langPair.split("\\|")[0] + "&tl=" + langPair.split("\\|")[1] + "&dt=t&q=" + encodedText);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            in.close();

            String translatedText = parseGoogleTranslateResponse(response.toString());
            return translatedText;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String translateReponseToSpanish(String text) {
        String langPair = "en|es";
        /*
        English to Arabic: String langPair = "en|ar";
        English to Spanish: String langPair = "en|es";
        English to French: String langPair = "en|fr";
        English to German: String langPair = "en|de";
        English to Chinese : String langPair = "en|zh-CN";
        English to Russian: String langPair = "en|ru";
         */
        String encodedText = URLEncoder.encode(text);

        try {
            URL url = new URL("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + langPair.split("\\|")[0] + "&tl=" + langPair.split("\\|")[1] + "&dt=t&q=" + encodedText);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            in.close();

            String translatedText = parseGoogleTranslateResponse(response.toString());
            return translatedText;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String translateReponseToFrench(String text) {
        String langPair = "en|fr";
        /*
        English to Arabic: String langPair = "en|ar";
        English to Spanish: String langPair = "en|es";
        English to French: String langPair = "en|fr";
        English to German: String langPair = "en|de";
        English to Chinese : String langPair = "en|zh-CN";
        English to Russian: String langPair = "en|ru";
         */
        String encodedText = URLEncoder.encode(text);

        try {
            URL url = new URL("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + langPair.split("\\|")[0] + "&tl=" + langPair.split("\\|")[1] + "&dt=t&q=" + encodedText);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            in.close();

            String translatedText = parseGoogleTranslateResponse(response.toString());
            return translatedText;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String parseGoogleTranslateResponse(String response) {
        String[] parts = response.split("\",\"");
        String translation = parts[0].substring(4);
        return translation;
    }

    @Override
    public boolean ReponseContainsBadWord(Reponse reponse) {
        String content = reponse.getReponse().toLowerCase();
        for (String badWord : badWords) {
            if (content.contains(badWord)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getNumberOfResponses() {
        return reponseRepository.count();
    }

}

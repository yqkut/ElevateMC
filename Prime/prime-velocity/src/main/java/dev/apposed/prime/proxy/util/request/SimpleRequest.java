package dev.apposed.prime.proxy.util.request;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class SimpleRequest {

    @SneakyThrows
    public static CompletableFuture<JSONObject> getRequest(String urlString) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject object = new JSONObject();
            try {
                URL url = new URL(urlString);
                object = (JSONObject) new JSONParser().parse(new InputStreamReader(url.openStream()));

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            } catch(ParseException e) {
                e.printStackTrace();
            }

            return object;
        });
    }
}
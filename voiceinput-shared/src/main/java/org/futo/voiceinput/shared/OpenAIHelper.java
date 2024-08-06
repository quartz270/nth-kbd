package org.futo.voiceinput.shared;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OpenAIHelper {

    private String API_KEY = "";

    public OpenAIHelper(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public interface ResponseCallback {
        void onResponse(String content);
        void onFailure(String error);
    }

    public void makeRequest(String userMessage, ResponseCallback cb) {
        OkHttpClient client = new OkHttpClient();

        // Create the JSON body for the request
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-4o-mini");
            JSONArray messages = makeMessagesArray(userMessage);
            jsonBody.put("messages", messages);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the request
        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        // Execute the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> cb.onFailure(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        JSONObject choice = choices.getJSONObject(0);
                        JSONObject message = choice.getJSONObject("message");
                        String content = message.getString("content");
                        new Handler(Looper.getMainLooper()).post(() -> cb.onResponse(content));
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(() -> cb.onFailure(e.getMessage()));
                    }
                } else {
                    String errorMessage = "Request failed: " + response.code() + " - " + response.message();
                    new Handler(Looper.getMainLooper()).post(() -> cb.onFailure(errorMessage));
                }
            }
        });
    }

    private static @NonNull JSONArray makeMessagesArray(String userMessageString) throws JSONException {
        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a typing assistant - you are part of the user's phone keyboard, the user will ask you to help them generate a text message. Don't be super formal. Generate short responses unless specified by the user. Just reply with the generated text and nothing else. Do not add quotes to the response. Important: The User is not talking to you.");
        messages.put(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", userMessageString);
        messages.put(userMessage);
        return messages;
    }
}

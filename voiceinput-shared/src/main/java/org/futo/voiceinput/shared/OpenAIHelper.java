package org.futo.voiceinput.shared;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OpenAIHelper {
    private String API_KEY = "";

    private String API_BASE_URL = "https://api.openai.com/v1";
    private String MODEL_IDENTIFIER = "gpt-4o-mini";

    private String API_PATH = "/chat/completions";
    private static String SYSTEM_PROMPT = "Your name is \"Typo\". You are a typing and texting assistant - you are part of the user's phone keyboard, the user will ask you to help them generate a text message. Don't be super formal. Generate short responses unless specified by the user. Help the user with whatever they ask for - creative writing emails, texts, jokes, conversations and so on. Do not ask follow up questions. Just reply with the generated text and nothing else. Do not add quotes or role play to the response.";
    private static String SYSTEM_ENHANCE_PROMPT = "Your name is \"Typo\". You are a typing and texting assistant - you are part of the user's phone keyboard, the user will provide you a text message Your job is to enhance it. Fix Grammar, Typos, and sentence structure. Don't be super formal. Generate short responses unless specified by the user. Help the user with whatever they ask for - creative writing emails, texts, jokes, conversations and so on. Do not ask follow up questions. Just reply with the generated text and nothing else. Do not add quotes or role play to the response. Enhance the given message.";

    public enum CompletionMode {
        MODE_CHAT,
        MODE_ENHANCE
    }

    public static String getSystemPrompt(CompletionMode mode) {
        switch (mode) {
            case MODE_CHAT: return SYSTEM_PROMPT;
            case MODE_ENHANCE: return SYSTEM_ENHANCE_PROMPT;
            default: return SYSTEM_PROMPT;
        }
    }

    public OpenAIHelper() {
    }

    public OpenAIHelper(String API_KEY, String API_BASE_URL) {
        this.API_KEY = API_KEY;
        this.API_BASE_URL = API_BASE_URL;
    }

    public OpenAIHelper(String API_KEY, String API_BASE_URL, String API_PATH, String model_name) {
        this.API_KEY = API_KEY;
        this.API_BASE_URL = API_BASE_URL;
        this.API_PATH = API_PATH;
        this.MODEL_IDENTIFIER = model_name;
    }

    public interface ResponseCallback {
        void onResponse(String content);
        void onFailure(String error);
    }

    public void makeRequest(String userMessage, ResponseCallback cb, CompletionMode mode) {
        OkHttpClient client = new OkHttpClient.Builder().writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS).build();

        // Create the JSON body for the request
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", MODEL_IDENTIFIER);
            JSONArray messages = makeMessagesArray(userMessage, mode);
            jsonBody.put("messages", messages);

            JSONObject responseFormat = new JSONObject();
            responseFormat.put("type", "json_schema");

            // Create the json_schema JSONObject
            JSONObject jsonSchema = new JSONObject();
            jsonSchema.put("name", "message_enhancer");

            // Create the schema JSONObject
            JSONObject schema = new JSONObject();
            schema.put("type", "object");

            // Create the properties JSONObject
            JSONObject properties = new JSONObject();

            // Create the enhanced_message property
            JSONObject enhancedMessage = new JSONObject();
            enhancedMessage.put("type", "string");

            // Add enhanced_message property to properties
            properties.put("enhanced_message", enhancedMessage);

            // Add properties and other keys to schema
            schema.put("properties", properties);
            schema.put("required", new JSONArray().put("enhanced_message"));
            schema.put("additionalProperties", false);

            // Add schema to jsonSchema
            jsonSchema.put("schema", schema);

            // Add strict flag to jsonSchema
            jsonSchema.put("strict", true);

            // Add jsonSchema to responseFormat
            responseFormat.put("json_schema", jsonSchema);

            jsonBody.put("response_format", responseFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the request
        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));
        String API_FULL_URL = API_BASE_URL + API_PATH;

        Log.d(TAG, "makeRequest: " + API_FULL_URL);
        Request request = new Request.Builder()
                .url(API_FULL_URL)
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36")
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
                    Log.d(TAG, "onResponse: " + responseBody);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        JSONObject choice = choices.getJSONObject(0);
                        JSONObject message = choice.getJSONObject("message");
                        String content = message.getString("content");

                        try {
                            JSONObject structuredResponse = new JSONObject(content);
                            String enhancedMessage = structuredResponse.getString("enhanced_message");
                            new Handler(Looper.getMainLooper()).post(() -> cb.onResponse(enhancedMessage));
                        } catch (Exception e) {
                            new Handler(Looper.getMainLooper()).post(() -> cb.onResponse(content));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(() -> cb.onFailure(e.getMessage()));
                    }
                } else {
                    String errorMessage = "Request failed: " + response.code() + " - " + response.message();
                    Log.d(TAG, "onResponse: "+errorMessage);
                    new Handler(Looper.getMainLooper()).post(() -> cb.onFailure(errorMessage));
                }
            }
        });
    }

    private static @NonNull JSONArray makeMessagesArray(String userMessageString, CompletionMode mode) throws JSONException {
        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", getSystemPrompt(mode));
        messages.put(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", userMessageString);
        messages.put(userMessage);
        return messages;
    }
}

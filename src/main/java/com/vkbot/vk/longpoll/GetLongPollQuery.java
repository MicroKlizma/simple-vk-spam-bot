package com.vkbot.vk.longpoll;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.events.longpoll.LongPollQueryBuilder;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.exceptions.LongPollServerKeyExpiredException;
import com.vk.api.sdk.exceptions.LongPollServerTsException;
import com.vk.api.sdk.objects.callback.longpoll.GetLongPollEventsActInfo;
import com.vk.api.sdk.objects.callback.longpoll.responses.GetLongPollEventsResponse;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class GetLongPollQuery extends LongPollQueryBuilder<GetLongPollQuery, GetLongPollResponse> {

    private static final int INCORRECT_TS_VALUE_ERROR_CODE = 1;
    private static final int TOKEN_EXPIRED_ERROR_CODE = 2;

    private static final String FAILED_CODE = "failed";


    public GetLongPollQuery(VkApiClient client, String url, String key, String ts) {
        super(client, url, GetLongPollResponse.class);
        act(GetLongPollEventsActInfo.CHECK);
        key(key);
        ts(ts);
    }
    public GetLongPollQuery version(Integer value) {
        return unsafeParam("version", value);
    }
    public GetLongPollQuery mode(Integer value) {
        return unsafeParam("mode", value);
    }

    public GetLongPollQuery waitTime(Integer value) {
        return unsafeParam("wait", value);
    }

    protected GetLongPollQuery key(String value) {
        return unsafeParam("key", value);
    }

    protected GetLongPollQuery ts(String value) {
        return unsafeParam("ts", value);
    }

    protected GetLongPollQuery act(GetLongPollEventsActInfo actInfo) {
        return unsafeParam("act", actInfo.getValue());
    }

    @Override
    protected GetLongPollQuery getThis() {
        return this;
    }

    @Override
    protected List<String> essentialKeys() {
        return Arrays.asList("act", "key", "ts");
    }

    @Override
    public GetLongPollResponse execute() throws ApiException, ClientException {
        String textResponse = executeAsString();

        JsonReader jsonReader = new JsonReader(new StringReader(textResponse));
        JsonObject json = (JsonObject) new JsonParser().parse(jsonReader);

        if (json.has(FAILED_CODE)) {
            JsonPrimitive failedParam = json.getAsJsonPrimitive(FAILED_CODE);
            int code = failedParam.getAsInt();
            switch (code) {
                case INCORRECT_TS_VALUE_ERROR_CODE:
                    int ts = json.getAsJsonPrimitive("ts").getAsInt();
                    throw new LongPollServerTsException("\'ts\' value is incorrect, minimal value is 1, maximal value is " + ts);
                case TOKEN_EXPIRED_ERROR_CODE:
                    throw new LongPollServerKeyExpiredException("Try to generate a new key.");
                default:
                    throw new ClientException("Unknown LongPollServer exception, something went wrong.");
            }
        }

        try {
//            Type listType = new TypeToken<List<Object>>(){}.getType();
//            List<Object> list = getGson().fromJson(json, listType);
//            JsonArray a = new JsonArray();
//            a.
//            GetLongPollResponse biba = getGson().fromJson(json, GetLongPollResponse.class);
//            List<Flag> posts = getGson().fromJson(json, listType);
//            biba.setUpdates(posts);
            return getGson().fromJson(json, GetLongPollResponse.class);
        } catch (JsonSyntaxException e) {
            throw new ClientException("Can't parse json response");
        }
    }
}

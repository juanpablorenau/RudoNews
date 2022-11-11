package com.example.rudonews.api;

import static com.example.rudonews.api.Config.HTTP_CLIENT_AUTHORIZATION;
import static com.example.rudonews.api.Config.TYPE_ITEM_AUTHORIZATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rudonews.App;

import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class AccessTokenAuthenticator implements Authenticator {

    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        final String accessToken = App.preferences.getAccessToken();
        if (!isRequestWithAccessToken(response) || accessToken == null) {
            return null;
        }
        synchronized (this) {
            final String newAccessToken = App.preferences.getAccessToken();
            // Access token is refreshed in another thread.
            if (!accessToken.equals(newAccessToken)) {
                return newRequestWithAccessToken(response.request(), newAccessToken);
            }
            // Need to refresh an access token
            Call<Token> call = new RetrofitClient().refreshToken();

            retrofit2.Response<Token> res = call.execute();
            Token data = res.body();
            if (data != null) {
                final String updatedAccessToken = data.getAccess_token();
                final String updatedRefreshToken = data.getRefresh_token();
                App.preferences.setAccessToken(updatedAccessToken);
                App.preferences.setRefreshToken(updatedRefreshToken);
                return newRequestWithAccessToken(response.request(), updatedAccessToken);
            } else {
                return newRequestWithAccessToken(response.request(), App.preferences.getAccessToken());
            }
        }
    }

    private boolean isRequestWithAccessToken(@NonNull Response response) {
        String header = response.request().header(TYPE_ITEM_AUTHORIZATION);
        return header != null && header.startsWith(HTTP_CLIENT_AUTHORIZATION);
    }

    @NonNull
    private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .header(TYPE_ITEM_AUTHORIZATION, HTTP_CLIENT_AUTHORIZATION + " " + accessToken)
                .build();
    }
}
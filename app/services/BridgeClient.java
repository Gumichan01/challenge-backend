package services;

import com.typesafe.config.Config;
import models.AuthenticateResponse;
import models.GetAccountsResponse;
import play.libs.Json;
import play.libs.ws.WSClient;

import javax.inject.Inject;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;

import static play.mvc.Http.Status.OK;

/**
 * Bridge is Bankin's SaaS API. This service is where the calls to the API should be implemented.
 *
 * The "doSomething" method doesn't actually do anything yet and needs to be modified to fit the exercice's needs.
 */
public class BridgeClient {

    private final WSClient wsClient;
    private final String baseUrl;
    private final String apiVersion;
    private final String apiClientId;
    private final String apiClientSecret;

    // these are hardcoded for simplicity's sake
    private static final String USER_EMAIL = "user5@mail.com";
    private static final String USER_PASSWORD = "a!Strongp#assword1";

    @Inject
    public BridgeClient(WSClient wsClient, Config config) {
        this.wsClient = wsClient;
        this.baseUrl = config.getString("bankin.api.baseUrl");
        this.apiVersion = config.getString("bankin.api.version");
        this.apiClientId = config.getString("bankin.api.app.clientId");
        this.apiClientSecret = config.getString("bankin.api.app.clientSecret");
    }

    /**
     * This method is "complete" and doesn't need editing, except if you feel some things could be improved (there
     * is no trap)
     */
    private Optional<AuthenticateResponse> authenticateUser(String email, String password) {
        return wsClient.url(baseUrl + "/authenticate")
                .addHeader("Bankin-Version", apiVersion)
                .addQueryParameter("client_id", apiClientId)
                .addQueryParameter("client_secret", apiClientSecret)
                .addQueryParameter("email", email)
                .addQueryParameter("password", password)
                .post("")
                .thenApply(response -> {
                    if (response.getStatus() == OK) {
                        return Optional.of(Json.fromJson(response.asJson(), AuthenticateResponse.class));
                    }
                    return Optional.<AuthenticateResponse>empty();
                })
                .toCompletableFuture()
                .join();
    }

    public double retrieveSumOfAllAccounts() {
        Optional<AuthenticateResponse> maybeAccessToken = authenticateUser(USER_EMAIL, USER_PASSWORD);

        if (maybeAccessToken.isPresent()) {
            return wsClient.url(baseUrl + "/accounts")
                    .addHeader("Bankin-Version", apiVersion)
                    .addHeader("Authorization", "Bearer " + maybeAccessToken.get().accessToken)
                    .addQueryParameter("client_id", apiClientId)
                    .addQueryParameter("client_secret", apiClientSecret)
                    .get()
                    .thenApply(response -> {
                        GetAccountsResponse accountsResponse = Json.fromJson(response.asJson(), GetAccountsResponse.class);
                        if (accountsResponse != null && accountsResponse.accounts != null) {
                            return accountsResponse.accounts.stream()
                                    .filter(a -> a!= null && a.balance != null)
                                    .map(a -> a.balance)
                                    .reduce(0.0, Double::sum);
                        } else {
                            return 0.0;
                        }
                    })
                    .toCompletableFuture()
                    .join();
        } else {
            return 0.0;
        }
    }
}

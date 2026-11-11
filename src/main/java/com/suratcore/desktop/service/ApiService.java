package com.suratcore.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suratcore.desktop.model.*;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service to handle HTTP communication with Spring Boot REST API using OkHttp.
 */
public class ApiService {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public ApiService() {
        this.objectMapper = new ObjectMapper();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    private String getFullUrl(String endpoint) {
        String baseUrl = SessionManager.getInstance().getBaseUrl();
        if (endpoint.startsWith("/")) {
            return baseUrl + endpoint;
        }
        return baseUrl + "/" + endpoint;
    }

    private Request.Builder newAuthenticatedRequestBuilder(String endpoint) {
        Request.Builder builder = new Request.Builder().url(getFullUrl(endpoint));
        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.trim().isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
        return builder;
    }

    /**
     * Parse error response to extract server message if available.
     */
    private IOException parseHttpError(Response response, String responseBody) {
        String msg = "Error " + response.code() + ": " + response.message();
        if (responseBody != null && !responseBody.trim().isEmpty()) {
            try {
                JsonNode node = objectMapper.readTree(responseBody);
                if (node.has("message") && !node.get("message").asText().isEmpty()) {
                    msg = node.get("message").asText();
                } else if (node.has("error") && !node.get("error").asText().isEmpty()) {
                    msg = node.get("error").asText();
                }
            } catch (Exception ignored) {
                msg = responseBody;
            }
        }
        return new IOException(msg);
    }

    /**
     * Authenticate user with username and password.
     */
    public CompletableFuture<AuthResponse> loginAsync(String username, String password) {
        CompletableFuture<AuthResponse> future = new CompletableFuture<>();
        try {
            LoginRequest req = new LoginRequest(username, password);
            String jsonPayload = objectMapper.writeValueAsString(req);
            RequestBody body = RequestBody.create(jsonPayload, JSON);

            Request request = new Request.Builder()
                    .url(getFullUrl("/api/auth/login"))
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        String bodyStr = responseBody != null ? responseBody.string() : "";
                        if (!response.isSuccessful()) {
                            future.completeExceptionally(parseHttpError(response, bodyStr));
                        } else {
                            AuthResponse auth = objectMapper.readValue(bodyStr, AuthResponse.class);
                            SessionManager.getInstance().setSession(auth);
                            future.complete(auth);
                        }
                    } catch (Exception ex) {
                        future.completeExceptionally(ex);
                    }
                }
            });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    /**
     * Get list of all divisions.
     */
    public CompletableFuture<List<Division>> getDivisionsAsync() {
        CompletableFuture<List<Division>> future = new CompletableFuture<>();
        Request request = newAuthenticatedRequestBuilder("/api/divisions").get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String bodyStr = responseBody != null ? responseBody.string() : "";
                    if (!response.isSuccessful()) {
                        future.completeExceptionally(parseHttpError(response, bodyStr));
                    } else {
                        List<Division> list = objectMapper.readValue(bodyStr, new TypeReference<List<Division>>() {});
                        future.complete(list);
                    }
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }

    /**
     * Get list of all Registrasi Surat.
     */
    public CompletableFuture<List<RegistrasiSurat>> getRegistrasiSuratListAsync() {
        CompletableFuture<List<RegistrasiSurat>> future = new CompletableFuture<>();
        Request request = newAuthenticatedRequestBuilder("/api/registrasi-surat").get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String bodyStr = responseBody != null ? responseBody.string() : "";
                    if (!response.isSuccessful()) {
                        future.completeExceptionally(parseHttpError(response, bodyStr));
                    } else {
                        List<RegistrasiSurat> list = objectMapper.readValue(bodyStr, new TypeReference<List<RegistrasiSurat>>() {});
                        future.complete(list);
                    }
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }

    /**
     * Create/generate new Registrasi Surat.
     */
    public CompletableFuture<RegistrasiSurat> createRegistrasiSuratAsync(RegistrasiSuratRequest payload) {
        CompletableFuture<RegistrasiSurat> future = new CompletableFuture<>();
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            RequestBody body = RequestBody.create(jsonPayload, JSON);

            Request request = newAuthenticatedRequestBuilder("/api/registrasi-surat")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        String bodyStr = responseBody != null ? responseBody.string() : "";
                        if (!response.isSuccessful()) {
                            future.completeExceptionally(parseHttpError(response, bodyStr));
                        } else {
                            RegistrasiSurat result = objectMapper.readValue(bodyStr, RegistrasiSurat.class);
                            future.complete(result);
                        }
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }
            });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    /**
     * Update existing Registrasi Surat.
     */
    public CompletableFuture<RegistrasiSurat> updateRegistrasiSuratAsync(Long id, RegistrasiSuratRequest payload) {
        CompletableFuture<RegistrasiSurat> future = new CompletableFuture<>();
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            RequestBody body = RequestBody.create(jsonPayload, JSON);

            Request request = newAuthenticatedRequestBuilder("/api/registrasi-surat/" + id)
                    .put(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        String bodyStr = responseBody != null ? responseBody.string() : "";
                        if (!response.isSuccessful()) {
                            future.completeExceptionally(parseHttpError(response, bodyStr));
                        } else {
                            RegistrasiSurat result = objectMapper.readValue(bodyStr, RegistrasiSurat.class);
                            future.complete(result);
                        }
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }
            });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    /**
     * Delete Registrasi Surat by ID.
     */
    public CompletableFuture<Void> deleteRegistrasiSuratAsync(Long id) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Request request = newAuthenticatedRequestBuilder("/api/registrasi-surat/" + id)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String bodyStr = responseBody != null ? responseBody.string() : "";
                    if (!response.isSuccessful()) {
                        future.completeExceptionally(parseHttpError(response, bodyStr));
                    } else {
                        future.complete(null);
                    }
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}

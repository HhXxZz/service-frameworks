package base.service.frameworks.utils;

import base.service.frameworks.vo.HTTPCommonResult;
import base.service.frameworks.vo.HTTPCommonResultType;
import base.service.frameworks.vo.HTTPCommonSimpleResult;
import base.service.frameworks.vo.HTTPResultType;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Created by someone on 2017-09-12.
 *
 * </pre>
 */
@SuppressWarnings("unused")
public enum HTTPUtil {
    INSTANCE;
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(HTTPUtil.class);

    // ===========================================================
    // Fields
    // ===========================================================
    final private OkHttpClient                            mClient;
    final private OkHttpClient                            mTrustEverythingClient;
    private final ConcurrentHashMap<String, List<Cookie>> mCookieStore = new ConcurrentHashMap<>();

    // ===========================================================
    // Constructors
    // ===========================================================
    HTTPUtil() {
        this.mClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(32, 5, TimeUnit.MINUTES))
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                        mCookieStore.put(url.host(), cookies);
                    }

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                        List<Cookie> cookies = mCookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                })
                .build();

        final SSLContext sslContext;
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                   String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                   String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
            System.out.println(String.format("{} [WARN] %d HTTPUtil Build trust every thing client failed. All request will check authentication.",
                    DateUtil.getDateTimeString(), Thread.currentThread().getId()));
            this.mTrustEverythingClient = null;
            return;
        }
        this.mTrustEverythingClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> true)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(32, 5, TimeUnit.MINUTES))
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                        mCookieStore.put(url.host(), cookies);
                    }

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                        List<Cookie> cookies = mCookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                })
                .build();
    }

    // ===========================================================
    // ===========================================================

    public ArrayList<String> getCookieHosts() {
        return Collections.list(mCookieStore.keys());
    }

    public List<Cookie> getCookies(String pHost){
        return mCookieStore.get(pHost);
    }

    public void setCookies(String pHost, List<Cookie> pCookies){
        mCookieStore.put(pHost, pCookies);
    }

    public boolean hasCookies(String pHost){
        return mCookieStore.getOrDefault(pHost, new ArrayList<>()).size() > 0;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    public String execute(Request pRequest) {
        return execute(pRequest, false, false);
    }

    public String execute(Request pRequest, boolean pTrustEverything, boolean pIgnoreHTTPResultCode) {
        if (pRequest != null) {
            long         startTime = System.nanoTime();
            OkHttpClient client    = pTrustEverything && this.mTrustEverythingClient != null ? this.mTrustEverythingClient : this.mClient;
            try (Response response = client.newCall(pRequest).execute()) {
                if (!pIgnoreHTTPResultCode && !response.isSuccessful()) {
                    LOG.error("FAIL > Request URL[ {} ], response [ {} ]", pRequest.toString(), response.toString());
                    response.close();
                } else {
                    if (response.body() != null) {
                        return response.body().string();
                    }
                    return "";
                }
            } catch (Exception e) {
                long endTime = System.nanoTime();
                if(e instanceof java.io.EOFException){
                    LOG.error("ERROR > Request URL[ {} ] Cost[%d] fail [EOF response]", pRequest.toString(), endTime - startTime);
                }else {
                    String error = e.getMessage();
                    if (error.contains("timeout")) {
                        LOG.error("ERROR > Request URL[ {} ] Cost[%d] fail [timeout]", pRequest.toString(), endTime - startTime);
                    } else if (error.contains("Read timeout")) {
                        LOG.error("ERROR > Request URL[ {} ] Cost[%d] fail [Read timeout]", pRequest.toString(), endTime - startTime);
                    } else if (error.contains("Read timed out")) {
                        LOG.error("ERROR > Request URL[ {} ] Cost[%d] fail [Read timed out]", pRequest.toString(), endTime - startTime);
                    } else if (error.contains("Failed to connect to")) {
                        LOG.error("ERROR > Request URL[ {} ] Cost[%d] fail [Failed to connect to]", pRequest.toString(), endTime - startTime);
                    } else {
                        LOG.error("ERROR > Request URL[ {} ] Cost[%d] fail [{}]", pRequest.toString(), endTime - startTime, e.getMessage());
                    }
                }
            }
        }
        return "";
    }

    public <T> T executeToObject(Request pRequest, Class<T> pClass) {
        return executeToObject(pRequest, pClass, false, false);
    }

    public <T> T executeToObject(Request pRequest, Class<T> pClass, boolean pPrintResponseBody) {
        return executeToObject(pRequest, pClass, pPrintResponseBody, false);
    }

    public <T> T executeToObject(Request pRequest, Class<T> pClass, boolean pPrintResponseBody, boolean pTrustEverything) {
        return executeToObject(pRequest, pClass, pPrintResponseBody, false, false);
    }

    public <T> T executeToObject(Request pRequest, Class<T> pClass, boolean pPrintResponseBody, boolean pTrustEverything, boolean pIgnoreHTTPResultCode) {
        if (pRequest != null) {
            long         startTime = System.nanoTime();
            OkHttpClient client    = pTrustEverything && this.mTrustEverythingClient != null ? this.mTrustEverythingClient : this.mClient;
            try (Response response = client.newCall(pRequest).execute()) {
                if (!pIgnoreHTTPResultCode && !response.isSuccessful()) {
                    LOG.error("FAIL > Request URL[ {} ], response [ {} ]", pRequest.toString(), response.toString());
                    response.close();
                } else {
                    if (response.body() != null) {
                        try {
                            String content = response.body().string();
                            if (pPrintResponseBody) {
                                LOG.debug("SUCCESS > Request URL[ {} ] return {}", pRequest.toString(), content);
                            }
                            return GsonUtil.fromJson(content, pClass);
                        } catch (IOException e) {
                            LOG.error("ERROR > Request URL[ {} ] return {}", pRequest.toString(), response.body().string(),e);
                        }
                    }
                }
            } catch (Exception e) {
                long endTime = System.nanoTime();
                LOG.error("ERROR > Request URL[ {} ] Cost[{}]", pRequest.toString(), endTime - startTime,e);
            }
        }
        return null;
    }

    public HTTPCommonSimpleResult executeToSimpleResult(Request pRequest) {
        return executeToSimpleResult(pRequest, false, false, false);
    }

    public HTTPCommonSimpleResult executeToSimpleResult(Request pRequest, boolean pPrintResponseBody) {
        return executeToSimpleResult(pRequest, pPrintResponseBody, false, false);
    }

    public HTTPCommonSimpleResult executeToSimpleResult(Request pRequest, boolean pPrintResponseBody, boolean pTrustEverything, boolean pIgnoreHTTPResultCode) {
        if (pRequest != null) {
            long         startTime = System.nanoTime();
            OkHttpClient client    = pTrustEverything && this.mTrustEverythingClient != null ? this.mTrustEverythingClient : this.mClient;
            try (Response response = client.newCall(pRequest).execute()) {
                if (!pIgnoreHTTPResultCode && !response.isSuccessful()) {
                    LOG.error("FAIL > Request URL[ {} ], response [ {} ]", pRequest.toString(), response.toString());
                    response.close();
                } else {
                    if (response.body() != null) {
                        try {
                            String content = response.body().string();
                            if (pPrintResponseBody) {
                                LOG.debug("SUCCESS > Request URL[ {} ] return {}", pRequest.toString(), content);
                            }
                            return GsonUtil.fromJson(content, HTTPCommonSimpleResult.class);
                        } catch (IOException e) {
                            LOG.error("ERROR > Request URL[ {} ] return {}", pRequest.toString(), response.body().string(),e);
                        }
                    }
                }
            } catch (Exception e) {
                long endTime = System.nanoTime();
                LOG.error( "ERROR > Request URL[ {} ] Cost[{}]", pRequest.toString(), endTime - startTime,e);
            }
        }
        return null;
    }

    public <T> HTTPCommonResult<T> executeToResult(Request pRequest, Class<T> pClass) {
        return executeToResult(pRequest, pClass, false, false, false, false);
    }

    public <T> HTTPCommonResult<T> executeToResult(Request pRequest, Class<T> pClass, boolean pEnableExpose) {
        return executeToResult(pRequest, pClass, false, pEnableExpose, false, false);
    }

    public <T> HTTPCommonResult<T> executeToResult(Request pRequest, Class<T> pClass, boolean pPrintResponseBody, boolean pEnableExpose) {
        return executeToResult(pRequest, pClass, pPrintResponseBody, pEnableExpose, false, false);
    }

    public <T> HTTPCommonResult<T> executeToResult(Request pRequest, Class<T> pClass, boolean pPrintResponseBody, boolean pEnableExpose, boolean pTrustEverything, boolean pIgnoreHTTPResultCode) {
        if (pRequest != null) {
            long         startTime = System.nanoTime();
            OkHttpClient client    = pTrustEverything && this.mTrustEverythingClient != null ? this.mTrustEverythingClient : this.mClient;
            try (Response response = client.newCall(pRequest).execute()) {
                if (!pIgnoreHTTPResultCode && !response.isSuccessful()) {
                    LOG.error("FAIL > Request URL[ {} ], response [ {} ]", pRequest.toString(), response.toString());
                    response.close();
                } else {
                    if (response.body() != null) {
                        try {
                            String content = response.body().string();
                            if (pPrintResponseBody) {
                                LOG.debug("SUCCESS > Request URL[ {} ] return {}", pRequest.toString(), content);
                            }
                            return GsonUtil.fromJson(content, new HTTPCommonResultType(pClass), pEnableExpose);
                        } catch (IOException e) {
                            LOG.error( "ERROR > Request URL[ {} ] return {}", pRequest.toString(), response.body().string(),e);
                        }
                    }
                }
            } catch (Exception e) {
                long endTime = System.nanoTime();
                LOG.error( "ERROR > Request URL[ {} ] Cost[{}]", pRequest.toString(), endTime - startTime,e);
            }
        }
        return null;
    }

    public <T, C> T executeToResult(Request pRequest, Class<T> pRawType, Class<C> pClass) {
        return executeToResult(pRequest, pRawType, pClass, false, false, false, false);
    }
    public <T, C> T executeToResult(Request pRequest, Class<T> pRawType, Class<C> pClass, boolean pPrintResponseBody) {
        return executeToResult(pRequest, pRawType, pClass, pPrintResponseBody, false, false, false);
    }
    public <T, C> T executeToResult(Request pRequest, Class<T> pRawType, Class<C> pClass, boolean pPrintResponseBody, boolean pEnableExpose) {
        return executeToResult(pRequest, pRawType, pClass, pPrintResponseBody, pEnableExpose, false, false);
    }
    public <T, C> T executeToResult(Request pRequest, Class<T> pRawType, Class<C> pClass, boolean pPrintResponseBody, boolean pEnableExpose, boolean pTrustEverything) {
        return executeToResult(pRequest, pRawType, pClass, pPrintResponseBody, pEnableExpose, pTrustEverything, false);
    }
    public <T, C> T executeToResult(Request pRequest, Class<T> pRawType, Class<C> pClass, boolean pPrintResponseBody, boolean pEnableExpose, boolean pTrustEverything, boolean pIgnoreHTTPResultCode) {
        if (pRequest != null) {
            long         startTime = System.nanoTime();
            OkHttpClient client    = pTrustEverything && this.mTrustEverythingClient != null ? this.mTrustEverythingClient : this.mClient;
            try (Response response = client.newCall(pRequest).execute()) {
                if (!pIgnoreHTTPResultCode && !response.isSuccessful()) {
                    LOG.error("FAIL > Request URL[ {} ], response [ {} ]", pRequest.toString(), response.toString());
                    response.close();
                } else {
                    if (response.body() != null) {
                        try {
                            String content = response.body().string();
                            if (pPrintResponseBody) {
                                LOG.debug("SUCCESS > Request URL[ {} ] return {}", pRequest.toString(), content);
                            }
                            return GsonUtil.fromJson(content, new HTTPResultType<>(pRawType, pClass), pEnableExpose);
                        } catch (IOException e) {
                            LOG.error("ERROR > Request URL[ {} ] return {}", pRequest.toString(), response.body().string(),e);
                        }
                    }
                }
            } catch (Exception e) {
                long endTime = System.nanoTime();
                LOG.error( "ERROR > Request URL[ {} ] Cost[{}]", pRequest.toString(), endTime - startTime,e);
            }
        }
        return null;
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}

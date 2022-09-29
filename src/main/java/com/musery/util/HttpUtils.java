package com.musery.util;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class HttpUtils {

  public static final MediaType MULTIPART = MediaType.parse("multipart/form-data");
  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  public static final MediaType XML = MediaType.get("text/xml; charset=utf-8");
  public static final MediaType SOAP = MediaType.get("application/soap+xml");
  public static final MediaType WWW = MediaType.get("application/x-www-form-urlencoded");
  private static OkHttpClient client;

  static {
    try {
      VMTrustManager vmTrustManager = new VMTrustManager();
      SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
      sslContext.init(null, new TrustManager[] {vmTrustManager}, null);
      client =
          new OkHttpClient.Builder()
              .hostnameVerifier((host, session) -> host.equalsIgnoreCase(session.getPeerHost()))
              .sslSocketFactory(sslContext.getSocketFactory(), vmTrustManager)
              .addInterceptor(new PathParamReplace())
              .addInterceptor(new DynamicConnectTimeout())
              .build();
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      log.error("Generator OkHttpClient error", e);
    }
  }

  public static String doMethod(Request request) {
    try (Response response = client.newCall(request).execute()) {
      String result = response.body().string();
      log.info(
          "请求[{}]执行{}, 返回信息[{}]",
          request.toString(),
          response.isSuccessful() ? "成功" : "失败",
          result);
      if (response.isSuccessful()) {
        return result;
      } else {
        switch (response.code()) {
          case 401:
            throw new RuntimeException("认证失败, 请确认用户名密码是否正确");
          default:
            throw new RuntimeException("连接失败");
        }
      }
    } catch (IOException e) {
      log.error("请求[{}]执行异常", request.toString(), e);
      if (e instanceof ConnectException) {
        throw new RuntimeException("连接失败, 请确认IP/端口是否正确");
      } else if (e instanceof SocketTimeoutException) {
        throw new RuntimeException("连接超时, 请确认IP/端口是否正确");
      } else if (e instanceof NoRouteToHostException) {
        throw new RuntimeException("连接失败, 请确认IP/端口是否正确");
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  public static <T> T doMethod(Request request, TypeReference<T> valueTypeRef) {
    try (Response response = client.newCall(request).execute()) {
      if (response.isSuccessful()) {
        T result = JacksonUtils.deepToObject(response.body().byteStream(), valueTypeRef);
        log.info("请求[{}]执行成功, 返回信息[{}]", request.toString(), JacksonUtils.toString(result));
        return result;
      } else {
        log.info("请求[{}]执行失败, 返回信息[{}]", request.toString(), response.body().string());
        switch (response.code()) {
          case 401:
            throw new RuntimeException("认证失败, 请确认用户名密码是否正确");
          default:
            log.error("连接失败错误码[{}]", response.code());
            throw new RuntimeException("连接失败");
        }
      }
    } catch (IOException e) {
      log.error("请求[{}]执行异常", request.toString(), e);
      if (e instanceof ConnectException) {
        throw new RuntimeException("连接失败, 请确认IP/端口是否正确");
      } else if (e instanceof SocketTimeoutException) {
        throw new RuntimeException("连接超时, 请确认IP/端口是否正确");
      } else if (e instanceof NoRouteToHostException) {
        throw new RuntimeException("连接失败, 请确认IP/端口是否正确");
      } else if (e instanceof ProtocolException) {
        throw new RuntimeException("连接失败, 请确认协议是否正确");
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  public static Response doMethodWithCode(Request request) {
    try {
      return client.newCall(request).execute();
    } catch (IOException e) {
      log.error("请求[{}]执行异常", request.toString(), e);
      if (e instanceof ConnectException) {
        throw new RuntimeException("连接失败, 请确认IP/端口是否正确");
      } else if (e instanceof SocketTimeoutException) {
        throw new RuntimeException("连接超时, 请确认IP/端口是否正确");
      } else if (e instanceof NoRouteToHostException) {
        throw new RuntimeException("连接失败, 请确认IP/端口是否正确");
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * 异步回调
   *
   * @param request
   * @param consumer
   */
  public static void doAsynMethod(Request request, BiConsumer<Response, Exception> consumer) {
    client
        .newCall(request)
        .enqueue(
            new Callback() {
              @Override
              public void onFailure(Call call, IOException e) {
                consumer.accept(null, e);
              }

              @Override
              public void onResponse(Call call, Response response) throws IOException {
                consumer.accept(response, null);
              }
            });
  }

  static class VMTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {}

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  }

  /** 动态路径替换 */
  static class PathParamReplace implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
      Request origin = chain.request();
      HttpUrl.Builder builder = origin.url().newBuilder();
      List<String> segments = origin.url().pathSegments();
      for (int i = 0; i < segments.size(); i++) {
        String path = segments.get(i);
        if (path.startsWith("{") && path.endsWith("}")) {
          String key = path.substring(1, path.length() - 1);
          if (origin.url().queryParameter(key) != null) {
            builder.setPathSegment(i, origin.url().queryParameter(key));
            builder.removeAllQueryParameters(key);
          }
        }
      }
      return chain.proceed(origin.newBuilder().url(builder.build()).build());
    }
  }

  /** 动态超时时间设置 */
  static class DynamicConnectTimeout implements Interceptor {

    private static final int CONNECT_LONG_TIMEOUT = 20 * 60;
    private static final int CONNECT_SHORT_TIMEOUT = 10;

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
      Request request = chain.request();
      boolean timeout = !Objects.isNull(chain.request().header("timeout"));
      return chain
          .withConnectTimeout(
              timeout ? CONNECT_SHORT_TIMEOUT : CONNECT_LONG_TIMEOUT, TimeUnit.SECONDS)
          .withReadTimeout(timeout ? CONNECT_SHORT_TIMEOUT : CONNECT_LONG_TIMEOUT, TimeUnit.SECONDS)
          .withWriteTimeout(
              timeout ? CONNECT_SHORT_TIMEOUT : CONNECT_LONG_TIMEOUT, TimeUnit.SECONDS)
          .proceed(request);
    }
  }

  @Slf4j
  @Getter
  public static class BaseHttpClient {

    private String baseUrl;
    private Map<String, String> baseHeader;
    private Map<String, Object> baseUriVariables;
    private Map<String, Object> baseRequestBody;

    public BaseHttpClient(
        String url, Map<String, String> header, Map<String, Object> uriVariables) {
      this.baseUrl = url;
      this.baseHeader = header;
      this.baseUriVariables = uriVariables;
    }

    public BaseHttpClient(
        String url,
        Map<String, String> header,
        Map<String, Object> uriVariables,
        Map<String, Object> baseRequestBody) {
      this.baseUrl = url;
      this.baseHeader = header;
      this.baseUriVariables = uriVariables;
      this.baseRequestBody = baseRequestBody;
    }

    /**
     * POST 和 GET 区别字段通过 body + mediaType
     *
     * @param uri
     * @param body
     * @param mediaType
     * @param headers
     * @param tmpUriVariables
     * @return
     */
    public Request buildRequest(
        String uri,
        Object body,
        MediaType mediaType,
        Map<String, String> headers,
        Map<String, Object> tmpUriVariables) {
      if (null != mediaType) {
        if (mediaType.equals(HttpUtils.JSON)) {
          if (null == body) {
            body = new HashMap<>(1);
          }
        } else {
          if (null == body) {
            throw new RuntimeException("POST null with " + mediaType.toString());
          }
        }
        if (body instanceof String) {
          return buildRequest(
              uri, RequestBody.create((String) body, mediaType), headers, tmpUriVariables);
        } else {
          log.info("输出http请求参数：" + JacksonUtils.toString(body));
          if (mediaType.equals(HttpUtils.JSON)) {
            return buildRequest(
                uri,
                RequestBody.create(JacksonUtils.toString(body), mediaType),
                headers,
                tmpUriVariables);
          } else if (mediaType.equals(HttpUtils.WWW)) {
            // 进行key/value组装
            StringBuilder sb = new StringBuilder();
            if (null != baseRequestBody) {
              for (Map.Entry<String, Object> mapEntry : baseRequestBody.entrySet()) {
                if (null != mapEntry.getValue()) {
                  sb.append(mapEntry.getKey());
                  sb.append("=");
                  sb.append(mapEntry.getValue());
                  sb.append("&");
                }
              }
            }
            for (Map.Entry<String, Object> mapEntry : ((Map<String, Object>) body).entrySet()) {
              if (mapEntry.getValue() instanceof List) {
                sb.append(mapEntry.getKey() + "[]");
                sb.append("=");
                sb.append(String.join(",", (List) mapEntry.getValue()));
              } else {
                sb.append(mapEntry.getKey());
                sb.append("=");
                sb.append(mapEntry.getValue());
              }
              sb.append("&");
              log.info("输出参数：{}", sb.toString());
            }
            return buildRequest(
                uri, RequestBody.create(sb.toString(), mediaType), headers, tmpUriVariables);
          }
        }
      } else {
        if (null != body) {
          throw new RuntimeException("POST something without mediaType");
        }
      }
      return buildRequest(uri, null, headers, tmpUriVariables);
    }

    public Request buildRequest(
        String uri,
        RequestBody requestBody,
        Map<String, String> headers,
        Map<String, Object> tmpUriVariables) {
      Request.Builder builder = new Request.Builder().url(buildUrl(uri, tmpUriVariables));
      if (null != this.baseHeader) {
        for (Map.Entry<String, String> entry : this.baseHeader.entrySet()) {
          builder.addHeader(entry.getKey(), entry.getValue());
        }
      }
      if (null != headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          builder.addHeader(entry.getKey(), entry.getValue());
        }
      }
      if (null != requestBody) {
        builder.post(requestBody);
      }
      return builder.build();
    }

    private HttpUrl buildUrl(String uri, Map<String, Object> tmpUriVariables) {
      HttpUrl.Builder httpUrl = HttpUrl.parse(this.baseUrl + uri).newBuilder();

      if (null != this.baseUriVariables) {
        for (Map.Entry<String, Object> entry : this.baseUriVariables.entrySet()) {
          if (null != entry.getValue()) {
            httpUrl.addQueryParameter(entry.getKey(), entry.getValue().toString());
          }
        }
      }
      if (null != tmpUriVariables) {
        for (Map.Entry<String, Object> entry : tmpUriVariables.entrySet()) {
          if (null != entry.getValue()) {
            httpUrl.addQueryParameter(entry.getKey(), entry.getValue().toString());
          }
        }
      }
      return httpUrl.build();
    }
  }
}

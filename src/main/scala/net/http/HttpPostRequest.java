package net.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * HTTP POSTリクエストを送信するためのクラスです。
 */
public class HttpPostRequest {

    private final URL url;
    private final Map<String, String> headers;
    private String body;

    public HttpPostRequest(URL url) {
        this.url = url;
        this.headers = new HashMap<>();
        // デフォルトのヘッダーを設定
        this.headers.put("Content-Type", "application/x-www-form-urlencoded");
        this.headers.put("Charset", "UTF-8");
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String send() throws IOException {
        byte[] postData = body.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // ヘッダーを設定
        for (Map.Entry<String, String> header : headers.entrySet()) {
            conn.setRequestProperty(header.getKey(), header.getValue());
        }

        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }

        // レスポンスを読み込む
        return new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    // フォームデータを構築するユーティリティメソッド
    public static String buildFormData(Map<String, String> data) {
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            sj.add(entry.getKey() + "=" + entry.getValue());
        }
        return sj.toString();
    }
}

package net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * このクラスはHTTP GETリクエストを行うためのクラス。
 * @author rxxuzi
 */
public final class Crawler {

    // デフォルトのユーザーエージェント
    private static final String USER_AGENT = "Mozilla/5.0";

    // デフォルトのタイムアウト値 (ミリ秒)
    private static final int DEFAULT_TIMEOUT = 10000;

    /**
     * 指定されたURLからページの内容を取得する。タイムアウト値はデフォルト値が使用される。
     *
     * @param url ページのURL
     * @return ページの内容、またはエラーが発生した場合はnull
     */
    public static String getPageContent(URL url) throws IOException {
        return getPageContent(url, DEFAULT_TIMEOUT);
    }

    /**
     * 指定されたURLからページの内容を取得する。タイムアウト値は指定可能
     *
     * @param url     ページのURL
     * @param timeout タイムアウト値 (ミリ秒)
     * @return ページの内容、またはエラーが発生した場合はnull
     */
    public static String getPageContent(URL url, int timeout) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = setupHttpConnection(url, timeout);
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTPレスポンスコードがOKではありません: " + responseCode);
            }

            return readResponse(connection);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * HTTP接続の設定を行う。
     *
     * @param url     接続するURL
     * @param timeout タイムアウト値
     * @return 設定されたHttpURLConnectionオブジェクト
     * @throws IOException 接続エラーが発生した場合
     */
    private static HttpURLConnection setupHttpConnection(URL url, int timeout) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(timeout); // 接続タイムアウトを設定
        connection.setReadTimeout(timeout);    // 読み込みタイムアウトを設定
        connection.setRequestProperty("User-Agent", USER_AGENT); // ユーザーエージェントを設定
        return connection;
    }

    /**
     * HTTPレスポンスから内容を読み取る。
     *
     * @param connection HttpURLConnectionオブジェクト
     * @return HTTPレスポンスの内容
     * @throws IOException 読み取りエラーが発生した場合
     */
    private static String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString(); // HTTPレスポンスの内容を返す。
        }
    }
}



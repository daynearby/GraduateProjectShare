package com.young.share.network;

import android.content.Context;

import com.young.share.config.Contants;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 自定义https请求,通过自定义的defaultHttpClient实现，添加了请求头
 * Created by Nearby Yang on 2015-12-19.
 */
public class HttpsRequest {

    private static HttpClient client = null;

    /**
     * 发送post请求
     */
    public static synchronized String postRequest(String serverURL, JSONObject jsonObj) throws Exception {
        HttpPost post = new HttpPost(serverURL);
        // 发送数据类型
        post.addHeader("Content-Type", "application/json;charset=utf-8");
        // 接受数据类型
        post.addHeader("Accept", "application/json");
        post.addHeader(Contants.REST_APP_KEY, Contants.BMOB_APP_KEY);
        post.addHeader(Contants.REST_APP_REST_KEY, Contants.BMOB_APP_REST_KEY);
        // 请求报文,进行编码，防止中文转换成乱码
        StringEntity entity = new StringEntity(jsonObj.toString(), "UTF-8");
        post.setEntity(entity);
        // 参数
        HttpParams httpParameters = new BasicHttpParams();
        post.setParams(httpParameters);

        HttpResponse response = null;
        try {
            response =  initHttpClient(httpParameters).execute(post);
        } catch (UnknownHostException e) {
            throw new Exception("Unable to access " + e.getLocalizedMessage());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        int sCode = 0;/*状态码*/
        if ( response != null) {
            sCode = response.getStatusLine().getStatusCode();
        }
        if (sCode == HttpStatus.SC_OK) {
            return EntityUtils.toString(response.getEntity());
        } else
            throw new Exception("StatusCode is " + sCode);

    }


    /**
     * 发送post请求
     */
    public static synchronized String getRequest(String serverURL) throws Exception {
        HttpGet get = new HttpGet(serverURL);
        // 发送数据类型
        get.addHeader("Content-Type", "application/json;charset=utf-8");
        // 接受数据类型
        get.addHeader("Accept", "application/json");
        get.addHeader(Contants.REST_APP_KEY, Contants.BMOB_APP_KEY);
        get.addHeader(Contants.REST_APP_REST_KEY, Contants.BMOB_APP_REST_KEY);

        // 参数
        HttpParams httpParameters = new BasicHttpParams();
//        get.setParams(httpParameters);

        HttpResponse response = null;
        try {
            response = initHttpClient(httpParameters).execute(get);
        } catch (UnknownHostException e) {
            throw new Exception("Unable to access " + e.getLocalizedMessage());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        int sCode = 0;/*状态码*/
        if ( response != null) {
            sCode = response.getStatusLine().getStatusCode();
        }
        if (sCode == HttpStatus.SC_OK) {
            return EntityUtils.toString(response.getEntity());
        } else
            throw new Exception("StatusCode is " + sCode);

    }

    /**
     * 初始化HttpClient对象
     * 绕开ssl的验证
     *
     * @param params
     * @return
     */
    public static synchronized HttpClient initHttpClient(HttpParams params) {
        if (client == null) {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
                //允许所有主机的验证
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                // 设置http和https支持
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                registry.register(new Scheme("https", sf, 443));

                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

                return new DefaultHttpClient(ccm, params);
            } catch (Exception e) {
                e.printStackTrace();
                return new DefaultHttpClient(params);
            }
        }
        return client;
    }


    public static String requestHTTPSPage(Context context, String mUrl) {
        InputStream ins = null;
        String result = "";
        try {
            ins = context.getAssets().open("bmob.cer"); // 下载的证书放到项目中的assets目录中
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
            Certificate cer = cerFactory.generateCertificate(ins);
            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("trust", cer);

            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
            Scheme sch = new Scheme("https", socketFactory, 443);
            HttpClient mHttpClient = new DefaultHttpClient();
            mHttpClient.getConnectionManager().getSchemeRegistry().register(sch);

            BufferedReader reader = null;
            try {
                HttpGet request = new HttpGet();
                request.setURI(new URI(mUrl));
                HttpResponse response = mHttpClient.execute(request);
                if (response.getStatusLine().getStatusCode() != 200) {
                    request.abort();
                    return result;
                }

                reader = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent()));
                StringBuffer buffer = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                result = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ins != null)
                    ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static class SSLSocketFactoryImp extends SSLSocketFactory {
        final SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryImp(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws java.security.cert.CertificateException {
                }
            };
            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,
                    port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

}

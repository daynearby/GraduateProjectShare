package com.young.share.network;

import com.young.share.R;
import com.young.share.config.ApplicationConfig;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import java.io.InputStream;
import java.security.KeyStore;

/**
 * 自定义的httpsclient 进行https请求，添加bmob的请求头
 * Created by Nearby Yang on 2016-03-28.
 */
public class MyHttpsClient extends DefaultHttpClient {

    public MyHttpsClient() {
    }



    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        // 用我们自己定义的 SSLSocketFactory 在 ConnectionManager 中注册一个 443 端口
        registry.register(new Scheme("https", newSslSocketFactory(), 443));
        return new SingleClientConnManager(getParams(), registry);
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance("BKS");
            // 从资源文件中读取你自己创建的那个包含证书的 keystore 文件

            InputStream in =ApplicationConfig.getContext().getResources().openRawResource(R.raw.sharebmob); //这个参数改成你的 keystore 文件名
            try {
                // 用 keystore 的密码跟证书初始化 trusted
                trusted.load(in, "sdf554sdf4c".toCharArray());
            } finally {
                in.close();
            }
            // Pass the keystore to the SSLSocketFactory. The factory is responsible
            // for the verification of the server certificate.
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            // Hostname verification from certificat6
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER
            ); // 这个参数可以根据需要调整, 如果对主机名的验证不需要那么严谨, 可以将这个严谨程度调低些.
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

}

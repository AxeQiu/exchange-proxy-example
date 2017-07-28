package me.qiuyue.tech;

import java.net.*;
import javax.net.ssl.*;
import java.nio.charset.Charset;
import io.pingpang.simpleexchangeproxyserver.*;
import io.pingpang.simpleexchangeproxyserver.handler.*;
import io.pingpang.simpleexchangeproxyserver.dispatcher.*;
import mx4j.tools.adaptor.http.*;


/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) 
        throws Exception {

        // !!!!!!
        SSLContext sslCtx; // = SomeSSLContextFactory.getInstance() ....
        // !!!!!!

        Acceptor acceptor80 = new Acceptor(
                new InetSocketAddress(
                    InetAddress.getByName("localhost"), 80));

        Acceptor acceptor443 = new Acceptor(
                new InetSocketAddress(
                    InetAddress.getByName("localhost"), 443));
        acceptor443.setSslContext(sslCtx);

        String exchangeServer = "10.237.8.100";
        final Connector connector80 = new Connector(InetAddress.getByName(exchangeServer), 80);
        final Connector connector443 = new Connector(InetAddress.getByName(exchangeServer), 443);
        connector443.setSslContext(sslCtx);

        Router route80 = new Router() { 
            @Override
            public Connector getConnector(InetAddress addr) {
                return connector80;
            }
        };

        Router route443 = new Router() {
            @Override
            public Connector getConnector(InetAddress addr) {
                return connector443;
            }
        };

        ExchangeRequestLine owaLogonReq = new ExchangeRequestLine();
        owaLogonReq.setVerb("POST"); //here is case sensitive
        owaLogonReq.setPath("/owa/auth\\.owa"); //here can using regular express
        RequestHandle handle = new RequestHandle() {
            @Override
            public boolean handle(ExchangeSession session, ExchangeRequestObject requestObject)
                throws HttpException {

                byte[] bs = requestObject.getContent();
                String content = new String(bs, Charset.forName("ISO8859-1"));
                String newContent = content.replace("mypass", "");
                requestObject.getHeaders().put("Content-Length", String.valueOf(newContent.length()));
                requestObject.setContent(newContent.getBytes(Charset.forName("ISO8859-1")));
                //The returned boolean is represents whether or not the request should be BLOCKED 
                //true represents the blocked(and discards the request),
                //or false represents transmits the request to peer
                return false;
            }
        };

        connector80.registerRequestHandle(owaLogonReq, handle);
        connector443.registerRequestHandle(owaLogonReq, handle);

        final SimpleExchangeProxyServer p80 = new SimpleExchangeProxyServer();
        p80.setAcceptor(acceptor80);
        p80.setRoutable(new Routable(route80));

        final SimpleExchangeProxyServer p443 = new SimpleExchangeProxyServer();
        p443.setAcceptor(acceptor443);
        p443.setRoutable(new Routable(route443));

        new Thread() {
            @Override
            public void run() {
                p443.start();
            }
        }.start();
        p80.start();
    }
}

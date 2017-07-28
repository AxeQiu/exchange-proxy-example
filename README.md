# exchange-proxy-example

    This project is a demo of the SimpleExchangeProxyServer. https://github.com/AxeQiu/SimpleExchangeProxyServer


## How to use

1. You must use own SSL/TLS certificate to initialize the SSLContext, then replace the following line:

        
        SSLContext sslCtx; // = SomeSSLContextFactory.getInstance() ....

2. package project

        mvn clean package

3. run project

        java -jar .\target\exchange-proxy-example-0.1.jar

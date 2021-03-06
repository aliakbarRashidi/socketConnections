/*
 * Copyright (C) 2016 Nihas Kalam.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.websocket.client.connection.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.Proxy;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;

/**
 * A thin wrapper around the WebSocketClient class from the Java-WebSocket
 * library. The purpose of this class is to enable the WebSocketConnection class
 * to be unit tested by swapping out an instance of this wrapper for a mock
 * version.
 */
public class WebSocketClientWrapper extends WebSocketClient {

    private static final String WSS_SCHEME = "wss";
    private final WebSocketListener webSocketListener;

    public WebSocketClientWrapper(final URI uri, final Proxy proxy, final WebSocketListener webSocketListener, HashMap<String, String> header) throws SSLException {
//        super(uri);
        super(uri, new Draft_17(), header, 0);

        if (uri.getScheme().equals(WSS_SCHEME)) {
            try {
                SSLContext sslContext = null;
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null); // will use java's default
                // key and trust store which
                // is sufficient unless you
                // deal with self-signed
                // certificates

                final SSLSocketFactory factory = sslContext.getSocketFactory();// (SSLSocketFactory)
                // SSLSocketFactory.getDefault();
                setSocket(factory.createSocket());
            } catch (final IOException e) {
                throw new SSLException(e);
            } catch (final NoSuchAlgorithmException e) {
                throw new SSLException(e);
            } catch (final KeyManagementException e) {
                throw new SSLException(e);
            }
        }
        this.webSocketListener = webSocketListener;
        setProxy(proxy);
    }

    @Override
    public void onOpen(final ServerHandshake handshakedata) {
        webSocketListener.onOpen(handshakedata);
    }

    @Override
    public void onMessage(final String message) {
        webSocketListener.onMessage(message);
    }

    @Override
    public void onClose(final int code, final String reason, final boolean remote) {
        webSocketListener.onClose(code, reason, remote);
    }

    @Override
    public void onError(final Exception ex) {
        webSocketListener.onError(ex);
    }
}

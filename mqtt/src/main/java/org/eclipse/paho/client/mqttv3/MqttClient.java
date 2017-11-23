/*
 * Copyright (c) 2009, 2015 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 *    Ian Craggs - MQTT 3.1.1 support
 *    Ian Craggs - per subscription message handlers (bug 466579)
 *    Ian Craggs - ack control (bug 472172)
 */
package org.eclipse.paho.client.mqttv3;

import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.eclipse.paho.client.mqttv3.util.Debug;

import java.util.Properties;

import javax.net.SocketFactory;

/**
 * Lightweight client for talking to an MQTT server using methods that block
 * until an operation completes.
 * <p>
 * <p>
 * This class implements the blocking {@link IMqttClient} client interface where all
 * actions block until they have completed (or timed out).
 * This implementation is compatible with all Java SE runtimes from 1.4.2 and up.
 * </p>
 * <p>An application can connect to an MQTT server using:
 * <ul>
 * <li>A plain TCP socket
 * <li>An secure SSL/TLS socket
 * </ul>
 * </p>
 * <p>
 * To enable messages to be delivered even across network and client restarts
 * messages need to be safely stored until the message has been delivered at the requested
 * quality of service. A pluggable persistence mechanism is provided to store the messages.
 * </p>
 * <p>
 * By default {@link MqttDefaultFilePersistence} is used to store messages to a file.
 * If persistence is set to null then messages are stored in memory and hence can  be lost
 * if the client, Java runtime or device shuts down.
 * </p>
 * <p>
 * If connecting with {@link MqttConnectOptions#setCleanSession(boolean)} set to true it
 * is safe to use memory persistence as all state it cleared when a client disconnects. If
 * connecting with cleanSession set to false, to provide reliable message delivery
 * then a persistent message store should be used such as the default one. </p>
 * <p>
 * The message store interface is pluggable. Different stores can be used by implementing
 * the {@link MqttClientPersistence} interface and passing it to the clients constructor.
 * </p>
 *
 * @see IMqttClient
 */
@SuppressWarnings("unused")
public class MqttClient implements IMqttClient { //), DestinationProvider {
    //private static final String CLASS_NAME = MqttClient.class.getName();
    //private static final Logger log = LoggerFactory.getLogger(LoggerFactory.MQTT_CLIENT_MSG_CAT,CLASS_NAME);

    protected MqttAsyncClient mqttAsyncClient = null;  // Delegate implementation to MqttAsyncClient
    protected long timeToWait = -1;                // How long each method should wait for action to complete

    /**
     * Create an MqttClient that can be used to communicate with an MQTT server.
     * <p>
     * The address of a server can be specified on the constructor. Alternatively
     * a list containing one or more servers can be specified using the
     * {@link MqttConnectOptions#setServerURIs(String[]) setServerURIs} method
     * on MqttConnectOptions.
     * <p>
     * <p>
     * The <code>serverURI</code> parameter is typically used with the
     * the <code>clientId</code> parameter to form a key. The key
     * is used to store and reference messages while they are being delivered.
     * Hence the serverURI specified on the constructor must still be specified even if a list
     * of servers is specified on an MqttConnectOptions object.
     * The serverURI on the constructor must remain the same across
     * restarts of the client for delivery of messages to be maintained from a given
     * client to a given server or set of servers.
     * <p>
     * <p>
     * The address of the server to connect to is specified as a URI. Two types of
     * connection are supported <code>tcp://</code> for a TCP connection and
     * <code>ssl://</code> for a TCP connection secured by SSL/TLS.
     * For example:
     * <ul>
     * <li><code>tcp://localhost:1883</code></li>
     * <li><code>ssl://localhost:8883</code></li>
     * </ul>
     * If the port is not specified, it will
     * default to 1883 for <code>tcp://</code>" URIs, and 8883 for <code>ssl://</code> URIs.
     * </p>
     * <p>
     * <p>
     * A client identifier <code>clientId</code> must be specified and be less that 65535 characters.
     * It must be unique across all clients connecting to the same
     * server. The clientId is used by the server to store data related to the client,
     * hence it is important that the clientId remain the same when connecting to a server
     * if durable subscriptions or reliable messaging are required.
     * <p>
     * A convenience method is provided to generate a random client id that
     * should satisfy this criteria - {@link #generateClientId()}. As the client identifier
     * is used by the server to identify a client when it reconnects, the client must use the
     * same identifier between connections if durable subscriptions or reliable
     * delivery of messages is required.
     * </p>
     * <p>
     * In Java SE, SSL can be configured in one of several ways, which the
     * client will use in the following order:
     * </p>
     * <ul>
     * <li>
     * <strong>Supplying an <code>SSLSocketFactory</code></strong> - applications can
     * use {@link MqttConnectOptions#setSocketFactory(SocketFactory)} to supply
     * a factory with the appropriate SSL settings.
     * </li>
     * <li>
     * <strong>SSL Properties</strong> - applications can supply SSL settings as a
     * simple Java Properties using {@link MqttConnectOptions#setSSLProperties(Properties)}.
     * </li>
     * <li>
     * <strong>Use JVM settings</strong> - There are a number of standard
     * Java system properties that can be used to configure key and trust stores.
     * </li>
     * </ul>
     * <p>
     * <p>
     * In Java ME, the platform settings are used for SSL connections.
     * </p>
     * <p>
     * <p>
     * An instance of the default persistence mechanism {@link MqttDefaultFilePersistence}
     * is used by the client. To specify a different persistence mechanism or to turn
     * off persistence, use the {@link #MqttClient(String, String, MqttClientPersistence)}
     * constructor.
     *
     * @param serverURI the address of the server to connect to, specified as a URI. Can be overridden using
     *                  {@link MqttConnectOptions#setServerURIs(String[])}.
     * @param clientId  a client identifier that is unique on the server being connected to.
     * @throws IllegalArgumentException if the URI does not start with "tcp://", "ssl://" or "local://".
     * @throws IllegalArgumentException if the clientId is null or is greater than 65535 characters in length.
     * @throws MqttException            if any other problem was encountered.
     */
    public MqttClient(String serverURI, String clientId) throws MqttException {
        this(serverURI, clientId, new MqttDefaultFilePersistence());
    }

    /**
     * Create an MqttClient that can be used to communicate with an MQTT server.
     * <p>
     * The address of a server can be specified on the constructor. Alternatively
     * a list containing one or more servers can be specified using the
     * {@link MqttConnectOptions#setServerURIs(String[]) setServerURIs} method
     * on MqttConnectOptions.
     * <p>
     * <p>
     * The <code>serverURI</code> parameter is typically used with the
     * the <code>clientId</code> parameter to form a key. The key
     * is used to store and reference messages while they are being delivered.
     * Hence the serverURI specified on the constructor must still be specified even if a list
     * of servers is specified on an MqttConnectOptions object.
     * The serverURI on the constructor must remain the same across
     * restarts of the client for delivery of messages to be maintained from a given
     * client to a given server or set of servers.
     * <p>
     * <p>
     * The address of the server to connect to is specified as a URI. Two types of
     * connection are supported <code>tcp://</code> for a TCP connection and
     * <code>ssl://</code> for a TCP connection secured by SSL/TLS.
     * For example:
     * <ul>
     * <li><code>tcp://localhost:1883</code></li>
     * <li><code>ssl://localhost:8883</code></li>
     * </ul>
     * If the port is not specified, it will
     * default to 1883 for <code>tcp://</code>" URIs, and 8883 for <code>ssl://</code> URIs.
     * </p>
     * <p>
     * <p>
     * A client identifier <code>clientId</code> must be specified and be less that 65535 characters.
     * It must be unique across all clients connecting to the same
     * server. The clientId is used by the server to store data related to the client,
     * hence it is important that the clientId remain the same when connecting to a server
     * if durable subscriptions or reliable messaging are required.
     * <p>
     * A convenience method is provided to generate a random client id that
     * should satisfy this criteria - {@link #generateClientId()}. As the client identifier
     * is used by the server to identify a client when it reconnects, the client must use the
     * same identifier between connections if durable subscriptions or reliable
     * delivery of messages is required.
     * </p>
     * <p>
     * In Java SE, SSL can be configured in one of several ways, which the
     * client will use in the following order:
     * </p>
     * <ul>
     * <li>
     * <strong>Supplying an <code>SSLSocketFactory</code></strong> - applications can
     * use {@link MqttConnectOptions#setSocketFactory(SocketFactory)} to supply
     * a factory with the appropriate SSL settings.
     * </li>
     * <li>
     * <strong>SSL Properties</strong> - applications can supply SSL settings as a
     * simple Java Properties using {@link MqttConnectOptions#setSSLProperties(Properties)}.
     * </li>
     * <li>
     * <strong>Use JVM settings</strong> - There are a number of standard
     * Java system properties that can be used to configure key and trust stores.</li>
     * </ul>
     * <p>
     * <p>
     * In Java ME, the platform settings are used for SSL connections.
     * </p>
     * <p>
     * A persistence mechanism is used to enable reliable messaging.
     * For messages sent at qualities of service (QoS) 1 or 2 to be reliably delivered,
     * messages must be stored (on both the client and server) until the delivery of the message
     * is complete. If messages are not safely stored when being delivered then
     * a failure in the client or server can result in lost messages. A pluggable
     * persistence mechanism is supported via the {@link MqttClientPersistence}
     * interface. An implementer of this interface that safely stores messages
     * must be specified in order for delivery of messages to be reliable. In
     * addition {@link MqttConnectOptions#setCleanSession(boolean)} must be set
     * to false. In the event that only QoS 0 messages are sent or received or
     * cleanSession is set to true then a safe store is not needed.
     * </p>
     * <p>
     * An implementation of file-based persistence is provided in
     * class {@link MqttDefaultFilePersistence} which will work in all Java SE based
     * systems. If no persistence is needed, the persistence parameter
     * can be explicitly set to <code>null</code>.
     * </p>
     *
     * @param serverURI   the address of the server to connect to, specified as a URI. Can be overridden using
     *                    {@link MqttConnectOptions#setServerURIs(String[])}
     * @param clientId    a client identifier that is unique on the server being connected to
     * @param persistence the persistence class to use to store in-flight message.
     *                    If null then the default persistence mechanism is used
     * @throws IllegalArgumentException if the URI does not start with "tcp://", "ssl://" or "local://"
     * @throws IllegalArgumentException if the clientId is null or is greater than 65535 characters in length
     * @throws MqttException            if any other problem was encountered
     */
    public MqttClient(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException {
        mqttAsyncClient = new MqttAsyncClient(serverURI, clientId, persistence);
    }

    @Override
    public void connect() throws MqttException {
        this.connect(new MqttConnectOptions());
    }

    @Override
    public void connect(MqttConnectOptions options) throws MqttException {
        mqttAsyncClient.connect(options, null, null).waitForCompletion(getTimeToWait());
    }

    @Override
    public IMqttToken connectWithResult(MqttConnectOptions options) throws MqttException {
        IMqttToken tok = mqttAsyncClient.connect(options, null, null);
        tok.waitForCompletion(getTimeToWait());
        return tok;
    }

    @Override
    public void disconnect() throws MqttException {
        mqttAsyncClient.disconnect().waitForCompletion();
    }

    @Override
    public void disconnect(long quiesceTimeout) throws MqttException {
        mqttAsyncClient.disconnect(quiesceTimeout, null, null).waitForCompletion();
    }

    @Override
    public void disconnectForcibly() throws MqttException {
        mqttAsyncClient.disconnectForcibly();
    }

    @Override
    public void disconnectForcibly(long disconnectTimeout) throws MqttException {
        mqttAsyncClient.disconnectForcibly(disconnectTimeout);
    }

    @Override
    public void disconnectForcibly(long quiesceTimeout, long disconnectTimeout) throws MqttException {
        mqttAsyncClient.disconnectForcibly(quiesceTimeout, disconnectTimeout);
    }

    @Override
    public void subscribe(String topicFilter) throws MqttException {
        subscribe(new String[]{topicFilter}, new int[]{1});
    }

    @Override
    public void subscribe(String[] topicFilters) throws MqttException {
        int[] qos = new int[topicFilters.length];
        for (int i = 0; i < qos.length; i++) {
            qos[i] = 1;
        }
        subscribe(topicFilters, qos);
    }

    @Override
    public void subscribe(String topicFilter, int qos) throws MqttException {
        this.subscribe(new String[]{topicFilter}, new int[]{qos});
    }

    @Override
    public void subscribe(String[] topicFilters, int[] qos) throws MqttException {
        IMqttToken tok = mqttAsyncClient.subscribe(topicFilters, qos, null, null);
        tok.waitForCompletion(getTimeToWait());
        int[] grantedQos = tok.getGrantedQos();
        System.arraycopy(grantedQos, 0, qos, 0, grantedQos.length);
        if (grantedQos.length == 1 && qos[0] == 0x80) {
            throw new MqttException(MqttException.REASON_CODE_SUBSCRIBE_FAILED);
        }
    }

    @Override
    public void subscribe(String topicFilter, IMqttMessageListener messageListener) throws MqttException {
        subscribe(new String[]{topicFilter}, new int[]{1}, new IMqttMessageListener[]{messageListener});
    }

    @Override
    public void subscribe(String[] topicFilters, IMqttMessageListener[] messageListeners) throws MqttException {
        int[] qos = new int[topicFilters.length];
        for (int i = 0; i < qos.length; i++) {
            qos[i] = 1;
        }
        subscribe(topicFilters, qos, messageListeners);
    }

    @Override
    public void subscribe(String topicFilter, int qos, IMqttMessageListener messageListener) throws MqttException {
        subscribe(new String[]{topicFilter}, new int[]{qos}, new IMqttMessageListener[]{messageListener});
    }

    @Override
    public void subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) throws MqttException {
        subscribe(topicFilters, qos);
        // add message handlers to the list for this client
        for (int i = 0; i < topicFilters.length; ++i) {
            mqttAsyncClient.comms.setMessageListener(topicFilters[i], messageListeners[i]);
        }
    }

    @Override
    public void unsubscribe(String topicFilter) throws MqttException {
        unsubscribe(new String[]{topicFilter});
    }

    @Override
    public void unsubscribe(String[] topicFilters) throws MqttException {
        // message handlers removed in the async client unsubscribe below
        mqttAsyncClient.unsubscribe(topicFilters, null, null).waitForCompletion(getTimeToWait());
    }

    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws MqttException {
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);
        message.setRetained(retained);
        publish(topic, message);
    }

    public void publish(String topic, MqttMessage message) throws MqttException {
        mqttAsyncClient.publish(topic, message, null, null).waitForCompletion(getTimeToWait());
    }

    /**
     * Set the maximum time to wait for an action to complete.
     * <p>
     * Set the maximum time to wait for an action to complete before
     * returning control to the invoking application. Control is returned
     * when:
     * <ul>
     * <li>the action completes
     * <li>or when the timeout if exceeded
     * <li>or when the client is disconnect/shutdown
     * <ul>
     * The default value is -1 which means the action will not timeout.
     * In the event of a timeout the action carries on running in the
     * background until it completes. The timeout is used on methods that
     * block while the action is in progress.
     * </p>
     *
     * @param timeToWaitInMillis before the action times out.
     *                           A value or 0 or -1 will wait until the action finishes and not timeout.
     */
    public void setTimeToWait(long timeToWaitInMillis) throws IllegalArgumentException {
        if (timeToWaitInMillis < -1) {
            throw new IllegalArgumentException();
        }
        timeToWait = timeToWaitInMillis;
    }

    /**
     * Return the maximum time to wait for an action to complete.
     *
     * @see MqttClient#setTimeToWait(long)
     */
    public long getTimeToWait() {
        return timeToWait;
    }

    @Override
    public void close() throws MqttException {
        mqttAsyncClient.close();
    }

    @Override
    public String getClientId() {
        return mqttAsyncClient.getClientId();
    }

    @Override
    public IMqttDeliveryToken[] getPendingDeliveryTokens() {
        return mqttAsyncClient.getPendingDeliveryTokens();
    }

    @Override
    public String getServerURI() {
        return mqttAsyncClient.getServerURI();
    }

    /**
     * Returns the currently connected Server URI
     * Implemented due to: https://bugs.eclipse.org/bugs/show_bug.cgi?id=481097
     * <p>
     * Where getServerURI only returns the URI that was provided in
     * MqttAsyncClient's constructor, getCurrentServerURI returns the URI of the
     * Server that the client is currently connected to. This would be different in scenarios
     * where multiple server URIs have been provided to the MqttConnectOptions.
     *
     * @return the currently connected server URI
     */
    public String getCurrentServerURI() {
        return mqttAsyncClient.getCurrentServerURI();
    }

    @Override
    public MqttTopic getTopic(String topic) {
        return mqttAsyncClient.getTopic(topic);
    }

    @Override
    public boolean isConnected() {
        return mqttAsyncClient.isConnected();
    }

    @Override
    public void setCallback(MqttCallback callback) {
        mqttAsyncClient.setCallback(callback);
    }

    @Override
    public void setManualAcks(boolean manualAcks) {
        mqttAsyncClient.setManualAcks(manualAcks);
    }

    @Override
    public void messageArrivedComplete(int messageId, int qos) throws MqttException {
        mqttAsyncClient.messageArrivedComplete(messageId, qos);
    }

    /**
     * Returns a randomly generated client identifier based on the current user's login
     * name and the system time.
     * <p>
     * When cleanSession is set to false, an application must ensure it uses the
     * same client identifier when it reconnects to the server to resume state and maintain
     * assured message delivery.
     * </p>
     *
     * @return a generated client identifier
     * @see MqttConnectOptions#setCleanSession(boolean)
     */
    public static String generateClientId() {
        return MqttAsyncClient.generateClientId();
    }

    public void reconnect() throws MqttException {
        mqttAsyncClient.reconnect();
    }

    /**
     * Return a debug object that can be used to help solve problems.
     */
    public Debug getDebug() {
        return (mqttAsyncClient.getDebug());
    }

}

package com.horstmann.violet.client;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by CSingh on 4/21/2017.
 */
public class Publisher {
    private static final String BROKER_HOST = "tcp://35.185.234.194:%d";
    private static final int BROKER_PORT = 61616; 
    private static final String BROKER_URL = String.format(BROKER_HOST, BROKER_PORT); 
    private static final Boolean NON_TRANSACTED = false;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    
    public void start() throws JMSException {
        try { 
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);

            // Allows these packages to be deserialized
            connectionFactory.setTrustedPackages(new ArrayList<>(
                    Arrays.asList("java.lang,javax.security,java.util",
                            "org.apache.activemq", "fusesource.hawtbuf", "com.thoughtworks.xstream.mapper",
                            "com.horstmann.violet")));

            connection = connectionFactory.createConnection(); 
            connection.start(); 
            session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("VIOLET.TOPIC"); 
            messageProducer = session.createProducer(topic);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    } 
    
    public void sendCommand(Serializable command) throws JMSException, InterruptedException {
        Message msg = session.createObjectMessage(command);
        messageProducer.send(msg); 
    }
    
    public void closePublisherConnection() {
        if (connection != null) {
            try { 
                connection.close(); 
            } catch (JMSException e) { 
                System.out.println("Could not close an open publisher connection..."); 
            }
        }
    }
}
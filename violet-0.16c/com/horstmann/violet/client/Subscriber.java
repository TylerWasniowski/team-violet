package com.horstmann.violet.client;

import javax.jms.*;

import com.horstmann.violet.Command;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by CSingh on 4/21/2017.
 */
public class Subscriber {
    
    private static final String BROKER_HOST = "tcp://35.185.234.194:%d";
    private static final int BROKER_PORT = 61616; 
    private static final String BROKER_URL = String.format(BROKER_HOST, BROKER_PORT); 
    private static final Boolean NON_TRANSACTED = false;
    private Connection connection;
    private Session session;
    private MessageConsumer messageConsumer;
    private static TeamSequenceDiagramGraph tDiagram;
    
    public Subscriber(TeamSequenceDiagramGraph tDiagram) {
       Subscriber.tDiagram = tDiagram;
    }
    
    public void start() throws JMSException {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
            connection = connectionFactory.createConnection();
            connection.start();

            // Allows these packages to be deserialized
            connectionFactory.setTrustedPackages(new ArrayList<>(
                    Arrays.asList("java.lang,javax.security,java.util",
                            "org.apache.activemq", "fusesource.hawtbuf", "com.thoughtworks.xstream.mapper",
                            "com.horstmann.violet")));

            session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            messageConsumer = session.createConsumer(session.createTopic("VIOLET.TOPIC"));
            messageConsumer.setMessageListener(new TeamVioletMessageListener());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    private static class TeamVioletMessageListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            if(message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                try {
                    Object object = objectMessage.getObject();
                    if (object instanceof Command) {
                        tDiagram.executeCommand((Command) object);
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void closeSubscriberConnection() {
        if (connection != null) {
            try { 
                connection.close(); 
            } catch (JMSException e) { 
                System.out.println("Could not close an open subscriber connection..."); 
            }
        }
    }
}
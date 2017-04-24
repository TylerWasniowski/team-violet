package com.horstmann.violet.client;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;

/**
 * Created by CSingh on 4/21/2017.
 */
public class Subscriber {
    
    private static final String BROKER_HOST = "tcp://104.199.127.233:%d"; 
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
    
    public void start() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
            connection = connectionFactory.createConnection();
            connection.start();
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
            if(message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    tDiagram.executeCommand(textMessage.getText());
                    //System.out.println("Consumer received message: " + textMessage.getText());
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
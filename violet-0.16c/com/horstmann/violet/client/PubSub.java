package com.horstmann.violet.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import com.horstmann.violet.graphs.TeamDiagram;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;
import com.horstmann.violet.commands.Command;

public class PubSub {
    private static final String BROKER_HOST = "tcp://35.185.245.223:%d";
    private static final int BROKER_PORT = 61616; 
    private static final String BROKER_URL = String.format(BROKER_HOST, BROKER_PORT); 
    private static final Boolean NON_TRANSACTED = false;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    private MessageConsumer messageConsumer;
    public static Queue<ActiveMQObjectMessage> recievedMsgs = new LinkedList<>();
    private static TeamDiagram teamDiagram;

    public PubSub(TeamDiagram teamDiagram) {
        this.teamDiagram = teamDiagram;
    }
    
    public void start() throws JMSException {
        try {
            if(session != null) {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
                connectionFactory.setTrustAllPackages(true);
                connection = connectionFactory.createConnection();
                connection.start();
                session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
                messageConsumer.setMessageListener(new TeamVioletMessageListener());
            }
            Topic topic = session.createTopic("VIOLET.TOPIC"); 
            messageProducer = session.createProducer(topic);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
    private static class TeamVioletMessageListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            synchronized (this) {
                try {
                    Serializable obj;
                    ActiveMQObjectMessage mq = (ActiveMQObjectMessage) message;
                    obj = mq.getObject();
                    if (obj instanceof Command) {
                        recievedMsgs.add(mq);
                        Command command = (Command) obj;
                        if (!command.execute(teamDiagram))
                            System.out.println(command.getClass() + " failed");

                        teamDiagram.layout();
                        if (teamDiagram.getPanel() != null) {
                            teamDiagram.getPanel().revalidate();
                            teamDiagram.getPanel().repaint();
                        }
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized List<Command> receiveCommands() throws Exception {
        List<Command> lst = new ArrayList<>();
        Serializable obj;
        if(!recievedMsgs.isEmpty()) {
            ActiveMQObjectMessage mq = recievedMsgs.poll();
            obj = mq.getObject();
            if (obj instanceof Command) {
                lst.add((Command) obj);
            } else {
                throw new Exception("Unknown Message");
            }
        }
        return lst;
    }
    
    public void sendCommand(Command command) throws JMSException, InterruptedException {
        ObjectMessage msg = session.createObjectMessage();
        msg.setObject(command);
        messageProducer.send(msg); 
    }
    
    public Set getAavilableTopics() {
        return null;
    }
    
    public void closePubSubConnection() {
        if (connection != null) {
            try { 
                connection.close(); 
            } catch (JMSException e) { 
                System.out.println("Could not close an open PubSub connection..."); 
            }
        }
    }
}
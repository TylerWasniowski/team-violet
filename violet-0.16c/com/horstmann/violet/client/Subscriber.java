package com.horstmann.violet.client;

import javax.jms.*;
import com.horstmann.violet.commands.*;
import com.horstmann.violet.graphs.TeamDiagram;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;
import java.io.Serializable;


/**
 * Created by CSingh on 4/21/2017.
 */
public class Subscriber implements MessageListener {

    private static final String BROKER_HOST = "tcp://35.185.243.162:%d";
    private static final int BROKER_PORT = 61616;
    private static final String BROKER_URL = String.format(BROKER_HOST, BROKER_PORT);
    private static final Boolean NON_TRANSACTED = false;
    private Connection connection;
    private Session session;
    private MessageConsumer messageConsumer;
    private TeamDiagram teamDiagram;

    public Subscriber(TeamDiagram teamDiagram) {
       this.teamDiagram = teamDiagram;
    }

    public void start() throws JMSException {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
            connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            messageConsumer = session.createConsumer(session.createTopic("VIOLET.TOPIC"));
            messageConsumer.setMessageListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        synchronized (this) {
            try {
                Serializable obj;
                ActiveMQObjectMessage mq = (ActiveMQObjectMessage) message;
                obj = mq.getObject();
                if (obj instanceof Command) {
                    Command command = (Command) obj;
                    if (!command.execute(teamDiagram))
                        System.out.println(command.getClass() + " failed on graph with ID: " + teamDiagram.getGraphID());
                    // or command.execute(projectIDToTeamDiagram.get("Project 1"));

                    teamDiagram.layout();
                    if (teamDiagram.getJPanel() != null) {
                        teamDiagram.getJPanel().revalidate();
                        teamDiagram.getJPanel().repaint();
                    }
                }
            } catch (JMSException e) {
                e.printStackTrace();
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
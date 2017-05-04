package com.horstmann.violet.client;

import javax.jms.*;

import com.horstmann.violet.commands.*;
import com.horstmann.violet.graphs.TeamDiagram;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;
import com.horstmann.violet.graphs.TeamSequenceDiagramGraph;
import java.io.Serializable;
import java.util.*;
import java.util.Queue;

/**
 * Created by CSingh on 4/21/2017.
 */
public class Subscriber {

    private static final String BROKER_HOST = "tcp://35.185.245.223:%d";
    private static final int BROKER_PORT = 61616;
    private static final String BROKER_URL = String.format(BROKER_HOST, BROKER_PORT);
    private static final Boolean NON_TRANSACTED = false;
    private Connection connection;
    private Session session;
    private MessageConsumer messageConsumer;
    private static Map<String, TeamDiagram> projectIDToTeamDiagram;
    public static Queue<ActiveMQObjectMessage> recievedMsgs = new LinkedList<>();
    private static TeamDiagram teamDiagram;

    public Subscriber(TeamDiagram teamDiagram) {
       Subscriber.teamDiagram = teamDiagram;
    }

    public void start() throws JMSException {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
            connectionFactory.setTrustAllPackages(true);
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
                        // or command.execute(projectIDToTeamDiagram.get("Project 1"));

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
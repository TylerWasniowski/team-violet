package com.horstmann.violet.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.swing.*;

import com.horstmann.violet.graphs.TeamDiagram;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQObjectMessage;
import com.horstmann.violet.commands.Command;
import org.apache.activemq.command.ActiveMQTopic;
/**
 * Creates connection to server with ActiveMQ and 
 * handles the sending/receiving of commands between team members.
 */
public class PubSub implements MessageListener, Closeable, AutoCloseable {
    private static final String BROKER_HOST = "tcp://%s:%d";
    private static final int BROKER_PORT = 61616;
    private static final Boolean NON_TRANSACTED = false;
    private static final String RESOURCE_BUNDLE_NAME = "com.horstmann.violet.client.teamActiveMQ";
    private static ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
    private ActiveMQConnection connection;
    private Session session;
    private MessageConsumer messageConsumer;
    private MessageProducer messageProducer;
    private TeamDiagram teamDiagram;
    private String projectName;
    
    /**
     * PubSub constructor 
     * @param teamDiagram the diagram that commands will be executed on.
     * @param pName the project/topic name.
     */
    public PubSub(TeamDiagram teamDiagram, String pName) {
        this.teamDiagram = teamDiagram;
        this.projectName = pName;
    }
    
    /**
     * PubSub empty constructor 
     */
    public PubSub() {
    }

    /**
     * Starts the server and returns the unique client ID.
     * @return a unique client ID
     */
    public String start() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin",
                    String.format(BROKER_HOST, rb.getString("serverIP"), BROKER_PORT));
            connectionFactory.setTrustAllPackages(true);
            connection = (ActiveMQConnection) connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            messageConsumer = session.createConsumer(session.createTopic(projectName));
            messageProducer = session.createProducer(session.createTopic(projectName));
            messageConsumer.setMessageListener(this);
            return connection.getClientID();
        } catch (JMSException e) {
            try {
                teamDiagram.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                teamDiagram = null;
            }

            JOptionPane jOptionPane = new JOptionPane("Could not start connection with ActiveMQ." +
                    " Is ActiveMQ running on the server?", JOptionPane.WARNING_MESSAGE);
            JDialog jDialog = jOptionPane.createDialog("Error");
            jDialog.setAlwaysOnTop(true);
            jDialog.setVisible(true);
        }

        return null;
    }

    @Override
    public synchronized void onMessage(Message message) {
        try {
            Serializable obj;
            ActiveMQObjectMessage mq = (ActiveMQObjectMessage) message;
            obj = mq.getObject();
            if (obj instanceof Command) {
                Command command = (Command) obj;
                if (!command.execute(teamDiagram))
                    System.out.println(command.getClass() + " failed on graph with ID: " + teamDiagram.getClientID());
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
    /**
     * Creates a JMS message with the command object, then sends it to the server.
     * @param command the command to send to the server
     * @throws JMSException if problem with JMS
     * @throws InterruptedException thrown if thread is interrupted
     */
    public synchronized void sendCommand(Command command) throws JMSException, InterruptedException {
        ObjectMessage msg = session.createObjectMessage();
        msg.setObject(command);
        messageProducer.send(msg);
    }
    
    /**
     * fetches topics on server
     * @return array list of topic strings
     */
    public ArrayList<String> fetchTopics() throws JMSException {
        ArrayList<String> tops = new ArrayList<String>();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin",
                String.format(BROKER_HOST, rb.getString("serverIP"), BROKER_PORT));
        connectionFactory.setTrustAllPackages(true);
        connection = (ActiveMQConnection) connectionFactory.createConnection();
        connection.start();
        DestinationSource dSource = connection.getDestinationSource();
        Set<ActiveMQTopic> topics = dSource.getTopics();
        Iterator<ActiveMQTopic> itr = topics.iterator();
        while(itr.hasNext())
            tops.add(itr.next().getTopicName());
        
        return tops;
    }

    /**
     * Reloads IP from settings
     */
    public static void reloadIPFromSettings() {
        rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
    }

    /**
     * Tries to close the ActiveMQ connection
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

        connection = null;
    }
}
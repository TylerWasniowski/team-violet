package com.horstmann.violet.client;

import java.io.Closeable;
import java.io.IOException;
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
import javax.swing.*;

import com.horstmann.violet.graphs.TeamDiagram;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;
import com.horstmann.violet.commands.Command;

public class PubSub implements MessageListener, Closeable, AutoCloseable {
    private static final String BROKER_HOST = "tcp://35.185.243.162:%d";
    private static final int BROKER_PORT = 61616;
    private static final String BROKER_URL = String.format(BROKER_HOST, BROKER_PORT);
    private static final Boolean NON_TRANSACTED = false;
    private Connection connection;
    private Session session;
    private MessageConsumer messageConsumer;
    private MessageProducer messageProducer;
    private TeamDiagram teamDiagram;

    public PubSub(TeamDiagram teamDiagram) {
        this.teamDiagram = teamDiagram;
    }

    /**
     * Starts the server and returns the unique client ID.
     * @return a unique client ID
     */
    public String start() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", BROKER_URL);
            connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            messageConsumer = session.createConsumer(session.createTopic("VIOLET.TOPIC"));
            messageProducer = session.createProducer(session.createTopic("VIOLET.TOPIC"));
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

    public void sendCommand(Command command) throws JMSException, InterruptedException {
        ObjectMessage msg = session.createObjectMessage();
        msg.setObject(command);
        messageProducer.send(msg);
    }

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
/*
 * (c) Copyright Enterprise Computing Research Group (ECRG),
 *               National University of Ireland, Galway 2003.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE and
 * no warranty that the program does not infringe the Intellectual Property rights of a third party.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

/**
 * SonicMQ implementation of the ProviderAdmin interface
 * 
 * <p>
 * Provides support for the SonicMQ JMS provider
 * </p>
 */
package otros;

import java.util.logging.Logger;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;

import com.sonicsw.ma.mgmtapi.config.*;
import com.sonicsw.mq.mgmtapi.config.*;

import demo.KfkMtpConfig;
import demo.TCPAddress;
import ie.nuigalway.ecrg.jade.jmsmtp.common.JMSAddress;
import jade.mtp.MTPException;

public final class KafkaProviderAdmin {

	private static final Logger log = Logger.getLogger(KafkaProviderAdmin.class.getName());
	private static final String classKey = "ie.nuigalway.ecrg.jade.jmsmtp.providerType.sonicmq.";

	/**
	 * Create a QueueConnection to a specified broker and place it in the hashmap
	 * 
	 * @param jmsTA Details of broker to create connection factory too
	 * @return QueueConnectionFactory onnection to this broker
	 * @throws MTPException Error during quue connection activation
	 */
	public QueueConnectionFactory getQueueConnectionFactory(JMSAddress jmsTA) throws MTPException {

		try {

			return (new progress.message.jclient.QueueConnectionFactory(jmsTA.getBrokerURL()));
		} catch (javax.jms.JMSException jmse) {
			log.error("Cannot connect to Broker:" + jmse.toString());
			throw new MTPException("Cannot connect to Broker", jmse);
		}
	}

	/**
	 * Lookup or create a specified queue and return it
	 * 
	 * @param jmsTA Contains details of the queue to lookup or create
	 * @return Queue The specified queue returned
	 * @throws MTPException Error during queue activation
	 */
	public Queue getOrCreateQueue(TCPAddress jmsTA) throws MTPException {

		final String topic;
		final Object messageListener;

		Queue endResult = null;

		try {

			try {

				// check if the specified queue already exists
				queue = queues.getQueue(jmsTA.getQueueName());

				if (log.isDebugEnabled()) {
					log.debug(jmsTA.getQueueName() + " already exists");
				}
			} catch (MgmtException e) {

				if (log.isDebugEnabled()) {

					// Queue doesn't exist - create it. It will have default values
					// for all attributes
					log.debug(jmsTA.getQueueName() + " doesn't exist. Creating it.");
				}

				queue = queues.createQueue();
				queue.setQueueName(jmsTA.getQueueName());

				// Add the new queue into the list of queues
				queues.addQueue(jmsTA.getQueueName(), queue);
			}

			// Now set the queue values
			int maxQueueSize;

			try {
				maxQueueSize = Integer.parseInt(KfkMtpConfig.getProperty(classKey + "queue.maxSize", "50000"));
			} catch (Exception any) {
				maxQueueSize = 50000; // Default setting if these an non-int value in the config
			}

			queue.setQueueMaxSize(maxQueueSize);

			int queueSaveThr;

			try {
				queueSaveThr = Integer.parseInt(KfkMtpConfig.getProperty(classKey + "Queue.saveThreshold", "25000"));
			} catch (Exception any) {
				queueSaveThr = 50000; // Default setting if these an non-int value in the config
			}

			queue.setQueueSaveThreshold(queueSaveThr);
			queue.setReadExclusive(false);

			// save bean
			domain.saveBean(broker);
			domain.disconnect();

			QueueConnectionFactory qcf = getQueueConnectionFactory(jmsTA);
			QueueConnection conn;

			if (jmsTA.getUsername().equals("")) {
				conn = qcf.createQueueConnection();
			} else {
				conn = qcf.createQueueConnection(jmsTA.getUsername(), jmsTA.getPassword());
			}

			if (log.isDebugEnabled()) {
				log.debug("Start the new broker connection");
			}

			conn.start();

			QueueSession session = conn.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			Queue que = session.createQueue(jmsTA.getQueueName());
			session.close();

			return que;
		} catch (Exception e) {
			log.error("Failed to get or create the queue:" + e.toString());
			throw new MTPException("Failed to get or create the queue:", e);
		}
	}

	@Override
	public QueueConnectionFactory getQueueConnectionFactory(TCPAddress jmsTA) throws MTPException {
		// TODO Auto-generated method stub
		return null;
	}
}

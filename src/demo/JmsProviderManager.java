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
 * Interface for JMS provider support to the main MTP
 * 
 * <p>
 * Handles the send and receving of JMS messages to and from the JADE platform
 * </p>
 * 
 */
package demo;

import jade.domain.FIPAAgentManagement.Envelope;
import jade.mtp.InChannel;
import jade.mtp.MTPException;

public abstract interface JmsProviderManager {

	/**
	 * Address specific activation of a MTP
	 * 
	 * @param disp  Dispatcher for messages to the platform
	 * @param jmsTA Address to activate on
	 * @throws MTPException Error with activation
	 */
	public abstract void activate(InChannel.Dispatcher disp, TCPAddress jmsTA) throws MTPException;

	/**
	 * Deactivate a specific transport address
	 * 
	 * @param jmsTA Address to deactivate
	 * @throws MTPException Error with address deactivation
	 */
	public abstract void deactivate(TCPAddress jmsTA) throws MTPException;

	/**
	 * Deactivate the MTP
	 * 
	 * @throws MTPException Error with MTP deactivation
	 */
	public abstract void deactivate() throws MTPException;

	/**
	 * Deliver a message to a jmsTA
	 * 
	 * @param jmsTA   Address to deliver too
	 * @param env     Envelope of message
	 * @param payload Message payload
	 * @throws MTPException Error during message send
	 */
	public abstract void deliver(TCPAddress jmsTA, Envelope env, byte[] payload) throws MTPException;

//	/**
//	 * Return a broker connetion
//	 * 
//	 * @param jmsTA Broker details to return
//	 * @return a QueueConnectionFactory to the specified broker
//	 * @throws Exception Error in creating connection to broker
//	 */
//	public abstract TopicConnection getBrokerConnection(TCPAddress jmsTA) throws MTPException;
//
//	/**
//	 * Get the Admin interface for a specific JMS provider
//	 * 
//	 * @param providerType Providers interface to return
//	 * @return JMS providers admin interface
//	 * @throws Exception Error in creating the provider interface
//	 */
//	public abstract ProviderAdmin getProviderAdmin(String providerType) throws MTPException;
}

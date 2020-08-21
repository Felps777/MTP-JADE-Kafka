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
 * JMS Map Message Utility
 * 
 * <p>
 * Utility class used to encode and decode JMS Map Messages for use with the
 * JMS-MTP
 * </p>
 * 
 */
package otros;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.jdom2.Element;

import demo.MessageTransportProtocol;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ReceivedObject;

public class MapMessageUtil {

	private static final Logger log = Logger.getLogger(MessageTransportProtocol.class.getName());
	private DateFormat fipaDateFormat; // Date formatter for timestamp

	/**
	 * Creates a new MapMessageUtil object.
	 */
	public MapMessageUtil() {
		fipaDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS'Z'");
	}

	/**
	 * Encodes a JADE Envelope and mesage paylod into a JMS MapMesage
	 * 
	 * @param mm      Message to encode envelope and payload into
	 * @param env     Envelope to be encoded
	 * @param payload Payload to be encoded
	 */
	public void encode(MapMessage mm, Envelope env, byte[] payload) {

		try {

			// Create params
			// TODO: Need to support multiple params
			// TODO: loop here
			mm.setInt("params", 1);

			// Fill in the 'to' field
			Iterator itTo = env.getAllTo();
			int toAID = 0;

			while (itTo.hasNext()) {

				AID id = (AID) itTo.next();
				toAID++;
				encodeAID("to-" + toAID, id, mm);
			}

			// Add count of to-aids
			mm.setInt("to", toAID);

			// Fill in the 'from' field
			encodeAID("from", env.getFrom(), mm);

			// Encode other fields
			// Comments
			if ((env.getComments() != null) && (!env.getComments().trim().equalsIgnoreCase(""))) {
				mm.setString("comments", env.getComments());
			}

			// ACL Repesentation
			if (((env.getAclRepresentation()) != null) && (!env.getAclRepresentation().trim().equalsIgnoreCase(""))) {
				mm.setString("acl-representation", env.getAclRepresentation());
			}

			// Payload Lenght
			if ((env.getPayloadLength() != null)
					&& (!(env.getPayloadLength().toString()).trim().equalsIgnoreCase(""))) {
				mm.setString("payload-length", env.getPayloadLength().toString());
			}

			// Payload Encoding
			if ((env.getPayloadEncoding() != null) && (!env.getPayloadEncoding().trim().equalsIgnoreCase(""))) {
				mm.setString("payload-encoding", env.getPayloadEncoding());
			}

			// Date
			if ((env.getDate() != null)) {
				mm.setString("date", fipaDateFormat.format(env.getDate()));
			}

			// Fill in the 'intended Receiver' field
			Element ir = new Element("intended-reciever");
			Iterator itIR = env.getAllIntendedReceiver();
			boolean anyIR = false;
			int irCount = 0;

			while (itIR.hasNext()) {
				anyIR = true;

				AID id = (AID) itIR.next();
				irCount++;
				encodeAID("intended-reciever-" + irCount, id, mm);
			}

			mm.setInt("intended-reciever", irCount);

			// Received Tag
			ReceivedObject rcv = env.getReceived();

			if ((rcv != null) && ((rcv.getBy() != null) || (rcv.getFrom() != null) || (rcv.getDate() != null)
					|| (rcv.getVia() != null) || (rcv.getId() != null))) {

				// Received
				mm.setBoolean("received", true);

				// received-by
				mm.setString("received-by", String.valueOf(rcv.getBy()));

				// received-from
				mm.setString("received-from", String.valueOf(rcv.getFrom()));

				// received-date
				mm.setString("received-date", String.valueOf(rcv.getDate().toString()));

				// received-id
				mm.setString("received-id", String.valueOf(rcv.getId()));

				// received-via
				mm.setString("received-via", String.valueOf(rcv.getVia()));
			}

			// TODO???
			// Transport Behaviour??
			// Transport Error msgs???
			// Transport Delivery??
			// Trnaport Ack??
			// Output Payload
			mm.setString("payload", new String(payload));
		} catch (JMSException jsme) {
			log.log(Level.SEVERE, "Error in encoding MapMessage: " + jsme.toString());
		}
	}

	/**
	 * Given a MapMessage, decode an AID stored under key
	 * 
	 * @param key Key that AID is stored under in MapMessage
	 * @param mm  MapMessage which contains the AID
	 * @return AID Decoded AID from MapMessage
	 */
	private AID decodeAID(String key, MapMessage mm) {

		AID aid = new AID();

		try {

			// Get the agents name
			aid.setName(mm.getString(key + "-name"));

			log.log(Level.INFO,"Creating AID for: " + aid.getName());

			// Decode all Addresses
			int addressCount = mm.getInt(key + "-addresses");

			for (int ii = 1; ii <= addressCount; ii++) {
				aid.addAddresses(mm.getString(key + "-address-" + addressCount));

				log.log(Level.INFO,"Adding Address");
			}

			// Dncode any resolvers
			// Decode all Addresses
			int resCount = mm.getInt(key + "-resolvers");

			for (int ii = 1; ii <= resCount; ii++) {
				aid.addResolvers(decodeAID(key + "-resolvers-" + resCount, mm));
			}
		} catch (JMSException jsme) {
			log.log(Level.SEVERE, "Error in encoding MapMessage AID: " + jsme.toString());
		}

		return aid;
	}

	/**
	 * Given a AID encode it into a MapMessage stored under key
	 * 
	 * @param key Key that AID is to be stored under in MapMessage
	 * @param aid AID to be stored
	 * @param mm  MapMessage to contains the AID
	 */
	private void encodeAID(String key, AID aid, MapMessage mm) {

		try {
			mm.setString(key + "-name", aid.getName());

			// Encode all Addresses
			Iterator itAdd = aid.getAllAddresses();
			int addressCount = 0;

			while (itAdd.hasNext()) {
				addressCount++;
				mm.setString(key + "-address-" + addressCount, itAdd.next().toString());
			}

			// Add any addresses
			mm.setInt(key + "-addresses", addressCount);

			// Encode any resolvers
			int resCount = 0;
			Iterator itRes = aid.getAllResolvers();

			while (itRes.hasNext()) {
				resCount++;
				encodeAID(key + "-resolvers-" + resCount, (AID) itRes.next(), mm); // Encode any reslovers
			}

			// Any any reslovers
			mm.setInt(key + "-resolvers", resCount);
		} catch (JMSException jsme) {
			log.log(Level.SEVERE, "Error in decoding MapMessage AID: " + jsme.toString());
		}
	}

	/**
	 * Given a MapMessage decode its JADE Envelope and message payload
	 * 
	 * @param mm      MapMessage which contains the Envelope and payload
	 * @param payload StringBuffer to populate with the message payload
	 * @return Envelope Decoded JADE Envelope from MapMessage
	 */
	public Envelope decode(MapMessage mm, StringBuffer payload) {

		Envelope envelope = new Envelope();

		// TODO: Need to support multiple params
		// TODO: loop here
		try {

			int params = mm.getInt("params");

			// TODO: use a for loop here to run params
			// NEED to lookingot the mulit pram stuff for non jade platforms
			for (int loopin = 0; loopin <= params; loopin++) {

				// Extract the 'to' field
				int numOfTos = mm.getInt("to");

				for (int ai = 1; ai <= numOfTos; ai++) {

					// Extract the Agent-Identifiers and add to env
					AID aid = decodeAID("to-" + ai, mm);
					envelope.addTo(aid);
				}

				// Get the 'from' field
				envelope.setFrom(decodeAID("from", mm));

				// Decode other fields
				// Comments
				if (mm.itemExists("comments")) {
					envelope.setComments(mm.getString("comments"));
				}

				// ACL Repesentation
				if (mm.itemExists("acl-representation")) {
					envelope.setAclRepresentation(mm.getString("acl-representation"));
				}

				// Payload Lenght
				if (mm.itemExists("payload-length")) {
					envelope.setPayloadLength(new Long(mm.getString("payload-length")));
				}

				// Payload Encoding
				if (mm.itemExists("payload-encoding")) {
					envelope.setPayloadEncoding(mm.getString("payload-encoding"));
				}

				// Date
				if (mm.itemExists("date")) {
					envelope.setDate(this.getDateFromString(mm.getString("date")));
				}

				// Fill in the 'intended Receiver' field
				int irCount = mm.getInt("intended-reciever");

				// Decode agent identifers
				for (int ii = 1; ii <= irCount; ii++) {
					envelope.addIntendedReceiver(decodeAID("intended-reciever-" + ii, mm));
				}

				// Decode Received Tag
				if (mm.itemExists("received")) {

					ReceivedObject ro = new ReceivedObject();
					ro.setBy(mm.getString("received-by"));
					ro.setFrom(mm.getString("received-from"));
					ro.setDate(this.getDateFromString(mm.getString("received-date")));
					ro.setId(mm.getString("received-id"));
					ro.setVia(mm.getString("received-via"));
					envelope.setReceived(ro);
				}

				// TODO???
				// Transport Behaviour??
				// Transport Error msgs???
				// Transport Delivery??
				// Trnaport Ack??
			}

			// Extract Payload
			payload.append(mm.getString("payload"));
		} catch (JMSException jsme) {
			log.log(Level.SEVERE, "Error in decoding MapMessage: " + jsme.toString());
		}

		return envelope;
	}

	/**
	 * Given a date in a string format convert it to a Date object
	 * 
	 * @param dateTxt Date to be converted in a string format
	 * @return Date Decoded date from string
	 */
	public Date getDateFromString(String dateTxt) {

		try {

			return fipaDateFormat.parse(dateTxt);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error extracting Date from String:" + e.toString());
		}

		return null;
	}
}

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
 * FIPA XML Utility
 * 
 * <p>
 * Utility class used to encode and decode JADE Envelopes into FIPA compatible
 * XML messages
 * </p>
 */
package demo;

import java.io.InputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.springframework.core.io.ClassPathResource;

import FIPA.EnvelopesHelper;
import FIPA.EnvelopesHolder;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ReceivedObject;
import jade.lang.acl.StringACLCodec;

public class FipaXMLUtil {

	private static final Logger log = Logger.getLogger(MessageTransportProtocol.class.getName());
	private XMLOutputter outputter; // Used ot output XML as a string
	private SAXBuilder builder; // XML SAX parser
	private DateFormat fipaDateFormat; // used to create FIPA XML date format

	/**
	 * Creates a new FipaXMLUtil object.
	 */
	public FipaXMLUtil() {

		try {

			Format fm = Format.getRawFormat();
			fm.setIndent("  ");
			fm.setLineSeparator(LineSeparator.SYSTEM);
			outputter = new XMLOutputter(fm);
			// outputter.setIndent(" "); // use two space indent
			// outputter.setNewlines(true);

			builder = new SAXBuilder(SAXParser.class.getName(), false);

		} catch (Exception e) {
			log.log(Level.SEVERE, "Error Creating XML outputter" + e.toString());
			log.log(Level.SEVERE, "Failed to create SaxBuilder:" + e.toString());
		}

		// Set the FIPA XMl Date format
		fipaDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS'Z'");
	}

	/**
	 * Convert a JADE Envelope and message Payload to FIPA compatible XML
	 * 
	 * @param env     JADE Envelope to be encoded
	 * @param payload Message payload to be encoded into XML
	 * @return String FIPA XML containing converted envelope and message payload
	 */
	public String encode(Envelope env, byte[] payload) {

		// Create Message
		Element fipaMessage = new Element("fipa-message");

		// Create Envelope
		Element envelope = new Element("envelope");

		// Add params to message
		fipaMessage.addContent(envelope);

		// Create params
		// TODO: Need to support multiple params
		// TODO: loop here
		Element params = new Element("params");
		params.setAttribute("index", String.valueOf(1));

		// Add params to envelope
		envelope.addContent(params);

		// Fill in the 'to' field
		Element to = new Element("to");
		Iterator itTo = env.getAllTo();

		while (itTo.hasNext()) {

			AID id = (AID) itTo.next();
			to.addContent(encodeAid(id));
		}

		params.addContent(to);

		// Fill in the 'from' field
		Element from = new Element("from");
		from.addContent(encodeAid(env.getFrom())); // Encode from
		params.addContent(from);

		// Encode other fields
		// Comments
		if ((env.getComments() != null) && (!env.getComments().trim().equalsIgnoreCase(""))) {

			Element comments = new Element("comments");
			comments.setText(env.getComments());
			params.addContent(comments);
		}

		// ACL Repesentation
		if ((env.getAclRepresentation() != null) && (!env.getAclRepresentation().trim().equalsIgnoreCase(""))) {

			Element aclRep = new Element("acl-representation");
			aclRep.setText(env.getAclRepresentation());
			params.addContent(aclRep);
		}

		// Payload Lenght
		if ((env.getPayloadLength() != null) && (!(env.getPayloadLength().toString()).trim().equalsIgnoreCase(""))) {

			Element pll = new Element("payload-length");
			pll.setText(env.getPayloadLength().toString());
			params.addContent(pll);
		}

		// Payload Encoding
		if ((env.getPayloadEncoding() != null) && (!env.getPayloadEncoding().trim().equalsIgnoreCase(""))) {

			Element plenc = new Element("payload-encoding");
			plenc.setText(env.getPayloadEncoding());
			params.addContent(plenc);
		}

		// Date
		if ((env.getDate() != null)) {

			Element date = new Element("date");
			date.setText(fipaDateFormat.format(env.getDate()));
			params.addContent(date);
		}

		// Fill in the 'intended Receiver' field
		Element ir = new Element("intended-reciever");
		Iterator itIR = env.getAllIntendedReceiver();
		boolean anyIR = false;

		while (itIR.hasNext()) {
			anyIR = true;

			AID id = (AID) itIR.next();
			ir.addContent(encodeAid(id));
		}

		if (anyIR) {
			params.addContent(ir);
		}

		// Received Tag
		ReceivedObject rcv = env.getReceived();

		if ((rcv != null) && ((rcv.getBy() != null) || (rcv.getFrom() != null) || (rcv.getDate() != null)
				|| (rcv.getVia() != null) || (rcv.getId() != null))) {

			// Received
			Element received = new Element("received");
			params.addContent(received);

			// received-by
			Element recBy = new Element("received-by");
			recBy.setAttribute("value", String.valueOf(rcv.getBy()));
			received.addContent(recBy);

			// received-from
			Element recFrom = new Element("received-from");
			recFrom.setAttribute("value", String.valueOf(rcv.getFrom()));
			received.addContent(recFrom);

			// received-date
			Element recDate = new Element("received-date");
			recDate.setAttribute("value", String.valueOf(rcv.getDate().toString()));
			received.addContent(recDate);

			// received-id
			Element recID = new Element("received-id");
			recID.setAttribute("value", String.valueOf(rcv.getId()));
			received.addContent(recID);

			// received-via
			Element recVIA = new Element("received-via");
			recVIA.setAttribute("value", String.valueOf(rcv.getVia()));
			received.addContent(recVIA);
		}

		// TODO???
		// Transport Behaviour??
		// Transport Error msgs???
		// Transport Delivery??
		// Trnaport Ack??
		// Output Payload
		Element pload = new Element("payload");
		pload.setText(new String(payload));
		fipaMessage.addContent(pload);

		Document doc = new Document();
		doc.setRootElement(fipaMessage);

		String res = "";

		// Oupt the XML to a string
		try {
			res = outputter.outputString(doc);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error outputting XML" + e.toString());
		}

		log.log(Level.INFO, "Message being sent: " + res);

		return res;
	}

	/**
	 * Decode the AID contained in the XML element
	 * 
	 * @param elAid XML Element containing an AID
	 * @return AID Decoded AID from XML Element
	 */
	private AID decodeAid(Element elAid) {

		AID aid = new AID();

		// Get the agents name
		aid.setName(elAid.getChildText("name"));

		log.log(Level.INFO, "Creating AID for: " + aid.getName());

		// Decode all Addresses
		Element elAddresses = elAid.getChild("addresses");
		java.util.Iterator itUrl = elAddresses.getChildren("url").iterator();

		while (itUrl.hasNext()) {
			aid.addAddresses(((Element) itUrl.next()).getText());

			log.log(Level.INFO, "Adding Address");
		}

		// Dncode any resolvers
		// Decode all Addresses
		Element elResolvers = elAid.getChild("resolvers");

		// Need to examine the rest of the parsing code for this kind of error!!
		if (elResolvers != null) {

			java.util.Iterator itRes = elResolvers.getChildren("agent-identifiers").iterator();

			while (itRes.hasNext()) {
				aid.addResolvers(decodeAid((Element) itRes.next()));
			}
		}

		return aid;
	}

	/**
	 * Given an AID store it in an XML Element
	 * 
	 * @param aid AID to e store in FIPA XML format
	 * @return Element XML Element containing encoded AID
	 */
	private Element encodeAid(AID aid) {

		Element agentId = new Element("agent-identifier");
		Element name = new Element("name");
		name.setText(aid.getName());
		agentId.addContent(name);

		// Encode all Addresses
		Iterator itAdd = aid.getAllAddresses();
		Element addresses = new Element("addresses");
		boolean anyAdd = false;

		while (itAdd.hasNext()) {

			Element url = new Element("url");
			anyAdd = true;
			url.setText(itAdd.next().toString());
			addresses.addContent(url);
		}

		// Add any addresses
		if (anyAdd) {
			agentId.addContent(addresses);
		}

		// Encode any resolvers
		Element resolvers = new Element("resolvers");
		boolean anyRes = false;
		Iterator itRes = aid.getAllResolvers();

		while (itRes.hasNext()) {
			anyRes = true;
			resolvers.addContent(encodeAid((AID) itRes.next())); // Encode any reslovers
		}

		// Any any reslovers
		if (anyRes) {
			agentId.addContent(resolvers);
		}

		return agentId;
	}

	/**
	 * Given an FIPA compliant XML message decode its envelope and payload
	 * 
	 * @param msg     FIPA compliant XML message to be decoded
	 * @param payload StringBuffer to populate with message payload
	 * @return Envelope Decoded JADE Message Envelope
	 */
	public Envelope decode(String msg, StringBuffer payload) {

		Envelope envelope = new Envelope();
		Element elFipaMessage = null;

		ClassLoader prevCl = Thread.currentThread().getContextClassLoader();

		try {

			// Save the class loader so that you can restore it later
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
//			InputStream is = this.getClass().getResourceAsStream("EnvelopeTest.xml") ;
					//new ClassPathResource("EnvelopeTest.xml").getInputStream();
			
			
//			Document doc = builder.build(is);
			Document doc = builder.build(new StringReader(msg));

			elFipaMessage = doc.getRootElement();

			log.log(Level.INFO, "Message received: " + outputter.outputString(doc));

		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to create SaxBuilder:" + e.toString());
		} finally {
			// Restore
			Thread.currentThread().setContextClassLoader(prevCl);
		}

		// Create Envelope
		// Create params
		// TODO: Need to support multiple params
		// TODO: loop here

		Element elEnvelope = elFipaMessage.getChild("envelope");
		Iterator itParams = elEnvelope.getChildren("params").iterator();

		// NEED to lookingot the mulit pram stuff for non jade platforms and the
		// like!!!!!!
		while (itParams.hasNext()) {

			// Extract the 'to' field
			Element elParams = (Element) itParams.next();

			// Get the 'to' field
			Element elTo = elParams.getChild("to");

			// Extract the Agent-Identifiers and add to env
			Iterator itToAid = elTo.getChildren("agent-identifier").iterator();

			while (itToAid.hasNext()) {

				AID aid = decodeAid((Element) itToAid.next());
				envelope.addTo(aid);
			}

			// Get the 'from' field
			Element elFrom = elParams.getChild("from");

			// Extract the Agent-Identifiers and add to env
			envelope.setFrom(decodeAid(elFrom.getChild("agent-identifier")));

			// Decode other fields
			// Comments
			if (elParams.getChildText("comments") != null) {
				envelope.setComments(elParams.getChildText("comments"));
			}

			// ACL Repesentation
			if (elParams.getChildText("acl-representation") != null) {
				envelope.setAclRepresentation(elParams.getChildText("acl-representation"));
			}

			// Payload Lenght
			if (elParams.getChildText("payload-length") != null) {
				envelope.setPayloadLength(new Long(elParams.getChildText("payload-length")));
			}

			// Payload Encoding
			if (elParams.getChildText("payload-encoding") != null) {
				envelope.setPayloadEncoding(elParams.getChildText("payload-encoding"));
			}

			// Date
			if (elParams.getChildText("date") != null) {
				envelope.setDate(this.getDateFromString(elParams.getChildText("date")));
			}

			// Fill in the 'intended Receiver' field
			Element elIR = new Element("intended-reciever");

			// Decode agent identifers
			Iterator itIR = elIR.getChildren("agent-identifiers").iterator();

			while (itIR.hasNext()) {
				envelope.addIntendedReceiver(decodeAid((Element) itIR.next()));
			}

			// Decode Received Tag
			if (elParams.getChild("received") != null) {

				Element received = elParams.getChild("received");
				ReceivedObject ro = new ReceivedObject();
				ro.setBy(((Element) received.getChild("received-by")).getAttribute("value").toString());
				ro.setFrom(((Element) received.getChild("received-from")).getAttribute("value").toString());
				ro.setDate(this.getDateFromString(
						((Element) received.getChild("received-date")).getAttribute("value").toString()));
				ro.setId(((Element) received.getChild("received-id")).getAttribute("value").toString());
				ro.setVia(((Element) received.getChild("received-via")).getAttribute("value").toString());
				envelope.setReceived(ro);
			}

			// TODO???
			// Transport Behaviour??
			// Transport Error msgs???
			// Transport Delivery??
			// Trnaport Ack??
		}

		// Extract Payload
		payload.append(elFipaMessage.getChildText("payload"));

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

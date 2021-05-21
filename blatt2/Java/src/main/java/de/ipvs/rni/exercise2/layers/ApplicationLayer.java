package de.ipvs.rni.exercise2.layers;

import de.ipvs.rni.exercise2.common.Application;
import de.ipvs.rni.exercise2.common.ProcessEvents;
import de.ipvs.rni.exercise2.common.Utils;
import sun.nio.ch.Util;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * This class represents the application layer.
 * 
 * An Application can bind to the application layer for network communication.
 * The application layer provides the application with three methods for message
 * communication: 'sendMessage', 'hasMessage', and 'popMessage'.
 *
 */
public class ApplicationLayer implements ProcessEvents {

	public final static byte END = (byte) 0xAE;

	// Input Queue/Buffer, to store messages from the application.
	private ArrayDeque<Serializable> fromUpper = new ArrayDeque<>();
	// Ouput Buffer, where the appliaction layer forwards an received object/message
	// to the application.
	private ArrayDeque<Object> toUpper = new ArrayDeque<>();
	// Input Buffer, where the appliaction layer receives new bytes from its lower
	// layer.
	private ArrayDeque<Byte> fromLower;
	// Output Buffer, where the appliaction layer forwards its data to next lower
	// layer.
	private ArrayDeque<Byte> toLower;
	// Buffer for aggregating bytes before object creation.
	private ArrayDeque<Byte> messageBuffer = new ArrayDeque<>();

	/**
	 * Bind an app to the application layer.
	 * 
	 * @param app
	 *            An application which should be bound to this layer
	 */
	public void bind(Application app) {
		app.setApplicationLayer(this);
	}

	/**
	 * Transform an object into an byte array/buffer/queue.
	 * 
	 * @param obj
	 *            Any object which implements the Serializable interface
	 * @return
	 */
	public ArrayDeque<Byte> transformMessageForSending(Serializable obj) {
		ArrayDeque<Byte> data = Utils.objToBytes(obj);
		data.add(END);
		return data;
	}

	/**
	 * Transforms the input bytes back into an object to be handed over to the next
	 * layer.
	 * 
	 * @param byteInput
	 * @return the object reconstructed
	 */
	public Object transformMessageForReceiving(ArrayDeque<Byte> byteInput) {
		while (!byteInput.isEmpty()) { //retrieve all available bytes into message buffer
			byte b = byteInput.poll();
			if (b == END) { //if end byte encountered: return message object, clear message buffer
				Object obj = Utils.bytesToObj(messageBuffer);
				messageBuffer.clear();
				return obj;
			}
			messageBuffer.add(b);
		}
		return null; //return null if message in buffer is not complete yet
	}

	/**
	 * Forward bytes to next lower layer
	 * 
	 * @param byteBuffer
	 *            Source Byte Buffer
	 * @param nextLayer
	 *            Destination Byte Buffer
	 */
	public void sendMessageBytes(ArrayDeque<Byte> byteBuffer, ArrayDeque<Byte> nextLayer) {
		nextLayer.addAll(byteBuffer);
	}

	@Override
	public void process() {
		//handle outgoing messages
		while (!fromUpper.isEmpty()) { //convert objects to byte stream and forward to lower layer
			Serializable obj = fromUpper.poll();
			ArrayDeque<Byte> data = transformMessageForSending(obj);
			toLower.addAll(data);
		}

		while (!fromLower.isEmpty()) { //take bytes from lower layer and convert into objects
			Object obj = transformMessageForReceiving(fromLower);
			if (obj != null) toUpper.add(obj);
		}
	}

	/**
	 * Send an object which implements the interface Serializable.
	 * 
	 * @param obj
	 */
	public void sendMessage(Serializable obj) {
		fromUpper.offer(obj);
	}

	/**
	 * Probe if we received an object/message.
	 * 
	 * @return
	 */
	public boolean hasMessage() {
		return !toUpper.isEmpty();
	}

	/**
	 * Get new object/message and remove it from the application layer queue/buffer.
	 * 
	 * @return
	 */
	public Object pollMessage() {
		return toUpper.poll();
	}

	/**
	 * Check if byteBuffer has an end delimiter.
	 * 
	 * This method assumes that the byte buffer has stuffed bytes .
	 * 
	 * @param byteBuffer
	 * @return
	 */
	public ArrayDeque<Serializable> getFromUpper() {
		return fromUpper;
	}

	public void setFromUpper(ArrayDeque<Serializable> fromUpper) {
		this.fromUpper = fromUpper;
	}

	public ArrayDeque<Object> getToUpper() {
		return toUpper;
	}

	public void setToUpper(ArrayDeque<Object> toUpper) {
		this.toUpper = toUpper;
	}

	public ArrayDeque<Byte> getFromLower() {
		return fromLower;
	}

	public void setFromLower(ArrayDeque<Byte> fromLower) {
		this.fromLower = fromLower;
	}

	public ArrayDeque<Byte> getToLower() {
		return toLower;
	}

	public void setToLower(ArrayDeque<Byte> toLower) {
		this.toLower = toLower;
	}
}

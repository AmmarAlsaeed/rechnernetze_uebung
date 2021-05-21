package de.ipvs.rni.exercise2.layers;

import de.ipvs.rni.exercise2.common.*;
import de.ipvs.rni.exercise2.packets.*;

import java.util.ArrayDeque;
import java.util.Iterator;

public class DataLinkLayer implements ProcessEvents
{
    private static final int MTU = 1500;
    private static final int RETRANSMISSION_TIMEOUT = 4;
    public static final boolean CRC_IMPLEMENTED = true;

    private ArrayDeque<Byte> fromUpper = new ArrayDeque<>();
    private ArrayDeque<Byte> toUpper = new ArrayDeque<>();
    // The in-flight queue contains all elements which are not ACKed yet
    private ArrayDeque<Frame> inFlight = new ArrayDeque<>();
    // The retransmissionQueue contains the frames which need to be retransmitted
    public ArrayDeque<Frame> retransmissionQueue = new ArrayDeque<>();
    // The retransmissionTimeouts contains the timeouts for the frames in the in-flight queue (same order)
    private ArrayDeque<Integer> retransmissionTimeouts = new ArrayDeque<>();
    private ArrayDeque<Frame> fromLower;
    private ArrayDeque<Frame> toLower;

    private int seqNo = 0;
    private int nextSeqNo = 0;
    private int availWindow = 5;
    private int nextAck = -1;
    private int curTime = 0;

    public DataLinkLayer()
    {
        nextAck = seqNo;
    }

    public void bind(ApplicationLayer appLayer){
       appLayer.setFromLower(toUpper);
       appLayer.setToLower(fromUpper);
    }
    
    public Frame createNextFrame(ArrayDeque<Byte> bytesToSent)
    {
        int payloadLength = Math.min(bytesToSent.size(), MTU - 16); //maximum payload: MTU - 4(seqNum) - 4(ackNum) - 8(CRC)
        byte[] payload = new byte[payloadLength];
        for (int i = 0; i < payloadLength; i++) {
            payload[i] = bytesToSent.poll();
        }
        return new Frame(seqNo++, payload, payloadLength); //create frame and increment sequence number
    }

    @Override
    public void process()
    {
        //handle incoming frame
        while (!fromLower.isEmpty()) { //receive frame
            Frame frame = fromLower.poll();
            boolean frameValid = frame.checkIntegrity();
            if (frame.isAck()) { //ACK frame
                int size = inFlight.size();
                for (int i = 0; i < size; i++) { //iterate over in-flight frames and timeouts
                    Frame f = inFlight.poll();
                    int timeout = retransmissionTimeouts.poll();
                    if (f.getSeqNo() > frame.getAckNo()) { //frame later than ackNo -> return to queue
                        inFlight.add(f);
                        retransmissionTimeouts.add(timeout);
                    } else { //frame before/at ackNo -> remove from queue and increment window
                        availWindow++;
                    }
                }
            } else if (frameValid && frame.getSeqNo() == nextSeqNo) { //next expected frame
                byte[] payload = frame.getPayload();
                for (byte b : payload) { //forward payload to upper layer
                    toUpper.add(b);
                }
                nextSeqNo++; //increment expected sequence number
            } else if (frameValid && frame.getSeqNo() < nextSeqNo) { //duplicate frame -> force ACK resend
                nextAck = frame.getSeqNo();
            }
        }

        //check in-flight frame timeouts
        int size = inFlight.size();
        for (int i = 0; i < size; i++) { //iterate over in-flight frame and timeouts
            Frame f = inFlight.poll();
            int timeout = retransmissionTimeouts.poll();
            if (timeout > curTime) { //frame not timed out yet -> return to queue
                inFlight.add(f);
                retransmissionTimeouts.add(timeout);
            } else { //frame timed out -> remove from in-flight and timeout queues, add to retransmission queue
                retransmissionQueue.add(f);
            }
        }

        //send outgoing frame if necessary
        if (nextSeqNo > nextAck) { //send ACK if required (with highest received sequence number)
            nextAck = nextSeqNo;
            toLower.add(new Frame(nextAck - 1));
        }
        while (!retransmissionQueue.isEmpty()) { //frames waiting for retransmission -> retransmit next
            Frame rtFrame = retransmissionQueue.poll();
            toLower.add(new Frame(rtFrame));
            inFlight.add(rtFrame);
            retransmissionTimeouts.add(curTime + RETRANSMISSION_TIMEOUT);
        }
        while (availWindow > 0 && !fromUpper.isEmpty()) { //upper layer frames waiting -> transmit if window open, decrement window
            Frame nextFrame = createNextFrame(fromUpper);
            toLower.add(new Frame(nextFrame));
            inFlight.add(nextFrame);
            retransmissionTimeouts.add(curTime + RETRANSMISSION_TIMEOUT);
            availWindow--;
        }

        curTime++;
	  }


    //////////////////////////////////////////////////
    // Getter and Setter for testing purposes only! //
    //////////////////////////////////////////////////

    public int getSeqNo()
    {
        return seqNo;
    }

    public ArrayDeque<Byte> getFromUpper()
    {
        return fromUpper;
    }

    public void setFromUpper(ArrayDeque<Byte> fromUpper)
    {
        this.fromUpper = fromUpper;
    }

    public ArrayDeque<Byte> getToUpper()
    {
        return toUpper;
    }

    public void setToUpper(ArrayDeque<Byte> toUpper)
    {
        this.toUpper = toUpper;
    }

    public ArrayDeque<Frame> getInFlight()
    {
        return inFlight;
    }

    public void setInFlight(ArrayDeque<Frame> inFlight)
    {
        this.inFlight = inFlight;
    }

    public ArrayDeque<Frame> getFromLower()
    {
        return fromLower;
    }

    public void setFromLower(ArrayDeque<Frame> fromLower)
    {
        this.fromLower = fromLower;
    }

    public ArrayDeque<Frame> getToLower()
    {
        return toLower;
    }

    public void setToLower(ArrayDeque<Frame> toLower)
    {
        this.toLower = toLower;
    }

    public void setSeqNo(int seqNo)
    {
        this.seqNo = seqNo;
    }

    public int getAvailWindow()
    {
        return availWindow;
    }

    public void setAvailWindow(int availWindow)
    {
        this.availWindow = availWindow;
    }
}

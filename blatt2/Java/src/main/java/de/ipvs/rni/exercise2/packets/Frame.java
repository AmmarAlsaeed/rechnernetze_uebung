package de.ipvs.rni.exercise2.packets;

import java.util.zip.CRC32;

import static java.lang.System.arraycopy;
import java.util.Arrays;

import de.ipvs.rni.exercise2.common.Utils;
import de.ipvs.rni.exercise2.layers.DataLinkLayer;

public class Frame
{

    private int seqNo;
    private int ackNo = -1;
    private byte[] payload;
    private long crc;


    public Frame(int ackNo)
    {
        this.ackNo = ackNo;
    }

    public Frame(int seqNo, byte[] payload, int payloadLength)
    {
        this.seqNo = seqNo;
        if(payloadLength != 0)
        {
            this.payload = new byte[payloadLength];
            arraycopy(payload, 0, this.payload, 0, payloadLength);

            CRC32 crclib = new CRC32();
            crclib.update(this.payload);
            this.crc = crclib.getValue();
        }
    }

    public Frame(Frame f)
    {
        this(f.seqNo, f.payload, f.payload != null ? f.payload.length : 0);

        if(f.payload != null) {
            System.arraycopy(f.payload, 0, this.payload, 0, f.payload.length);
        }

        ackNo = f.ackNo;
        crc = f.crc;
    }

    public boolean isAck(){
        return getAckNo() != -1;
    }

    public int getSeqNo()
    {
        return seqNo;
    }

    public int getAckNo()
    {
        return ackNo;
    }

    public byte[] getPayload()
    {
        return payload;
    }

    public long getCRC()
    {
        return crc;
    }

    public boolean checkIntegrity()
    {
        //CRC-Switch
        if(!DataLinkLayer.CRC_IMPLEMENTED)
            return true;

        if(payload != null)
        {
            CRC32 crclib = new CRC32();
            crclib.update(payload);

            return crclib.getValue() == crc;
        }
        else
        {
            return true;
        }
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.seqNo;
        hash = 89 * hash + this.ackNo;
        hash = 89 * hash + Arrays.hashCode(this.payload);
        hash = 89 * hash + (int) (this.crc ^ (this.crc >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Frame other = (Frame) obj;
        if (this.seqNo != other.seqNo) {
            return false;
        }
        if (this.ackNo != other.ackNo) {
            return false;
        }
        if (this.crc != other.crc) {
            return false;
        }
        if (!Arrays.equals(this.payload, other.payload)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Frame{" +
                "seqNo=" + seqNo +
                ", ackNo=" + ackNo +
                (payload != null  ? ", PayloadLength=" + payload.length : "") +
                ", crc=" + crc +
                '}';
    }
}

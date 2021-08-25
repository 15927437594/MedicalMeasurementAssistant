package cn.com.medicalmeasurementassistant.protocol;

import java.util.ArrayList;
import java.util.List;

public class Protocol {
    protected int start = 0xEB;
    protected int version = 0x01;
    protected int source = 0x00;
    protected int destination = 0x00;
    protected int action = 0x0000;
    protected int length = 0x0000;
    protected List<Integer> data = new ArrayList<>();
    protected int crc1 = 0x00;
    protected int crc2 = 0x00;
    protected List<Integer> raw = new ArrayList<>();

    public void initParent(Protocol protocol) {
        this.start = protocol.start;
        this.version = protocol.version;
        this.source = protocol.source;
        this.destination = protocol.destination;
        this.action = protocol.action;
        this.length = protocol.length;
        this.data = protocol.data;
        this.crc1 = protocol.crc1;
        this.crc2 = protocol.crc2;
        this.raw = protocol.raw;
    }
}

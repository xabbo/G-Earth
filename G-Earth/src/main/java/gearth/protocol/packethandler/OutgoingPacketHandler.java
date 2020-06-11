package gearth.protocol.packethandler;

import gearth.misc.listenerpattern.Observable;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import gearth.services.extensionhandler.ExtensionHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OutgoingPacketHandler extends PacketHandler {

    public OutgoingPacketHandler(OutputStream outputStream, Object[] trafficObservables, ExtensionHandler extensionHandler) {
        super(outputStream, trafficObservables, extensionHandler);
    }



    private Observable<OnDatastreamConfirmedListener> datastreamConfirmedObservable = new Observable<>();
    public void addOnDatastreamConfirmedListener(OnDatastreamConfirmedListener listener) {
        datastreamConfirmedObservable.addListener(listener);
    }

    private void dataStreamCheck(byte[] buffer)	{
        if (!isDataStream) {
            HPacket hpacket = new HPacket(buffer);
            isDataStream = (hpacket.getBytesLength() > 6 && hpacket.length() < 100);
            if (isDataStream) {
                String version = hpacket.readString();
                datastreamConfirmedObservable.fireEvent(l -> l.confirm(version));
            }
        }
    }

    @Override
    public void act(byte[] buffer) throws IOException {
        dataStreamCheck(buffer);
        super.act(buffer);
    }

    @Override
    public HMessage.Direction getMessageSide() {
        return HMessage.Direction.TOSERVER;
    }


    @Override
    protected void printForDebugging(byte[] bytes) {
        System.out.println("-- DEBUG OUTGOING -- " + new HPacket(bytes).toString() + " -- DEBUG --");
    }
}

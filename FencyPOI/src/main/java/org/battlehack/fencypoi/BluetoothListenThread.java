package org.battlehack.fencypoi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BluetoothListenThread extends Thread {
    public static final UUID BLUETOOTH_UUID = UUID.fromString("3357A7BB-762D-464A-8D9A-DCA592D57D5B");

    private final BluetoothServerSocket listeningSocket;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public BluetoothListenThread(final BluetoothAdapter adapter) {
        try {
            this.listeningSocket = adapter.listenUsingInsecureRfcommWithServiceRecord("FencyPOI", BLUETOOTH_UUID);

            start();
        } catch (final IOException x) {
            throw new RuntimeException(x);
        }
    }

    @Override
    public void run() {
        System.out.println("=== BTTX Thread run");
        while (running.get()) {
            BluetoothSocket socket = null;
            DataInputStream inputStream = null;

            try {
                // start a blocking call, and return only on success or exception
                socket = listeningSocket.accept();
                System.out.println("=== BTTX accepted");

                inputStream = new DataInputStream(socket.getInputStream());
                final int numMessages = inputStream.readInt();

                for (int i = 0; i < numMessages; i++) {
                    System.out.println("BTTX reading msg: " + i);
                    final int msgLength = inputStream.readInt();
                    final byte[] msg = new byte[msgLength];
                    inputStream.readFully(msg);

                    handleMsg(msg);
                }
            } catch (final IOException x) {
                x.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (final IOException x) {
                        // swallow
                    }
                }

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (final IOException x) {
                        // swallow
                    }
                }
            }
        }
    }

    public void stopAccepting() {
        System.out.println("BTTX stop accepting");

        running.set(false);

        try {
            listeningSocket.close();
        } catch (final IOException x) {
            // swallow
        }
    }

    protected abstract void handleMsg(byte[] msg);
}

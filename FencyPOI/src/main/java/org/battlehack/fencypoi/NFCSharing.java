package org.battlehack.fencypoi;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Parcelable;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * move the code from the MainActivity
 * <p/>
 * Created by ligi on 6/18/13.
 */
public class NFCSharing {

    private NfcManager nfcManager;
    private NfcAdapter nfcAdapter;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothListenThread acceptThread;
    private String btMac;

    private Activity ctx;

    public NFCSharing(Activity ctx) {
        this.ctx = ctx;
        nfcManager = (NfcManager) ctx.getSystemService(Context.NFC_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btMac = bluetoothAdapter.getAddress();

/*		nfcAdapter = nfcManager.getDefaultAdapter();
        nfcAdapter.setNdefPushMessageCallback(new CreateNdefMessageCallback()
		{
			@Override
			public NdefMessage createNdefMessage(final NfcEvent event)
			{
					final NdefRecord record = createExternal(getPackageName(), "pois", btMac.getBytes());
					final NdefRecord appRecord = NdefRecord.createApplicationRecord(getPackageName());
					return new NdefMessage(new NdefRecord[] { record, appRecord });
			}
		}, this, new Activity[]{});
*/


    }

    public void onNewIntent(final Intent intent) {
        final Parcelable[] msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        final NdefMessage ndef = (NdefMessage) msgs[0];

        final String sendBtMac = new String(ndef.getRecords()[0].getPayload());
        System.out.println("sendBtMac: " + sendBtMac);

        BluetoothSocket socket = null;

        try {

            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(sendBtMac);
            socket = device.createInsecureRfcommSocketToServiceRecord(BluetoothListenThread.BLUETOOTH_UUID);

            socket.connect();
            final DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            final Cursor cursor = ctx.getContentResolver().query(POIDBContentProvider.CONTENT_URI, null, null, null, null);

            os.writeInt(cursor.getCount());
            while (cursor.moveToNext()) {
                os.writeInt(cursor.getInt(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_LAT)));
                os.writeInt(cursor.getInt(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_LON)));
                os.writeInt(cursor.getInt(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_ALTITUDE)));
                os.writeInt(cursor.getInt(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_RADIUS)));
                os.writeUTF(cursor.getString(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_TYPE)));
                os.writeUTF(cursor.getString(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_NAME)));
                os.writeInt(cursor.getInt(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_DESCRIPTION)));
                os.writeUTF(cursor.getString(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_CREATOR)));
                os.writeInt(cursor.getInt(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_CREATED_AT)));
            }
            cursor.close();

            os.close();

            Toast.makeText(ctx, "sent via bluetooth", Toast.LENGTH_LONG).show();
        } catch (final IOException x) {
            x.printStackTrace();
            Toast.makeText(ctx, "error sending tx via bluetooth: " + x.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (final IOException x) {
                    // swallow
                }
            }
        }
    }

    protected void onResume() {
     /*
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
		{
			maybeInitBluetoothListening();

			final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			final IntentFilter intentFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

			nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[] { intentFilter }, null);
		}
		*/
    }

    public void onPause() {

		/*nfcAdapter.disableForegroundDispatch(this);

		if (acceptThread != null)
			acceptThread.stopAccepting();
     */
    }

    private void maybeInitBluetoothListening() {
        acceptThread = new BluetoothListenThread(bluetoothAdapter) {
            @Override
            public void handleMsg(final byte[] msg) {
                ctx.runOnUiThread(new Runnable() {
                    public void run() {
                        System.out.println("=== BTTX bluetooth message arrived");
                        Toast.makeText(ctx, "BTTX bluetooth message arrived", Toast.LENGTH_LONG).show();

                        try {
                            final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(msg));
                            final ContentValues values = new ContentValues();
                            values.put(POIDBContentProvider.KEY_LAT, dis.readInt());
                            values.put(POIDBContentProvider.KEY_LON, dis.readInt());
                            values.put(POIDBContentProvider.KEY_ALTITUDE, dis.readInt());
                            values.put(POIDBContentProvider.KEY_RADIUS, dis.readInt());
                            values.put(POIDBContentProvider.KEY_TYPE, dis.readUTF());
                            values.put(POIDBContentProvider.KEY_NAME, dis.readUTF());
                            values.put(POIDBContentProvider.KEY_DESCRIPTION, dis.readInt());
                            values.put(POIDBContentProvider.KEY_CREATOR, dis.readUTF());
                            values.put(POIDBContentProvider.KEY_CREATED_AT, dis.readInt());
                            dis.close();

                            ctx.getContentResolver().insert(POIDBContentProvider.CONTENT_URI, values);
                        } catch (final Exception x) {
                            Toast.makeText(ctx, "exception: " + x, Toast.LENGTH_LONG).show();
                            x.printStackTrace();
                        }
                    }
                });
            }
        };
    }


}

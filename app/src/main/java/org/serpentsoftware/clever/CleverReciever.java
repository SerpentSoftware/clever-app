package org.serpentsoftware.clever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import java.util.HashMap;

public class CleverReciever extends BroadcastReceiver {
    HashMap< String, PhoneNumber > numbers = new HashMap< String, PhoneNumber >();

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; message: " + message);

                    if( !numbers.containsKey( phoneNumber ) ) {
                        numbers.put( phoneNumber, new PhoneNumber( phoneNumber ) );
                    }
                    numbers.get( phoneNumber ).onSMSRecieve( message );

                    // Show alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, "senderNum: "+ phoneNumber + ", message: " + message, duration);
                    toast.show();

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}

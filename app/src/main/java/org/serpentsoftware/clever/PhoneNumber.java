package org.serpentsoftware.clever;

import android.telephony.SmsManager;
import android.util.Log;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Registration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 6/14/14.
 */
public class PhoneNumber {
    private XMPPConnection connection;
    private Chat mainChat;

    private String number;
    private String username;
    private String password;

    public PhoneNumber( String number ) {
        try {
            ConnectionConfiguration config = new ConnectionConfiguration("10.0.0.16", 5223);
            this.connection = new XMPPConnection(config);
            connection.connect();

            this.number = number;
            this.username = number;
            this.password = "password";

            createUser();

            this.connection.login( username, password );

            this.mainChat = this.connection.getChatManager().createChat( "main@clever", new MessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    sendText( message.getBody() );
                }
            });
            this.mainChat.sendMessage("Test Message");

        } catch ( XMPPException e ) {
            e.printStackTrace();
        }
    }

    private void connect() throws XMPPException {
        this.connection.connect();
        this.connection.login( username, password );
    }

    private void createUser() throws XMPPException {
        if( !this.connection.isConnected() ) {
            this.connection.connect();
        }

        Registration registration = new Registration();
        registration.setUsername( username );
        registration.setPassword( password );
        registration.setType(IQ.Type.fromString("set"));
        this.connection.sendPacket( registration );
    }

    public void onSMSRecieve( String message ) {
        try {
            Log.v( "phonenumber", "onSMSRecieve " + message );
            this.connect();
            this.mainChat.sendMessage(new Message(message));
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    private void sendText( String message ) {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage( this.number, null, message, null, null );
    }

}

package org.serpentsoftware.clever;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;

import java.util.ArrayList;
import java.util.Collection;


public class CleverActivity extends ActionBarActivity {

    private void sendMessageTest() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConnectionConfiguration config = new ConnectionConfiguration("10.0.0.16", 5223);
        XMPPConnection conn = new XMPPConnection(config);
        try {
            Log.v("conn", "About to conn");
            conn.connect();
            Log.v("conn", "conn");

            Registration registration = new Registration();
            registration.setType(IQ.Type.fromString("set"));
            registration.setUsername("regist");
            registration.setPassword("test");
            conn.sendPacket( registration );

            conn.login("regist", "notapassword", "");

            Roster roster = conn.getRoster();
            Collection<RosterEntry> rosterList = roster.getEntries();
            for( RosterEntry friend : rosterList ) {
                Log.v("Friend", "friend: " + friend.getUser() );
            }

            ChatManager chatmanager = conn.getChatManager();
            final Chat newChat = chatmanager.createChat("main@clever", new MessageListener() {
                // Receiving Messages
                public void processMessage(Chat chat, Message message) {
                    Message outMsg = new Message(message.getBody());
                    try {
                        //Send Message object
                        Log.v("Clever Activity","Recieved a message " + message.getBody());
                        chat.sendMessage(outMsg);
                    } catch (XMPPException e) {
                        //Error
                    }
                }
            });

            newChat.sendMessage("Hello There");

            Log.v("this", "Is Connected? " + conn.isConnected() );
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clever);

        sendMessageTest();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.clever, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

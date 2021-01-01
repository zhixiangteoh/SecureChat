/* 
Simple secure chat client with RSA Digital Envelop encryption.
*/

import java.net.*;
import java.util.Arrays;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.awt.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;

    ObjectInputStream myReader;
    ObjectOutputStream myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
    Socket connection;
    SymCipher cipher;

    public SecureChatClient()
    {
        try 
        {
            
            myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
            serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
            InetAddress addr = InetAddress.getByName(serverName);
            connection = new Socket(addr, PORT);   // Connect to server with new
                                                   // Socket

            // writes bytes to this client Socket's output stream
            myWriter = new ObjectOutputStream(connection.getOutputStream()); 
            myWriter.flush();

            // reads bytes from this client Socket's input stream
            myReader = new ObjectInputStream(connection.getInputStream());

            // -------------------------- handshaking --------------------------
            BigInteger E = (BigInteger) myReader.readObject(); // server's public key, E
            BigInteger N = (BigInteger) myReader.readObject(); // server's public mod value, N
            String cipherType = (String) myReader.readObject();

            System.out.println("Received E : " + E);
            System.out.println("Received N : " + N);
            System.out.println("Cipher type: " + cipherType);

            if (cipherType.equals("Sub"))
            {
                cipher = new Substitute();
            }
            else
            {
                cipher = new Add128();
            }

            // create a sign-magnitude represented BigInteger object from cipher key byte[] array
            BigInteger key = new BigInteger(1, cipher.getKey());
            System.out.println("Symmetric key (byte[])    : " + Arrays.toString(cipher.getKey()));
            System.out.println("Symmetric key (BigInteger): " + key);
            
            // RSA-encrypt key, send resulting BigInteger to server
            myWriter.writeObject(key.modPow(E, N));
            myWriter.flush();

            // Send name to Server.  Server will need
            // this to announce sign-on and sign-off
            // of clients
            myWriter.writeObject(cipher.encode(myName)); // encrypts name using symm cipher
            myWriter.flush();
            System.out.println("---------------------------------Encrypted Message---------------------------------\n" +
                               "Original String       : " + myName + "\n" +
                               "Bytes (pre-encryption): " + Arrays.toString(myName.getBytes()) + "\n" +
                               "Bytes (encrypted)     : " + Arrays.toString(cipher.encode(myName)) + "\n" +
                               "-----------------------------------------------------------------------------------");
            // ------------------------ handshaking end ------------------------

            this.setTitle(myName);      // Set title to identify chatter

            Box b = Box.createHorizontalBox();  // Set up graphical environment for
            outputArea = new JTextArea(8, 30);  // user
            outputArea.setEditable(false);
            b.add(new JScrollPane(outputArea));

            outputArea.append("Welcome to the Chat Group, " + myName + "\n");

            inputField = new JTextField("");  // This is where user will type input
            inputField.addActionListener(this);

            prompt = new JLabel("Type your messages below:");
            Container c = getContentPane();

            c.add(b, BorderLayout.NORTH);
            c.add(prompt, BorderLayout.CENTER);
            c.add(inputField, BorderLayout.SOUTH);

            Thread outputThread = new Thread(this);  // Thread is to receive strings
            outputThread.start();                    // from Server

            addWindowListener(
                    new WindowAdapter()
                    {
                        public void windowClosing(WindowEvent e)
                        {
                            try
                            {
                                // write symm cipher-encrypted "CLIENT CLOSING" string to Server
                                myWriter.writeObject(cipher.encode("CLIENT CLOSING"));
                                System.out.println("---------------------------------Encrypted Message---------------------------------\n" +
                                                   "Original String       : " + "CLIENT CLOSING" + "\n" +
                                                   "Bytes (pre-encryption): " + Arrays.toString("CLIENT CLOSING".getBytes()) + "\n" +
                                                   "Bytes (encrypted)     : " + Arrays.toString(cipher.encode("CLIENT CLOSING")) + "\n" +
                                                   "-----------------------------------------------------------------------------------");
                                System.exit(0);
                            }
                            catch (IOException ioe)
                            {
                                ioe.printStackTrace();
                            }
                        }
                    }
                );

            setSize(500, 200);
            setVisible(true);

        }
        catch (Exception e)
        {
            System.out.println("Problem starting client!");
        }
    }

	// Wait for a message to be received, then show it on the output area
    public void run()
    {
        while (true)
        {
            try {
                byte[] byteMsg = (byte[]) myReader.readObject();
                System.out.println("---------------------------------Decrypted Message---------------------------------\n" +
                                   "Bytes received        : " + Arrays.toString(byteMsg));
                // byte[] byteMsg passed by reference so byteMsg printed to terminal before decryption
                String currMsg = cipher.decode(byteMsg);
                System.out.println("Bytes (decrypted)     : " + Arrays.toString(currMsg.getBytes()) + "\n" +
                                   "Perceived String      : " + currMsg + "\n" +
                                   "-----------------------------------------------------------------------------------");
                outputArea.append(currMsg+"\n");
            }
            catch (Exception e)
            {
                System.out.println(e +  ", closing client!");
                break;
            }
        }
        System.exit(0);
    }

	// Get message typed in from user (from inputField) then add name and send
	// it to the server.
    public void actionPerformed(ActionEvent e)
    {
        String currMsg = e.getActionCommand(); // Get input value
        inputField.setText("");
        try
        {
            System.out.println("---------------------------------Encrypted Message---------------------------------\n" +
                               "Original String       : " + myName + ": " + currMsg + "\n" +
                               "Bytes (pre-encryption): " + Arrays.toString((myName + ": " + currMsg).getBytes()) + "\n" +
                               "Bytes (encrypted)     : " + Arrays.toString(cipher.encode(myName + ": " + currMsg)) + "\n" +
                               "-----------------------------------------------------------------------------------");
            // Add name, encode, and send to Server
            myWriter.writeObject(cipher.encode(myName + ": " + currMsg));
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }   
    }

	// Start things off by creating an ImprovedChatClient object.
    public static void main(String [] args)
    {
        SecureChatClient JR = new SecureChatClient();
        JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}
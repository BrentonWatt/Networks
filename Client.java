/**
 * Created by Brenton on 2/5/2016.
 */
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JOptionPane;


public class Client
{
    private Socket sock;
    private ObjectOutputStream oStream;
    private ObjectInputStream iStream;
    private String uName;
    private String serv;
    private int port;

    public Client(String serv, int port, String uName) throws Exception
    {
        this.serv = serv;
        this.port = port;
        this.uName = uName;
    }

    public boolean start()
    {
        try
        {
            sock = new Socket(serv, port);
        }
        catch (Exception e)
        {
            System.out.println("Connection failure");
            return false;
        }

        System.out.println("Connection successful " + sock.getInetAddress());

        try
        {
            iStream = new ObjectInputStream(sock.getInputStream());
            oStream = new ObjectOutputStream(sock.getOutputStream());
        }
        catch (IOException ie)
        {
            System.out.println("Failure to create streams");
        }

        new Listen().start();

        try
        {
            oStream.writeObject(uName);
        }
        catch (IOException ie)
        {
            System.out.println("Bad connection");
            disconnect();
            return false;
        }
        return true;
    }

    void Send(ChatMessage mess)
    {
        try
        {
            oStream.writeObject(mess);
        }
        catch (IOException ie)
        {
            System.out.println("Failed to send");
        }
    }

    private void disconnect()
    {
        try
        {
            if(oStream != null)
            {
                oStream.close();
            }
        }
        catch (Exception e)
        {

        }
        try
        {
            if(iStream != null)
            {
                iStream.close();
            }
        }
        catch (Exception e)
        {

        }
        try
        {
            if(sock != null)
            {
                sock.close();
            }
        }
        catch (Exception e)
        {

        }
    }

    public static void main(String[] args) throws Exception
            
    {
        Client client = new Client(JOptionPane.showInputDialog("IP"), 5555, JOptionPane.showInputDialog("Name"));
        if(!client.start())
        {
            return;
        }
        Scanner scan = new Scanner(System.in);
        while(true)
        {
            System.out.print("COS332 Chat:>");
            String mess = scan.nextLine();
            if(mess.equalsIgnoreCase("Logout"))
            {
                client.Send(new ChatMessage(ChatMessage.logout, ""));
                break;
            }
            else if(mess.equalsIgnoreCase("Connected"))
            {
                client.Send(new ChatMessage(ChatMessage.connected, ""));
            }
            else
            {
                client.Send(new ChatMessage(ChatMessage.message, mess));
            }
        }
        client.disconnect();
    }

    class Listen extends Thread
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    String mess = (String) iStream.readObject();
                    System.out.print(mess);
                    System.out.print("COS332 Chat:>");
                }
                catch(IOException ie)
                {
                    System.out.println("Closed connection");
                    break;
                }
                catch (ClassNotFoundException cnf)
                {

                }
            }
        }
    }

}

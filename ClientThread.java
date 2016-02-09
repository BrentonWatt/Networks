/**
 * Created by Brenton on 2/3/2016.
 */
import java.net.*;
import java.io.*;
import java.util.Date;

public class ClientThread extends Thread
{
    Socket sock;
    ObjectInputStream iStream;
    ObjectOutputStream oStream;
    public static int id = 0;
    String uName;
    ChatMessage cm;
    String date;
    Server s = new Server(5555);

    ClientThread(Socket sock)
    {
        id++;
        this.sock = sock;
        try
        {
            oStream = new ObjectOutputStream(sock.getOutputStream());
            iStream = new ObjectInputStream(sock.getInputStream());
            uName = (String) iStream.readObject();
            System.out.println(uName + " just connected");
        }
        catch(IOException ie)
        {
            System.out.println(ie);
            return;
        }
        catch (ClassNotFoundException ce)
        {
            return;
        }
        date = new Date().toString()+ "\n";
    }

    public void run()
    {
        boolean done = false;

        while(done == false)
        {
            try
            {
                cm = (ChatMessage) iStream.readObject();
            }
            catch(IOException ie)
            {
                System.out.println(ie);
            }
            catch(ClassNotFoundException ce)
            {
                break;
            }
            String mess = cm.getMessage();
            int type = cm.getType();

            if(type == ChatMessage.connected)
            {
                System.out.println("List of connected users: \n");
                for (int i = 0; i < s.clients.size(); ++i)
                {
                    ClientThread c = s.clients.get(i);
                    writeMessage(c.uName);
                }
            }

            else if(type == ChatMessage.message)
            {
                s.broadcast(uName + ": " + mess);
            }

            else if(type == ChatMessage.logout)
            {
                System.out.println(uName + " Disconnected");
                done = true;
                break;
            }

        }
        s.remove(id);
        close();
    }

    protected void close()
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

    protected boolean writeMessage(String mess)
    {
        if(!sock.isConnected())
        {
            close();
            return false;
        }

        try
        {
            oStream.writeObject(mess);
        }
        catch (IOException ie)
        {
            System.out.println("Error sending " + ie);
        }
        return true;
    }
}

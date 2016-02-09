/**
 * Created by Brenton on 2/3/2016.
 */

import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.text.SimpleDateFormat;

public class Server
{
    protected ArrayList<ClientThread> clients;
    private SimpleDateFormat sdf;
    private int port;
    private boolean unfinished;

    public Server(int port)
    {
        this.port = port;
        sdf = new SimpleDateFormat("HH:mm:ss");
        clients = new ArrayList<ClientThread>();
    }

    public void start()
    {
        unfinished = true;
        try
        {
            ServerSocket ss = new ServerSocket(port);
            while (unfinished)
            {
                System.out.println("Waiting for clients");
                Socket sock = ss.accept();
                if (!unfinished)
                {
                    break;
                }
                ClientThread t = new ClientThread(sock);
                clients.add(t);
                t.start();
            }
            try
            {
                ss.close();
                for (int i = 0; i < clients.size(); ++i)
                {
                    ClientThread tc = clients.get(i);
                    try
                    {
                        tc.iStream.close();
                        tc.oStream.close();
                        tc.sock.close();
                    }
                    catch (IOException ie)
                    {
                        System.out.println(ie);
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
        catch (IOException ie)
        {
            System.out.println(ie);
        }
    }

    protected synchronized void broadcast(String mess)
    {
        String time = sdf.format(new Date());
        String fMess = time + " " + mess + "\n";
        System.out.print(fMess);
        for (int i = clients.size(); --i >= 0;)
        {
            ClientThread ct = clients.get(i);
            if (!ct.writeMessage(fMess))
            {
                clients.remove(i);
                System.out.println("Removed client " + ct.uName);
            }
        }
    }

    synchronized void remove(int id)
    {
        for (int i = 0; i < clients.size(); i++)
        {
            ClientThread ct = clients.get(id);
            if (ct.id == id)
            {
                clients.remove(id);
                return;
            }
        }
    }


    public static void main(String[] args)
    {
        int port = 5555;
        Server server = new Server(port);
        server.start();
    }
}
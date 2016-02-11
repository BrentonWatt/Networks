/**
 * Created by Brenton on 2/3/2016.
 */

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

public class Server
{
    protected ArrayList<ClientThread> clients;
    private SimpleDateFormat sdf;
    private int port;
    private boolean running;

    public Server(int port)
    {
        this.port = port;
        sdf = new SimpleDateFormat("HH:mm:ss");
        clients = new ArrayList<ClientThread>();
    }

    public void start()
    {
        running = true;
        try
        {
            ServerSocket ss = new ServerSocket(port);
            while (running)
            {
                System.out.println("Waiting for clients");
                Socket sock = ss.accept();
                if (!running)
                {
                    break;
                }
                ClientThread t = new ClientThread(sock, this);
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

    protected synchronized void broadcast(String user, String mess)
    {
        String time = sdf.format(new Date());
        String fMess = "<"+time + "> " + user + ": " + mess;
        System.out.println(fMess);
        for (int i = 0; i < clients.size(); i++)
        {
            ClientThread ct = clients.get(i);
            
            if (!ct.writeMessage(fMess))
            {
                clients.remove(ct.id);
                i--;
                System.out.println("Removed client " + ct.uName);
            }                
        }
    }

    synchronized void remove(int id)
    {
        for (int i = 0; i < clients.size(); i++) {
            if(id == clients.get(i).id){
                clients.remove(i);
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
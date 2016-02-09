/**
 * Created by Brenton on 2/4/2016.
 */
import java.net.*;
import java.io.*;
public class ChatMessage implements Serializable
{
    protected static final long serialVersionUID = 1112122200L;
    static final int connected = 0, message = 1, logout = 2;
    private int type;
    private String Message;

    ChatMessage(int type, String Message)
    {
        this.type = type;
        this.Message = Message;
    }

    int getType()
    {
        return type;
    }

    String getMessage()
    {
        return Message;
    }
}

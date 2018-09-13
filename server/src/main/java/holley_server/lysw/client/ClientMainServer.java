package holley_server.lysw.client;

import holley_server.lysw.util.Config;


public class ClientMainServer {

    public static void main(String[] args) throws InterruptedException {
    	new TcpClientServer("127.0.0.1", Config.Server_Port).openClient();
    }
}

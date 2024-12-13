package socketServidor;

import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONObject;

public class Servidor {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Informe a porta a ser ouvida pelo servidor");
            System.exit(0);
        }

        try {
            int port = Integer.parseInt(args[0]);
            System.out.println("Inicializando o servidor...");
            ServerSocket serv = new ServerSocket(port);
            System.out.println("Servidor iniciado, ouvindo a porta " + port);

            while (true) {
                Socket cliente = serv.accept();
                new ServidorThread(cliente).start();
            }
        } catch (Exception e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }
}

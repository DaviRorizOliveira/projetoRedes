package socketCliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import org.json.JSONObject;

public class Cliente {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java Cliente <host> <porta> <nome>");
            System.exit(0);
        }

        try {
            String host = args[0];
            int porta = Integer.parseInt(args[1]);
            String nome = args[2];

            // Conecta ao servidor
            Socket socket = new Socket(host, porta);

            // Streams para comunicação
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            // Envia o comando ao servidor para gerar um número aleatório
            saida.writeUTF("gerarNumero");

            // Recebe a resposta do servidor (que será um JSON)
            String resposta = entrada.readUTF();
            System.out.println("Resposta do servidor: " + resposta);

            saida.close();
            entrada.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Erro no cliente: " + e.getMessage());
        }
    }
}

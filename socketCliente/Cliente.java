package socketCliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java Cliente <host> <porta>");
            System.exit(0);
        }

        try {
            String host = args[0];
            int porta = Integer.parseInt(args[1]);

            // Conecta ao servidor
            Socket socket = new Socket(host, porta);

            // Streams para comunicação
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            // Recebe e exibe o dicionário de operações do servidor
            String dicionario = entrada.readUTF();
            System.out.println("Dicionário de operações do servidor:\n" + dicionario);

            Scanner scanner = new Scanner(System.in);
            String nomeValor;

            // Loop para continuar enviando dados até "exit" ser enviado
            while (true) {
                System.out.print("Digite nome e valor (separados por espaço) ou 'exit' para sair: ");
                nomeValor = scanner.nextLine();

                // Envia o nome e valor ao servidor
                saida.writeUTF(nomeValor);

                // Se for 'exit', encerra a conexão
                if (nomeValor.equals("exit")) {
                    break;
                }

                // Recebe a resposta do servidor
                String resposta = entrada.readUTF();
                System.out.println("Resposta do servidor: " + resposta);
            }

            // Fechar streams e socket
            scanner.close();
            entrada.close();
            saida.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Erro no cliente: " + e.getMessage());
        }
    }
}
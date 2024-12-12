package socketServidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class ServidorThread extends Thread {

    private Socket cliente;

    public ServidorThread(Socket cliente) {
        this.cliente = cliente;
    }

    public void run() {
        try {
            // Streams para comunicação
            DataInputStream entrada = new DataInputStream(cliente.getInputStream());
            DataOutputStream saida = new DataOutputStream(cliente.getOutputStream());

            // Exibição inicial do dicionário de operações
            String dicionario = "{\n" +
                "\t\"operacoes\":{\n" +
                "\t\t\"novoInteiroStr\":{\n" +
                "\t\t\t\"descricao\": \"Retorna um número entre 0 e o valor absoluto de max como String\",\n" +
                "\t\t\t\"parametros\": [\"long max\"]\n" +
                "\t\t},\n" +
                "\t\t\"novoInteiroMax\": {\n" +
                "\t\t\t\"descricao\": \"Retorna um número entre 0 e o valor absoluto de max como long\",\n" +
                "\t\t\t\"parametros\": [\"long max\"]\n" +
                "\t\t},\n" +
                "\t\t\"novoInteiroMinMax\": {\n" +
                "\t\t\t\"descricao\": \"Retorna um número entre min e o valor absoluto de max como long\",\n" +
                "\t\t\t\"parametros\": [\"long min\", \"long max\"]\n" +
                "\t\t},\n" +
                "\t\t\"novoInteiroMinMaxProb\": {\n" +
                "\t\t\t\"descricao\": \"Retorna um número entre 0 e um limite, personalizado com probabilidade\",\n" +
                "\t\t\t\"parametros\": [\"long max1\", \"long chances1\", \"long max2\", \"long chances2\"]\n" +
                "\t\t},\n" +
                "\t\t\"novoInteiroMinMaxProb\": {\n" +
                "\t\t\t\"descricao\": \"Retorna um número não nulo, entre 1 e o valor absoluto de max como long\",\n" +
                "\t\t\t\"parametros\": [\"long max\"]\n" +
                "\t\t},\n" +
                "\t\t\"exit\": {\n" +
                "\t\t\t\"descricao\": \"Encerra a conexão\",\n" +
                "\t\t\t\"parametros\": []\n" +
                "\t\t}\n" +
                "\t}\n" +
            "}";

            // Envia o dicionário para o cliente logo após a conexão
            saida.writeUTF(dicionario);

            // Loop para receber dados continuamente
            while (true) {
                // Lê o nome e o valor enviados pelo cliente (separados por espaço)
                String mensagem = entrada.readUTF();
                System.out.println("Recebido do cliente: " + mensagem);

                // Se o cliente enviar "exit", encerra a conexão
                if (mensagem.equals("exit")) {
                    break;
                }

                // Separar nome e valor
                String[] partes = mensagem.split(" ");
                if (partes.length == 2) {
                    String nome = partes[0];
                    String valor = partes[1];

                    // Envia a resposta com o nome e o valor
                    saida.writeUTF("Servidor recebeu: Nome = " + nome + ", Valor = " + valor);
                } else {
                    saida.writeUTF("Formato inválido. Envie um nome e um valor separados por espaço.");
                }
            }

            // Fechar streams e conexão
            entrada.close();
            saida.close();
            cliente.close();
        } catch (Exception e) {
            System.out.println("Erro na thread do servidor: " + e.getMessage());
        }
    }
}

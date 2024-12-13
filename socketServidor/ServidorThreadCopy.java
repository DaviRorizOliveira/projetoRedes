package socketServidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import org.json.JSONObject;

class ServidorThread extends Thread {

    private Socket cliente;

    public ServidorThread(Socket cliente) {
        this.cliente = cliente;
    }

    public void run() {
        DataInputStream entrada = null;
        DataOutputStream saida = null;
        
        try {
            // Streams para comunicação
            entrada = new DataInputStream(cliente.getInputStream());
            saida = new DataOutputStream(cliente.getOutputStream());

            // Lê o comando enviado pelo cliente
            String comando = entrada.readUTF();
            System.out.println("Comando recebido do cliente: " + comando);

            // Verifica se o comando é para gerar um número aleatório
            if (comando.equals("gerarNumero")) {
                novoInteiroStrJSON(saida);  // Chama a função para gerar o número e enviar o JSON
            } else {
                saida.writeUTF("Comando desconhecido.");
            }

        } catch (Exception e) {
            System.out.println("Erro na thread do servidor: " + e.getMessage());
        } finally {
            try {
                // Garantir que os recursos sejam fechados
                if (entrada != null) entrada.close();
                if (saida != null) saida.close();
                if (cliente != null) cliente.close();
            } catch (Exception e) {
                System.out.println("Erro ao fechar os recursos: " + e.getMessage());
            }
        }
    }

    // Função modificada para enviar o resultado como JSON de volta ao cliente
    private void novoInteiroStrJSON(DataOutputStream saida) throws Exception {
        long max = 1000; // Defina o valor máximo que você deseja
        long result = novoInteiro(max);
        JSONObject json = new JSONObject();
        json.put("operacao", "novoInteiroStr");
        json.put("parametro", result);

        // Envia o JSON gerado para o cliente
        saida.writeUTF(json.toString());
    }

    // Função para gerar um número aleatório
    static public long novoInteiro(long max) {
        return (long) (Math.random() * max);
    }
}

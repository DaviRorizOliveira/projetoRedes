package socketCliente;

import org.json.JSONObject;

public class ClienteTest {
    public static void main(String[] args) {
        try {
            // Configurações do servidor
            String host = "localhost"; // Substitua pelo host do servidor
            int porta = 1234;         // Substitua pela porta correta

            // Cria o cliente e conecta ao servidor
            Cliente cliente = new Cliente(host, porta);

            // Teste de comando válido
            JSONObject comandoValido = new JSONObject();
            comandoValido.put("operacao", "novoInteiroStr");
            comandoValido.put("parametro1", 10);

            JSONObject resposta = cliente.conversarComServidor(comandoValido);
            System.out.println("Resposta para comando válido: " + resposta.toString(4));

            // Teste de comando inválido
            JSONObject comandoInvalido = new JSONObject();
            comandoInvalido.put("operacao", "comandoInvalido");

            JSONObject respostaErro = cliente.conversarComServidor(comandoInvalido);
            System.out.println("Resposta para comando inválido: " + respostaErro.toString(4));

            // Teste para encerrar o cliente
            JSONObject comandoExit = new JSONObject();
            comandoExit.put("operacao", "exit");

            cliente.conversarComServidor(comandoExit);

        } catch (Exception e) {
            System.out.println("Erro no ClienteTest: " + e.getMessage());
        }
    }
}
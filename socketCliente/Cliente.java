package socketCliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;

import org.json.JSONObject;

public class Cliente {  
    private String host;
    private int porta;
    
    private Socket socket;

    private DataOutputStream saida;
    private DataInputStream entrada;

    private String[] comandosPossiveis;

    public Cliente(String host, int porta) {
        this.host = host;
        this.porta = porta;

        this.socket = this.conectarComServidor();
    }

    public JSONObject lerServidor(){
        try {
            String entrada = this.entrada.readUTF();
            return new JSONObject(entrada);
        } catch (Exception e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Socket conectarComServidor() {
        try {
            // Conecta ao servidor
            Socket socket = new Socket(this.host, this.porta);

            // Streams para comunicação
            this.saida = new DataOutputStream(socket.getOutputStream());
            this.entrada = new DataInputStream(socket.getInputStream());

            // Recebe os comandos disponíveis e os armazena em um array de strings
            JSONObject operacoesField = this.lerServidor();
            String[] chavesOperacoes = operacoesField.keySet().toArray(new String[0]);

            this.comandosPossiveis = chavesOperacoes;

            return socket;
        } catch (Exception e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean ehComandoValido(String comando){
        return !Arrays.asList(this.comandosPossiveis).contains(comando);
    }

    public JSONObject geraErro(String msgErro, String comando) {
        JSONObject erro = new JSONObject();
        erro.put("status", "error");
        erro.put("details", msgErro);
        erro.put("comando", comando);
        return erro;
    }

    public String pegaComando(JSONObject request){
        return request.getString("operacao");
    }

    public JSONObject conversarComServidor(JSONObject request) {
        String comando = this.pegaComando(request);

        try {
            if(!this.ehComandoValido(comando)){
                return this.geraErro("Comando inválido", comando);
            }

            // Envia a requisição ao servidor
            saida.writeUTF(request.toString());

            if (this.comandosPossiveis[this.comandosPossiveis.length-1].equalsIgnoreCase(comando)) {
                // Fecha recursos
                saida.close();
                entrada.close();
                socket.close();
                return new JSONObject();
            } 

            return this.lerServidor();
        } catch (Exception e) {
            System.out.println("Erro no cliente: " + e.getMessage());
            return this.geraErro(e.getMessage(), comando);
        }
    }
}
package socketServidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.json.JSONArray;


import java.util.Random;

class ServidorThread extends Thread {
    static private Random gerador = new Random();

    private Socket cliente;
    private ScheduledExecutorService timer;
    DataInputStream entrada;
    DataOutputStream saida;

    public ServidorThread(Socket cliente) {
        this.cliente = cliente;
        this.timer = Executors.newSingleThreadScheduledExecutor();

        try{
            this.entrada = new DataInputStream(this.cliente.getInputStream());
            this.saida = new DataOutputStream(this.cliente.getOutputStream());

            // Envia a lista de operações disponíveis
            JSONObject allOpcs = ServidorThread.generateOperationsJson();
            this.saida.writeUTF(allOpcs.toString(4));

            // Configura o timer para encerrar após 60 segundos de inatividade
            this.resetaTimer();

            this.escutarCliente();
        } catch (Exception e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static JSONObject generateOperationsJson() {
        JSONObject operacoes = new JSONObject();

        addOperation(operacoes, "novoInteiroStr",
                "Retorna um número entre 0 e o valor absoluto de max como String",
                new String[]{"long max"});

        addOperation(operacoes, "novoInteiroMax",
                "Retorna um número entre 0 e o valor absoluto de max como long",
                new String[]{"long max"});

        addOperation(operacoes, "novoInteiroMinMax",
                "Retorna um número entre min e o valor absoluto de max como long",
                new String[]{"long min", "long max"});

        addOperation(operacoes, "novoInteiroMinMaxProb",
                "Retorna um número entre 0 e um limite, personalizado com probabilidade",
                new String[]{"long max1", "long chances1", "long max2", "long chances2"});

        addOperation(operacoes, "novoInteiroNL",
                "Retorna um número não nulo, entre 1 e o valor absoluto de max como long",
                new String[]{"long max"});

        addOperation(operacoes, "exit",
                "Encerra a conexão",
                new String[]{});

        JSONObject root = new JSONObject();
        root.put("operacoes", operacoes);

        return root;
    }

    private static void addOperation(JSONObject operacoes, String name, String descricao, String[] parametros) {
        JSONObject operation = new JSONObject();
        operation.put("descricao", descricao);
        operation.put("parametros", new JSONArray(parametros));
        operacoes.put(name, operation);
    }

    public void resetaTimer(){
        resetTimer(() -> encerrarConexao(entrada, saida));
    }

    private void escutarCliente() {
        try {
            while (true) {
                // Lê a requisição do cliente
                String requisicao = entrada.readUTF();
    
                // Reseta o timer a cada interação
                resetTimer(() -> encerrarConexao(entrada, saida));
    
                if ("exit".equalsIgnoreCase(requisicao)) {
                    System.out.println("Cliente solicitou desconexão.");
                    encerrarConexao(entrada, saida);
                    break;
                }
    
                // Processa a requisição recebida
                JSONObject resposta = processarRequisicao(new JSONObject(requisicao));
    
                // Envia a resposta ao cliente
                saida.writeUTF(resposta.toString());
            }
        } catch (Exception e) {
            System.out.println("Erro na thread do servidor: " + e.getMessage());
            encerrarConexao(entrada, saida);
        }
    }

    public String pegaComando(JSONObject request){
        return request.getString("operacao");
    }

    public JSONObject geraErro(String msgErro, String comando) {
        JSONObject erro = new JSONObject();
        erro.put("status", "error");
        erro.put("details", msgErro);
        erro.put("comando", comando);
        return erro;
    }

    private JSONObject processarRequisicao(JSONObject requisicao) {
        String comando = this.pegaComando(requisicao);

        switch (comando) {
            case "novoInteiroStr":
                return this.criarResposta(comando, 12);
            case "novoInteiroMax":
                return this.novoInteiroMax(requisicao);
            case "novoInteiroMinMax":
                return this.criarResposta(comando, 12);
            case "novoInteiroMinMaxProb":
                return this.criarResposta(comando, 12);
            case "novoInteiroNL":
                return this.criarResposta(comando, 12);
            case "exit":
                return this.criarResposta(comando, 12);
            default:
                return this.geraErro("Comando inválido", comando);
        }
    }

    public JSONObject criarResposta(String comando, Object resultado) {
        JSONObject resposta = new JSONObject();
        resposta.put("status", "sucesso");
        resposta.put("operacao", comando);
        resposta.put("resultado", resultado);
        return resposta;
    }

    private void resetTimer(Runnable onTimeout) {
        timer.shutdownNow();
        timer = Executors.newSingleThreadScheduledExecutor();
        timer.schedule(onTimeout, 60, TimeUnit.SECONDS);
    }

    private void encerrarConexao(DataInputStream entrada, DataOutputStream saida) {
        try {
            System.out.println("Conexão encerrada com o cliente: " + cliente.getInetAddress());
            entrada.close();
            saida.close();
            cliente.close();
            timer.shutdownNow();
        } catch (Exception e) {
            System.out.println("Erro ao encerrar a conexão: " + e.getMessage());
        }
    }

    public JSONObject novoInteiroMax(JSONObject requisicao){
        long max = requisicao.getLong("parametro1");
        /*
         * Como novoInteiroMinMax vai receber a requisição como parâmetro não coube puxar a função aqui
         * Implementei a equação de MinMax aqui, com min = 0
         */
        long resultado = (long) (gerador.nextDouble() * max);

        JSONObject resposta = criarResposta(this.pegaComando(requisicao), resultado);

        return resposta;
    }

    static public long novoInteiro(long min, long max){
        return (long)(gerador.nextDouble() * max-min)+min;
    }
}

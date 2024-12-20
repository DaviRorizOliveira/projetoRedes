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
        resetTimer(() -> encerrarConexao());
    }

    private void escutarCliente() {
        try {
            while (true) {
                // Lê a requisição do cliente
                String requisicao = entrada.readUTF();
    
                // Reseta o timer a cada interação
                resetTimer(() -> encerrarConexao());
    
                // Processa a requisição recebida
                JSONObject resposta = processarRequisicao(new JSONObject(requisicao));
    
                // Envia a resposta ao cliente
                saida.writeUTF(resposta.toString());
            }
        } catch (java.io.EOFException eof) {
            System.out.println("Cliente encerrou a conexão.");
        } catch (Exception e) {
            System.out.println("Erro na thread do servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            encerrarConexao(); // Sempre encerra a conexão no final
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
        System.err.println("Comando: "+ comando);

        switch (comando) {
            case "novoInteiroStr":
                return this.novoInteiroStrJSON(requisicao);
            case "novoInteiroMax":
                return this.novoInteiroMax(requisicao);
            case "novoInteiroMinMax":
                return this.novoInteiroMinMax(requisicao);
            case "novoInteiroMinMaxProb":
                return this.novoInteiroMinMaxProb(requisicao);
            case "novoInteiroNL":
                return this.novoInteiroNlJSON(requisicao);
            case "exit":
                this.agendaFechaCon();
                return this.criarResposta(comando, null);
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

    private void agendaFechaCon() {
        timer.shutdownNow();
        timer = Executors.newSingleThreadScheduledExecutor();
        timer.schedule(()->this.encerrarConexao(), 3, TimeUnit.SECONDS);
    }

    private void encerrarConexao() {
        try {
            if (entrada != null) {
                entrada.close();
            }
            if (saida != null) {
                saida.close();
            }
            if (cliente != null && !cliente.isClosed()) {
                cliente.close();
            }
            if (timer != null && !timer.isShutdown()) {
                timer.shutdownNow();
            }
            System.out.println("Conexão encerrada com o cliente: " + cliente.getInetAddress());
        } catch (Exception e) {
            System.out.println("Erro ao encerrar a conexão: " + e.getMessage());
            e.printStackTrace();
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

    /** -------------------- Davi Funções -------------------- */

    public JSONObject novoInteiroMinMax(JSONObject requisicao) {
        long min = requisicao.getLong("parametro1");
        long max = requisicao.getLong("parametro2");
        long resultado = (long)(gerador.nextDouble() * (max - min)) + min;

        JSONObject resposta = new JSONObject();
        resposta.put("status", "sucesso");
        resposta.put("operacao", "novoInteiroMinMax");
        resposta.put("resultado", resultado);
        return resposta;
    }

    /** -------------------- Joabe Funções -------------------- */

    /** Retorna um número entre 0 e o valor absoluto de max como String */
    static public String novoInteiroStr(long max) {
        return String.valueOf(novoInteiro(max));
    }

    /** Retorna um número entre min e o valor absoluto de max como long */
    static public long novoInteiro(long min, long max) {
        return (long)(gerador.nextDouble() * (max - min)) + min;
    }

    /** Retorna um número entre 0 e o valor absoluto de max como long */
    static public long novoInteiro(long max) {
        return novoInteiro(0, max);
    }

    /** Retorna um número entre 0 e o valor absoluto de max como String */
    public JSONObject novoInteiroStrJSON(JSONObject requisicao) {
        long max = requisicao.getLong("parametro1");
        
        long result = novoInteiro(max);
        if (result < 0)
            return this.geraErro("Valor Inválido", String.valueOf(max));
        JSONObject json = new JSONObject();
        json.put("status", "sucesso");
        json.put("operacao", "novoInteiroStr");
        json.put("resultado", String.valueOf(result));
        return json;
    }
    
    /** Retorna um número não nulo, entre 1 e o valor absoluto de max como long */
    static public long novoInteiro_nl(long max) {
        return novoInteiro(1, max);
    }

    public JSONObject novoInteiroMinMaxProb(JSONObject requisicao){
        String comando = this.pegaComando(requisicao);
        try{
            long max1 = requisicao.getLong("parametro1");
            long chances1 = requisicao.getLong("parametro2");
            long max2 = requisicao.getLong("parametro3");
            long chances2 = requisicao.getLong("parametro4");

            long resultado = ServidorThread.novoInteiro(max1, chances1, max2, chances2);
            return this.criarResposta(comando, resultado);
        } catch (Exception e) {
            return this.geraErro(e.getMessage(), comando);
        }
    }

    static public long novoInteiro(long max1, long chances1, long max2, long chances2){
        long ret, valorMenor = ServidorThread.novoInteiro(max1), valorMaior = ServidorThread.novoInteiro(max1+1, max2);

        if (ServidorThread.novoInteiro_nl(chances1+chances2)<=chances1) ret = valorMenor;
        else ret = valorMaior;

        return ret;
    }

    /** Retorna um número entre 0 e o valor absoluto de max como String */
    public JSONObject novoInteiroNlJSON(JSONObject requisicao) {
        long max = requisicao.getLong("parametro1");

        long result = novoInteiro_nl(max);
        if (result < 0)
            return this.geraErro("Valor Inválido", String.valueOf(max));
        JSONObject json = new JSONObject();
        json.put("status", "sucesso");
        json.put("operacao", "novoInteiroNl");
        json.put("resultado", result);
        return json;
    }
}
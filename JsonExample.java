import org.json.JSONObject;

public class JsonExample {
    public static void main(String[] args) {
        // Criando um objeto JSON vazio
        JSONObject json = new JSONObject();

        // Adicionando pares chave-valor
        json.put("nome", "João");
        json.put("idade", 30);
        json.put("cidade", "São Paulo");

        // Adicionando um objeto JSON aninhado
        JSONObject endereco = new JSONObject();
        endereco.put("rua", "Avenida Paulista");
        endereco.put("numero", 1000);
        json.put("endereco", endereco);

        // Adicionando um array ao JSON
        json.put("hobbies", new String[]{"futebol", "música", "programação"});

        // Exibindo o JSON criado
        System.out.println("JSON Criado:");
        System.out.println(json.toString()); // Exibe o JSON formatado com indentação de 4 espaços
    }
}

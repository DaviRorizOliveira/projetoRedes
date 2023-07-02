package jogadores;

import add.Data;
import add.AuxLib;
import contas.ContaPessoaJuridica;

public class JogadorPJ extends Jogador {
    private String cnpj;
    private ContaPessoaJuridica conta;
    static int TAMANHO_DOCUMENTO = 14; //cnpj

    static public String geraCNPJ(){
        String cnpj="";

        // gera um cnpj com 14 dígitos
        // xx xxx xxx 0001 xx
        for(int x=1; x<=TAMANHO_DOCUMENTO; x++){
            if(x>=9 && x < 12) cnpj+="0";
            else if (x==12) cnpj+="1";
            else cnpj+=AuxLib.novoInteiroStr(9);
        }

        return cnpj;
    }

    public static boolean validaCNPJ(String cnpj){
        if(cnpj.length()!=TAMANHO_DOCUMENTO || !AuxLib.ehApenasNumero(cnpj)){
            System.out.println("\nCNPJ não é válido. Precisa ter "+ TAMANHO_DOCUMENTO +" números");
            return false;
        } else {
            return true;
        }
    }

    private String toStringCNPJ() {
        String cnpjFormatado;

        // Adiciona os pontos, barra e traço de formatação
        cnpjFormatado = cnpj.substring(0, 2);
        cnpjFormatado += ".";
        cnpjFormatado += cnpj.substring(2, 5);
        cnpjFormatado += ".";
        cnpjFormatado += cnpj.substring(5, 8);
        cnpjFormatado += "/";
        cnpjFormatado += cnpj.substring(8, 12);
        cnpjFormatado += "-";
        cnpjFormatado += cnpj.substring(12);

        return cnpjFormatado;
    }

    public JogadorPJ(String nome, Data nasc, String localNasc, ContaPessoaJuridica conta, String cnpj){
        super(nome+" Co.", nasc, localNasc);
        if(validaConta(conta) && validaCNPJ(cnpj)){
            this.conta = conta;
            this.cnpj = cnpj;
        } else {
            System.out.println("Não foi possível criar o jogador pessoa jurídica");
        }
    }

    public float getLimiteCredito() {
        return conta.getLimiteCredito();
    }

    @Override
    public float getSaldo() {
        return conta.getSaldo();
    }

    @Override
    public boolean sacar(float valor, String senha) {
        return conta.sacar(valor, senha);
    }

    @Override
    public boolean depositar(float valor) {
        return conta.depositar(valor);
    }

    @Override
    public String toString(){
        String txt;

        txt = super.toString();
        txt+= "Seu CNPJ é: " + toStringCNPJ() +"\n";        
        txt+= conta.toString();

        return txt;
    }
}

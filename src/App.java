
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileReader;
import java.util.Random;

public class App {
    //Ferramentas de entrada e saída
    private static final BufferedReader IN = new BufferedReader(new InputStreamReader(System.in));
    private static final BufferedWriter OUT = new BufferedWriter(new OutputStreamWriter(System.out));

    public static void main(String[] args) throws Exception {
        //Variáveis
        int opcao = 0, xau = 0;

        // Menu
        while (true) {
            do {
                cls(0);
                menuPrincipal();
                opcao = inputInt('1', '3');
            } while (opcao == -1);

            //Opcões do menu
            switch(opcao) {
                case(1): {
                    int C = 0; //Controle geral
                    String[] palavra = sortearPalavra();
                    String mask_palavra = "";
                    for (C = 0; C < palavra[0].length(); C++) {
                        mask_palavra += "_";
                    }
                    String letras_foram = "";
                    char letra = '\0';
                    int erros = 0;
                    int estado;

                    //Jogo em si
                    while (true) {
                        do {
                            cls(0);
                            exibirForca(mask_palavra, palavra[1], letras_foram, erros);
                            letra = inputChar();
                        } while (letra == '8');
                        //-1 -> erro, 0 -> não existe/já foi, 1 -> certo
                        estado = checarLetra(letra, letras_foram, palavra[0]);
                        
                        //Resultados da ação do jogador
                        switch(estado) {
                            case(1): {
                                letras_foram += letra;
                                StringBuilder sb = new StringBuilder(mask_palavra);
                                for (C = 0; C < palavra[0].length(); C++) {
                                    if (letra == palavra[0].charAt(C)) {
                                        sb.setCharAt(C, letra);
                                        mask_palavra = sb.toString();
                                    }
                                }
                                break;
                            }
                            case(-1): {
                                letras_foram += letra;
                                erros++;
                                break;
                            }
                        }

                        //Finais do Jogo
                        if (mask_palavra.equals(palavra[0])) {
                            cls(0);
                            exibirForca(mask_palavra, palavra[1], letras_foram, erros);
                            OUT.write(" MEUS PARABÉNS!!!\n VOCÊ GANHOU!\n");
                            OUT.flush();
                            cls(-1);
                            break;
                        }
                        if (erros == 6) {
                            cls(0);
                            exibirForca(mask_palavra, palavra[1], letras_foram, erros);
                            OUT.write(" Sinto muito, acabaram suas chances...\n");
                            OUT.flush();
                            cls(-1);
                            break;
                        }
                    }
                    break;
                }
                case(2): {
                    creditos();
                    break;
                }
                case(3): {
                    xau = 1;
                    break;
                }
            }

            if (xau == 1) break;
        }
        cls(0);
        OUT.close();
        IN.close();
    }

    public static void cls(int esperar) throws Exception { //Apagar a tela (pausar)
        if (System.getProperty("os.name").contains("Windows")) {
            if (esperar > 0) {
                for (int C = 1; C <= esperar;C++)
                    new ProcessBuilder("cmd", "/c", "timeout 1 >> cach").inheritIO().start().waitFor();
                new ProcessBuilder("cmd", "/c", "del cach").inheritIO().start().waitFor();
            } else if (esperar <= -1) {
                OUT.write("\n Pressione qualquer tecla para continuar...");
                OUT.flush();
                new ProcessBuilder("cmd", "/c", "pause > cach").inheritIO().start().waitFor();
                new ProcessBuilder("cmd", "/c", "del cach").inheritIO().start().waitFor();
            }
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            
        }
    }

    public static int inputInt(char min, char max) throws Exception { //Input números inteiros
        OUT.write(" <: ");
        OUT.flush();
        String digitado = IN.readLine();
        if (digitado.length() == 0) return -1;
        if ((digitado.charAt(0) < min) || (digitado.charAt(0) > max))
            return -1;
        return Integer.parseInt(digitado);
    }
    
    public static char inputChar() throws Exception { //Input números inteiros
        OUT.write(" <: ");
        OUT.flush();
        String digitado = IN.readLine();
        digitado = digitado.toUpperCase();
        if (digitado.length() == 0) return '8';
        if ((digitado.charAt(0) < 'A') || (digitado.charAt(0) > 'Z'))
            return '8';
        return digitado.charAt(0);
    }

    public static void creditos() throws Exception {
        cls(0);
        menuPrincipal();
        OUT.write(" Feito por: Wesley T. Benette\n");
        OUT.write(" ~ 17/03/2023\n\n");
        OUT.flush();
        cls(3);
    }

    public static void menuPrincipal() throws Exception {
        cls(0);
        OUT.write(
            "# JOGO DA FORCA #\n" +
            " (escolha uma das opções)\n\n" +
            " 1 - Iniciar\n" +
            " 2 - Créditos\n" +
            " 3 - Sair\n\n"
        );
        OUT.flush();
    }

    public static String[] sortearPalavra() throws Exception {
        //Abrir arquivo
        FileReader arq = new FileReader("src\\Palavras.txt");
        BufferedReader ler_arquivo = new BufferedReader(arq);

        //Ler palavras do arquivo
        String input_dado = "";
        String[] dados = new String[50];
        String[][] palavras = new String[30][30];
        int F = 0;
        String linha = "";

        for (int C = 0;;C++) { //Ler o arquivo
            input_dado = ler_arquivo.readLine();
            if (input_dado != null) {
                dados[C] = input_dado;
            } else {
                arq.close();
                break;
            }
        }
        
        //Tratamento de Dados
        for (int C = 0;;C++) { //Separar Palavra e Dica
            if (dados[C] != null) {
                F = 0;
                linha = dados[C];
                palavras[C][0] = "";
                palavras[C][1] = "";
                for (;;F++) { //Palavra
                    if (linha.charAt(F) != ':') {
                        palavras[C][0] += linha.charAt(F);
                    } else {
                        F++;
                        break;
                    }
                }
                for (;;F++) { //Dica
                    if (linha.charAt(F) != ';') {
                        palavras[C][1] += linha.charAt(F);
                    } else {
                        break;
                    }
                }
            } else {
                break;
            }
        }

        //Escolher aleatóriamente
        int tamanho_index = 0;
        for (int C = 0;;C++) {
            if (dados[C] != null) {
                tamanho_index = C;
            } else {
                tamanho_index++;
                break;
            }
        }
        Random rand = new Random();
        int index_ale = rand.nextInt(tamanho_index);
        String palavra = palavras[index_ale][0];
        String dica = palavras[index_ale][1];

        return new String[] {palavra.toUpperCase(), dica.toUpperCase()};
    }

    public static void exibirForca(String mask, String dica, String foram, int erros) throws Exception {
        OUT.write("\n Já foram:");
        for (int c = 0; c < foram.length(); c++) {
            OUT.write(" " + foram.charAt(c));
        }
        OUT.write(
            "\n Erros: " + erros + "/6\n\n" +
            " Dica: " + dica + "\n\n"
        );
        OUT.write(" ______\n");
        OUT.write(" |    |\n");
        OUT.write(" |");
        if (erros >= 1) OUT.write("    o\n"); else OUT.write("\n");
        OUT.write(" |");
        if (erros >= 3) OUT.write("   /"); else OUT.write("    ");
        if (erros >= 2) OUT.write("|");
        if (erros >= 4) OUT.write("\\\n"); else OUT.write("\n");
        OUT.write(" |");
        if (erros >= 5) OUT.write("   /");
        if (erros >= 6) OUT.write(" \\\n"); else OUT.write("\n");
        OUT.write(" |\n");
        OUT.write(" |");
        for (int C = 0; C < mask.length();C++) {
            OUT.write(" " +mask.charAt(C));
        }
        OUT.write("\n\n");
        OUT.flush();
    }

    public static int checarLetra(char letra, String foram, String palavra) {

        //É valido?
        if ((((letra >= 'A') && (letra <= 'Z')) || (letra == 'Ç') || (letra == '-')) == false) {
            return 0;
        }

        //Já foi?
        for (int C = 0;C < foram.length();C++) {
            if (letra == foram.charAt(C)) {
                return 0;
            }
        }

        //Acertou ou Errou?
        for (int C = 0; C < palavra.length();C++) {
            if (palavra.charAt(C) == letra) {
                return 1;
            }
            if ((C+1) == palavra.length()) return (-1);
        }

        return 0; //-1 -> erro, 0 -> não existe/já foi, 1 -> certo
    }
}


//import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Judge {

    private File userFile;
    private ArrayList<File> outputsExpecteds;
    private ArrayList<File> outputsUser;
    private ArrayList<File> inputs;
  
    public static void main(String[] args) {
        Judge judge = new Judge();
        judge.compilar();
        boolean result = judge.verifyDiff();

        if (result) {
            System.out.println("Parabéns, você passou em todos os testes!");
        } else {
            System.out.println("Você não passou em todos os testes, tente novamente!");
        }

        //judge.destroyArquivos();
    }

    public Judge() {
        userFile = new File("./Question.cpp");
        outputsExpecteds = new ArrayList<File>();
        outputsUser = new ArrayList<File>();
        inputs = new ArrayList<File>();

        File pasta = new File("./Inputs");
        if(pasta.isDirectory() && pasta.exists()) {
            File[] files = pasta.listFiles();
            for(File file : files) {
                inputs.add(file);
            }
        }

        pasta = new File("./OutputsExpecteds");
        if(pasta.isDirectory() && pasta.exists()) {
            File[] files = pasta.listFiles();
            for(int i = files.length - 1; i >= 0; i--) {
                outputsExpecteds.add(files[i]);
            }
        }
    }

    public void compilar() {
        long tempoInicial = System.currentTimeMillis();
        long tempoFinal = 0;
        try {
            ProcessBuilder pbCompilacao = new ProcessBuilder("g++", userFile.getName(), "-o", "question");
            pbCompilacao.directory(userFile.getParentFile());
            pbCompilacao.redirectError(new File("error.txt"));
            File error = new File("./error.txt");
            Process process = pbCompilacao.start();
            process.waitFor();
            if (error.length() == 0) {
                for (int i = 0; i < inputs.size(); ++ i) {
                    ProcessBuilder pbExecucao = new ProcessBuilder("./question");
                    pbExecucao.redirectInput(inputs.get(i));
                    pbExecucao.redirectOutput(new File("./OutputsUser", "userOut0" + (i + 1) + ".out"));
                    Process processExecucao = pbExecucao.start();
                    processExecucao.waitFor();
                }
        
            } else {
                System.out.println("Não compilou");
            }
            tempoFinal = System.currentTimeMillis();
            System.out.println("Executado em = " + (tempoFinal - tempoInicial) + " ms");
        } catch (IOException e) {
            System.out.println("Erro de I/O" + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Erro de interrupção" + e.getMessage());
        }
    }

        public void carregarUserOutputs() {
            File pasta = new File("./OutputsUser");
            if(pasta.isDirectory() && pasta.exists()) {
                File[] files = pasta.listFiles();
                for(File file : files) {
                    if(file.getName().endsWith(".out")) outputsUser.add(file);
                }
            }
        }

        public boolean verifyDiff() {
            carregarUserOutputs();
            
            for (int i = 0; i < outputsUser.size(); ++ i) {
                String pathOutputUser = outputsUser.get(i).getAbsolutePath();
                String pathOutputExpected = outputsExpecteds.get(i).getAbsolutePath();
                try {
                    System.out.println("Comparando " + outputsExpecteds.get(i).getName() + " com " + outputsUser.get(i).getName());
                    ProcessBuilder pbDiff = new ProcessBuilder("diff", pathOutputExpected, pathOutputUser);
                    pbDiff.redirectOutput(new File("./Diffs", "diff0" + (i + 1) + ".out"));
                    Process pDiff = pbDiff.start();
                    pDiff.waitFor();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            File pasta = new File("./Diffs");
            if(pasta.isDirectory() && pasta.exists()) {
                File[] files = pasta.listFiles();
                for(File file : files) {
                    if(file.length() != 0) return false;
                }
            }

            return true;
        }

    public void destroyArquivos() {
        File pasta = new File("./OutputsUser");
        if(pasta.isDirectory() && pasta.exists()) {
            File[] files = pasta.listFiles();
            for(File file : files) {
                file.delete();
            }
        }

        pasta = new File("./Diffs");
        if(pasta.isDirectory() && pasta.exists()) {
            File[] files = pasta.listFiles();
            for(File file : files) {
                file.delete();
            }
        }

        File error = new File("./error.txt");
        error.delete();

        File question = new File("./question");
        question.delete();

        //delete inputs
        pasta = new File("./Inputs");
        if(pasta.isDirectory() && pasta.exists()) {
            File[] files = pasta.listFiles();
            for(File file : files) {
                file.delete();
            }
        }

        //delete outputs expecteds
        pasta = new File("./OutputsExpecteds");
        if(pasta.isDirectory() && pasta.exists()) {
            File[] files = pasta.listFiles();
            for(File file : files) {
                file.delete();
            }
        }
    }
}

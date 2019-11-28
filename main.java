import java.io.*;
import java.util.ArrayList;

public class main {
    public static void main( String[] args ) throws IOException {
        File file = new File(args[0]);
        ArrayList<String> source = new ArrayList<String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                source.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bytecode bc = new Bytecode(source);
        ArrayList<Integer> out = bc.compile();
        System.out.println(out);
        DataOutputStream outputWriter = new DataOutputStream(new FileOutputStream("outfile"));
        for (int i : out) {
            outputWriter.write(i);
        }
        outputWriter.close();
    }
}

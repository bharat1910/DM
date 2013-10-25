import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class GenerateDictionary
{
	private void run() throws IOException
	{
		Set<String> vocab = new HashSet<>();
		String str;
		String[] strList;
		
		BufferedReader br = new BufferedReader(new FileReader("src/paper.txt"));
		while ((str = br.readLine()) != null) {
			strList = str.split(" |\t");
			for (int i=1; i<strList.length; i++) {
				vocab.add(strList[i]);
			}
		}
		br.close();

		BufferedWriter bw = new BufferedWriter(new FileWriter("src/vocab.txt"));
		for (String s : vocab) {
			bw.write(s + "\n");
		}
		bw.close();	
	}
	
	public static void main(String[] args) throws IOException
	{
		GenerateDictionary main = new GenerateDictionary();
		main.run();
		System.exit(0);		
	}
}
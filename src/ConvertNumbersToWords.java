import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConvertNumbersToWords
{
	List<String> vocab = new ArrayList<>();
	
	private void generatePhraseFiles(String folderName, String fileName) throws IOException
	{
		String str;
		String[] strList;
		
		for (int i=0; i<5; i++) {
			BufferedReader br = new BufferedReader(new FileReader("src/" + folderName + "/" + fileName + "-" + i + ".txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter("src/" + folderName + "/" + fileName + "-" + i + ".txt.phrase"));
			
			while ((str = br.readLine()) != null) {
				strList = str.split(" ");
				str = "";
				
				str += strList[0] + " ";
				
				for (int j=1; j<strList.length; j++) {
					str += vocab.get(Integer.parseInt(strList[j])) + " ";
				}
				
				bw.write(str.trim() + "\n");
			}
		
			br.close();
			bw.close();
		}
	}
	
	private void run() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("src/vocab.txt"));
		String str;
		
		while ((str = br.readLine()) != null) {
			vocab.add(str);
		}
		
		br.close();
		
		generatePhraseFiles("patterns", "pattern");
		generatePhraseFiles("max", "max");
		generatePhraseFiles("closed", "closed");
		generatePhraseFiles("purity", "purity");
		generatePhraseFiles("bonus", "bonus");
	}
	
	public static void main(String[] args) throws IOException
	{
		ConvertNumbersToWords main = new ConvertNumbersToWords();
		main.run();
		System.exit(0);
	}
}
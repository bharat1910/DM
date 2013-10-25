import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenizeText
{
	private void run() throws IOException
	{
		List<String> vocab = new ArrayList<>();
		Map<String, Integer> vocabCountPerLine = new HashMap<>();
		String str, output;
		String[] strList;
		
		BufferedReader br = new BufferedReader(new FileReader("src/vocab.txt"));
		while ((str = br.readLine()) != null) {
			vocab.add(str);
		}
		br.close();
		
		br = new BufferedReader(new FileReader("src/paper.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("src/title.txt"));
		while ((str = br.readLine()) != null) {
			vocabCountPerLine.clear();
			output = "";
			strList = str.split(" |\t");
			
			for (int i=1; i<strList.length; i++) {
				if (!vocabCountPerLine.containsKey(strList[i])) {
					vocabCountPerLine.put(strList[i], 0);
				}
				vocabCountPerLine.put(strList[i], vocabCountPerLine.get(strList[i]) + 1);
			}
			
			output += vocabCountPerLine.keySet().size() + " ";
			for (String s : vocabCountPerLine.keySet()) {
				output += vocab.indexOf(s) + ":" + vocabCountPerLine.get(s) + " ";
			}
			bw.write(output.trim() + "\n");
		}
		br.close();
		bw.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		TokenizeText main = new TokenizeText();
		main.run();
		System.exit(0);		
	}
}
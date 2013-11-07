import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;

public class ReorganizeTermsByTopic
{
	public static Comparator<String> stringSortComparator = new Comparator<String>() {
		@Override
		public int compare(String arg0, String arg1) {
			return Integer.parseInt(arg0) - Integer.parseInt(arg1);
		}
	};
	
	private void run() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("src/result/word-assignments.dat"));
		BufferedWriter[] bw = new BufferedWriter[5];
		String[] stri = new String[5], strList, tokens;
		String str;
		int temp;
		
		for (int i=0; i<5; i++) {
			bw[i] = new BufferedWriter(new FileWriter("src/topic-" + i + ".txt"));
		}
		
		while ((str = br.readLine()) != null) {
			for (int i=0; i<5; i++) {
				stri[i] = "";
			}
			
			strList = str.split(" ");
			for (int i=1; i<strList.length; i++) {
				tokens = strList[i].split(":");
				temp = Integer.parseInt(tokens[1]);
				stri[temp] += tokens[0] + " ";
			}

			for (int i=0; i<5; i++) {
				if (!stri[i].equals("")) {
					bw[i].write(stri[i] + "\n");
				}
			}
		}
		
		br.close();
		for (int i=0; i<5; i++) {
			bw[i].close();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		ReorganizeTermsByTopic main = new ReorganizeTermsByTopic();
		main.run();
		System.exit(0);		
	}
}
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenerateMaxPatterns {
	
	HashMap<String, Integer> maxPatterns;
	
	private boolean isSubset(String s1, String s2)
	{
		String[] s1List = s1.split(" ");
		String[] s2List = s2.split(" ");
		
		int i=0, j=0;
		while (i < s1List.length && j < s2List.length) {
			if (s1List[i].equals(s2List[j])) {
				i++;
				j++;
			} else {
				j++;
			}
		}
		
		if (i == s1List.length) {
			return true;
		} else {
			return false;
		}
	}
	
	private void removeSubsets(String str)
	{
		List<String> subsets = new ArrayList<>();
		for (String s : maxPatterns.keySet()) {
			if (isSubset(s, str)) {
				subsets.add(s);
			}
		}
		
		for (String s : subsets) {
			maxPatterns.remove(s);
			System.out.println("Removed : " + s);
		}
	}
	
	private void generateMaxPatterns(int index) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("src/patterns/pattern-" + index + ".txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("src/max/max-" + index + ".txt"));
		String str;
		String[] strList;
		
		while((str = br.readLine()) != null) {
			strList = str.split(" ");
			str = "";
			for (int i=1; i<strList.length; i++){
				str += strList[i] + " ";
			}
			str = str.trim();
			
			removeSubsets(str);
			maxPatterns.put(str, 0);
			System.out.println("Added " + str);
		}
		
		for (String s : maxPatterns.keySet()) {
			bw.write(s + "\n");
		}
		
		br.close();
		bw.close();
	}
	
	private void run() throws IOException
	{
		for (int i=0; i<5; i++) {
			maxPatterns = new HashMap<>();
			generateMaxPatterns(i);
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		GenerateMaxPatterns main = new GenerateMaxPatterns();
		main.run();
		System.exit(0);
	}
}
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenerateClosedPatterns {
	
	HashMap<String, Integer> closedPatterns;
	List<List<String>> transactions;
	
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
	
	private void removeSubsets(String str, int count)
	{
		List<String> subsets = new ArrayList<>();
		for (String s : closedPatterns.keySet()) {
			if (isSubset(s, str) && count == closedPatterns.get(s)) {
				subsets.add(s);
			}
		}
		
		for (String s : subsets) {
			closedPatterns.remove(s);
			System.out.println("Removed : " + s);
		}
	}
	
	private int getCount(String s)
	{
		String[] strList = s.split(" ");
		int count = 0;
		
		for (List<String> t : transactions) {
			int i=0, j=0;
			while (i < t.size() && j < strList.length) {
				if (t.get(i).equals(strList[j])) {
					i++;
					j++;
				} else {
					i++;
				}
			}
			
			if (j == strList.length) {
				count++;
			}
		}
		
		return count;
	}
	
	private void generateClosedPatterns(int index) throws IOException
	{
		BufferedReader brTopic = new BufferedReader(new FileReader("src/topic-" + index + ".txt"));
		BufferedReader br = new BufferedReader(new FileReader("src/patterns/pattern-" + index + ".txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("src/closed/closed-" + index + ".txt"));
		String str;
		String[] strList;
		List<String> eachTransaction;
		int count;
		
		while((str = brTopic.readLine()) != null) {
			eachTransaction = new ArrayList<>();
			strList = str.split(" ");
			for (String s : strList) {
				eachTransaction.add(s);
			}
			transactions.add(eachTransaction);
		}
		
		while((str = br.readLine()) != null) {
			count = getCount(str);
			removeSubsets(str, count);
			closedPatterns.put(str, count);
			System.out.println("Added " + str);
		}
		
		for (String s : closedPatterns.keySet()) {
			bw.write(s + "\n");
		}
		
		brTopic.close();
		br.close();
		bw.close();
	}
	
	private void run() throws IOException
	{
		for (int i=0; i<5; i++) {
			closedPatterns = new HashMap<>();
			transactions = new ArrayList<>();
			generateClosedPatterns(i);
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		GenerateClosedPatterns main = new GenerateClosedPatterns();
		main.run();
		System.exit(0);
	}
}
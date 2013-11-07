import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GenerateClosedPatterns {
	
	HashMap<String, Integer> closedPatterns;
	
	private boolean isSuperSet(String s, int count)
	{
		for (String p : closedPatterns.keySet()) {
			if (isSubset(s, p) && count == closedPatterns.get(p)) {
				return false;
			}
		}
		
		return true;
	}
	
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
	
	private void generateClosedPatterns(int index) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("src/patterns/pattern-" + index + ".txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("src/closed/closed-" + index + ".txt"));
		String str;
		String[] strList;
		int count;
		
		while((str = br.readLine()) != null) {
			strList = str.split(" ");
			str = "";
			for (int i=1; i<strList.length; i++){
				str += strList[i] + " ";
			}
			str = str.trim();
			
			count = Integer.parseInt(strList[0]);
			
			if (isSuperSet(str, count)) {
				removeSubsets(str, count);
				closedPatterns.put(str, count);				
				System.out.println("Added " + str);
			}
		}
		
		List<String> closedPatternsList = new ArrayList<>();
		for (String s : closedPatterns.keySet()) {
			closedPatternsList.add(s);
		}
		
		Collections.sort(closedPatternsList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return closedPatterns.get(o2) - closedPatterns.get(o1);
			}
		});
		
		for (String s : closedPatternsList) {
			bw.write(closedPatterns.get(s) + " " + s + "\n");
		}
		
		br.close();
		bw.close();
	}
	
	private void run() throws IOException
	{
		for (int i=0; i<5; i++) {
			closedPatterns = new HashMap<>();
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
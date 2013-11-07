import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Apriori
{
	List<List<String>> resultsBySize;
	List<Map<String, Integer>> transactions;
	Integer min_sup_count;
	Double min_support_percentage = 0.5;
	BufferedReader br;
	BufferedWriter bw;
	HashMap<String, Integer> resultsBySupport;
	
	private boolean containsPattern(int index, String str)
	{
		String[] strList = str.split(" ");
		String[] patternList;
		
		for (String p : resultsBySize.get(index)) {
			patternList = p.split(" ");
			int i=0, j=0;
			while (i < patternList.length && j < strList.length) {
				if (patternList[i].trim().equals(strList[j].trim())) {
					i++;
					j++;
				} else {
					i++;
				}
			}
			
			if (j == strList.length) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean allSubsetsValid(String s)
	{
		String temp;
		String[] strList = s.split(" ");
		
		if (strList.length == 1) {
			return true;
		}
		
		for (int i=0; i<strList.length - 1; i++) {
			temp = s.replace(strList[i] + " ", "");
			if (!containsPattern(strList.length - 2, temp)) {
				return false;
			}
		}
		
		temp = s.replace(" " + strList[strList.length - 1], "");
		if (!containsPattern(strList.length - 2, temp)) {
			return false;
		}
		
		return true;
	}
	
	private boolean countOverMinSup(String s) throws IOException
	{
		String[] strList = s.split(" ");
		int count = 0;
		boolean flag;
		
		for (Map<String, Integer> t : transactions) {
			flag = true;
			for (int i=0; i<strList.length; i++) {
				if (!t.containsKey(strList[i])) {
					flag = false;
					break;
				}
			}
			
			if (flag == true) {
				count++;
			}
		}
		
		if (count >= min_sup_count) {
			System.out.println(s);
			resultsBySupport.put(s, count);
			return true;
		} else {
			return false;
		}
	}
	
	
	private void runApriori(List<String> s) throws IOException
	{
		String[] strList1;
		String[] strList2;
		List<String> result = new ArrayList<>();
		List<String> resultMinSup = new ArrayList<>();
		boolean flag;
		
		if (s.size() <= 1) {
			return;
		}
		
		int length = s.get(0).split(" ").length - 1;
				
		for (int i=0; i<s.size(); i++) {
			for (int j=i+1; j<s.size(); j++) {
				strList1 = s.get(i).split(" ");
				strList2 = s.get(j).split(" ");
				flag = false;
				
				for (int k=0; k<strList1.length - 1; k++) {
					if (!strList1[k].equals(strList2[k])) {
						flag = true;
						break;
					}
				}
				
				if (flag) {
					continue;
				}
				
				if (Integer.parseInt(strList1[length]) < Integer.parseInt(strList2[length])) {
					result.add(s.get(i) + " " + strList2[length]);
				}
				else {
					result.add(s.get(j) + " " + strList1[length]);
				}
			}
		}
		
		for (int i=0; i<result.size(); i++) {
			if (allSubsetsValid(result.get(i)) && countOverMinSup(result.get(i))) {
				resultMinSup.add(result.get(i));
			}
		}
		resultsBySize.add(resultMinSup);
		runApriori(resultMinSup);
	}
	
	private void runAprioriForFile(int index) throws IOException
	{
		br = new BufferedReader(new FileReader("src/topic-" + index + ".txt"));
		bw = new BufferedWriter(new FileWriter("src/patterns/pattern-" + index + ".txt"));
		
		String str;
		String[] strList;
		Set<String> result = new HashSet<>();
		List<String> resultMinSup = new ArrayList<>();
		Map<String, Integer> temp = new HashMap<>();
		
		while ((str = br.readLine()) != null) {
			strList = str.split(" ");
			temp = new HashMap<>();
			
			for (int i=0; i<strList.length; i++) {
				result.add(strList[i]);
				temp.put(strList[i], 0);
			}
			
			transactions.add(temp);
		}

		min_sup_count = (int) (transactions.size() * min_support_percentage/100);
		
		for (String s : result) {
			if (countOverMinSup(s)) {
				resultMinSup.add(s);
			}
		}
		resultsBySize.add(resultMinSup);
		runApriori(resultMinSup);
		
		List<String> sortedResultsBySupport = new ArrayList<>();
		for (String s : resultsBySupport.keySet()) {
			sortedResultsBySupport.add(s);
		}
		
		Collections.sort(sortedResultsBySupport, new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return resultsBySupport.get(arg1) - resultsBySupport.get(arg0);
			}
		});
		
		for (String s : sortedResultsBySupport) {
			bw.write(resultsBySupport.get(s) + " " + s + "\n");
		}
		
		br.close();
		bw.close();
	}
	
	public Apriori(int i) throws IOException
	{
		resultsBySize = new ArrayList<>();
		transactions = new ArrayList<>();
		resultsBySupport = new HashMap<>();
		runAprioriForFile(i);
	}
}
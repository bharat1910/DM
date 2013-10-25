import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Apriori
{
	List<List<String>> resultsBySize;
	List<List<String>> transactions;
	Integer min_sup = 50;
	BufferedReader br;
	BufferedWriter bw;
	
	private boolean allSubsetsValid(String s)
	{
		String temp;
		String[] strList = s.split(" ");
		
		if (strList.length == 1) {
			return true;
		}
		
		for (int i=0; i<strList.length - 1; i++) {
			temp = s.replace(strList[i] + " ", "");
			if (!resultsBySize.get(strList.length - 2).contains(temp)) {
				return false;
			}
		}
		
		temp = s.replace(" " + strList[strList.length - 1], "");
		if (!resultsBySize.get(strList.length - 2).contains(temp)) {
			return false;
		}
		
		return true;
	}
	
	private boolean countOverMinSup(String s) throws IOException
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
		
		if (count >= min_sup) {
			System.out.println(s);
			bw.write(s + "\n");
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
		bw = new BufferedWriter(new FileWriter("src/pattern-" + index + ".txt"));
		
		String str;
		String[] strList;
		Set<String> result = new HashSet<>();
		List<String> resultMinSup = new ArrayList<>(), temp;
		
		while ((str = br.readLine()) != null) {
			strList = str.split(" ");
			temp = new ArrayList<>();
			
			for (int i=0; i<strList.length; i++) {
				result.add(strList[i]);
				temp.add(strList[i]);
			}
			
			transactions.add(temp);
		}
		
		for (String s : result) {
			if (countOverMinSup(s)) {
				resultMinSup.add(s);
			}
		}
		resultsBySize.add(resultMinSup);
		runApriori(resultMinSup);
		
		br.close();
		bw.close();
	}
	
	private void run() throws IOException
	{
		for (int i=0; i<5; i++) {
			resultsBySize = new ArrayList<>();
			transactions = new ArrayList<>();
			runAprioriForFile(i);
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		Apriori main = new Apriori();
		main.run();
		System.exit(0);
	}
}
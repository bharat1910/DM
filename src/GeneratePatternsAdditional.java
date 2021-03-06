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

public class GeneratePatternsAdditional {
	
	double omega = 0.4;
	double theta = 0.2;
	int[][] linesByTopics;
	Map<String, Double> patternPurityMap;
	Map<String, Double> patternCountMap;
	Map<String, Double> patternPhrasenessMap;
	List<List<Map<String, Integer>>> allTransactionsByTopic;
	
	private boolean isSubset(String s1, String s2) {
		String[] strList = s1.split(" ");
		String[] strList2 = s2.split(" ");
		Map<String, Integer> map =  new HashMap<>();
		for (int i=0; i<strList2.length; i++) {
			map.put(strList2[i], 0);
		}
		
		for (int i=0; i<strList.length; i++) {
			if (!map.containsKey(strList[i])) {
				return false;
			}
		}
		
		return true;
	}
	
	private void generateLinesCountByTopic() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("src/result/word-assignments.dat"));
		String str;
		String[] strList;
		int count = 0;
		
		linesByTopics = new int[5][5];
		List<Set<Integer>> linesByTopic = new ArrayList<>();
		for (int i=0; i<5; i++) {
			linesByTopic.add(new HashSet<Integer>());
		}
		
		while ((str = br.readLine()) != null) {
			strList = str.split(" ");
			for (int i=1; i<strList.length; i++) {
				linesByTopic.get(Integer.parseInt(strList[i].split(":")[1])).add(count);
			}
			count++;
		}
		
		for (int i=0; i<5; i++) {
			for (int j=i; j<5; j++) {
				Set<Integer> union = new HashSet<>();
				union.addAll(linesByTopic.get(i));
				union.addAll(linesByTopic.get(j));
				linesByTopics[i][j] = union.size();
				linesByTopics[j][i] = union.size();
			}
		}
		
		br.close();
	}
	
	private void getTransactionsByTopic() throws IOException
	{
		String str;
		String[] strList;
		Map<String, Integer> map;
		List<Map<String, Integer>> transactionsByFile;
		allTransactionsByTopic = new ArrayList<>();
		
		for (int i=0; i<5; i++) {
			BufferedReader br = new BufferedReader(new FileReader("src/topic-" + i + ".txt"));
			transactionsByFile = new ArrayList<>();
			
			while ((str = br.readLine()) != null) {
				map = new HashMap<>();
				strList = str.split(" ");
				for (String s : strList) {
					map.put(s, 0);
				}
				transactionsByFile.add(map);
			}
			
			allTransactionsByTopic.add(transactionsByFile);
			br.close();
		}
	}
	
	private int getCountInFile(int index, String str) {
		int count = 0;
		boolean flag;
		String[] strList = str.split(" ");
		
		for (Map<String, Integer> t : allTransactionsByTopic.get(index)) {
			flag = true;
			
			for (int i=0; i<strList.length; i++) {
				if (!t.containsKey(strList[i])) {
					flag = false;
					break;
				}
			}
			
			if (flag) {
				count++;
			}
		}
		
		return count;
	}
	
	private void generatePatternsByPurity(int index) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("src/patterns/pattern-" + index + ".txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("src/bonus/bonus-" + index + ".txt"));
		String str;
		String[] strList;
		Double frequency;
		Double lines = (double) linesByTopics[index][index];
		Double linesTotal;
		Double frequencyOther;
		Double temp;
		Double max;
		Double purity;
		
		while ((str = br.readLine()) != null) {
			max = Double.MIN_VALUE;
			
			strList = str.split(" ");
			str = "";
			for (int i=1; i<strList.length; i++) {
				str += strList[i] + " ";
			}
			str = str.trim();
			
			frequency = Double.parseDouble(strList[0]);
			for (int i=0; i<5; i++) {
				if (i != index) {
					frequencyOther = (double) getCountInFile(i, str);
					linesTotal = (double) linesByTopics[index][i];
					temp = (frequency + frequencyOther)/linesTotal;
					if (temp > max) {
						max = temp;
					}
				}
			}
			
			purity = Math.log10(frequency/lines) - Math.log10(max);
			patternPurityMap.put(str, purity);
			patternCountMap.put(str, frequency);
		}
		
		List<String> patternPurityList = new ArrayList<>();
		double term1, term2;
		for (String s : patternPurityMap.keySet()) {
			patternPurityList.add(s);
			
			term1 = Math.log(patternCountMap.get(s)/lines);
			strList = s.split(" ");
			term2 = 0;
			for (String s2 : strList) {
				term2  += Math.log(patternCountMap.get(s2)/lines);
			}
			patternPhrasenessMap.put(s, term1 - term2);
		}
		
		List<String> patternsToBeRemoved = new ArrayList<>();
		double check;
		for (int i=0; i<patternPurityList.size(); i++) {
			check = 0;
			for (int j=0; j<patternPurityList.size(); j++) {
				if (i != j && isSubset(patternPurityList.get(i), patternPurityList.get(j))) {
					temp = patternCountMap.get(patternPurityList.get(j))/patternCountMap.get(patternPurityList.get(i));
					if (temp > check) {
						check = temp;
					}
				}
			}
			
			if ((1 - check) <= theta) {
				patternsToBeRemoved.add(patternPurityList.get(i));
			}
		}
		
		for (String s : patternsToBeRemoved) {
			patternPurityList.remove(s);
		}
		
		Collections.sort(patternPurityList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				Double d1 = patternCountMap.get(o1) * ((omega * patternPurityMap.get(o1)) + ((1 - omega) * patternPhrasenessMap.get(o1)));
				Double d2 = patternCountMap.get(o2) * ((omega * patternPurityMap.get(o2)) + ((1 - omega) * patternPhrasenessMap.get(o2)));
				return d2.compareTo(d1);
			}
		});
		
		for (String s : patternPurityList) {
			bw.write((patternCountMap.get(s) * ((omega * patternPurityMap.get(s)) + ((1 - omega) * patternPhrasenessMap.get(s)))) + " " + s + "\n");
		}
		
		br.close();
		bw.close();
	}
	
	public void run() throws IOException
	{
		generateLinesCountByTopic();
		getTransactionsByTopic();
		
		for (int i=0; i<5; i++) {
			patternPurityMap = new HashMap<>();
			patternCountMap = new HashMap<>();
			patternPhrasenessMap = new HashMap<>();
			generatePatternsByPurity(i);
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		GeneratePatternsAdditional main = new GeneratePatternsAdditional();
		main.run();
		System.exit(0);
	}
}
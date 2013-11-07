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

public class GeneratePatternsByPurity {
	
	int[][] linesByTopics;
	Map<String, Double> patternPurityMap;
	List<List<Map<String, Integer>>> allTransactionsByTopic;
	
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
		
		System.out.println(linesByTopics[0][0]);
		System.out.println(linesByTopics[0][1]);
		System.out.println(linesByTopics[0][2]);
		System.out.println(linesByTopics[0][3]);
		System.out.println(linesByTopics[0][4]);
		
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
		BufferedWriter bw = new BufferedWriter(new FileWriter("src/purity/purity-" + index + ".txt"));
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
		}
		
		List<String> patternPurityList = new ArrayList<>();
		for (String s : patternPurityMap.keySet()) {
			patternPurityList.add(s);
		}
		
		Collections.sort(patternPurityList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return (patternPurityMap.get(o2).compareTo(patternPurityMap.get(o1)));
			}
		});
		
		for (String s : patternPurityList) {
			bw.write(patternPurityMap.get(s) + " " + s + "\n");
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
			generatePatternsByPurity(i);
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		GeneratePatternsByPurity main = new GeneratePatternsByPurity();
		main.run();
		System.exit(0);
	}
}
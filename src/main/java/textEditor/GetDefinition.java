package textEditor;


import java.io.*;
import java.net.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;



public class GetDefinition {

	
	/**
	 * The starting position of the term in the dictionary entry represented as a {@code String}
	 */
	static final int HTML = 6;
//	static int outputSize = 10;
	
	/**
	 *  Matches dictionary entries from OPTED encoded in UTF-8 to {@code entry} 
	 *  ignoring case considerations and accents.
	 *  
	 *  @param entry the entry to look up in the dictionaries
	 *  
	 *  @param outputSize the number of entries to fetch
	 *  
	 *  @return list of lines with matching entries 
	 */
	public static List<String> get(String entry, int outputSize) {
		List<String> returnList = new LinkedList<>();
		List<String> frenchList = new ArrayList<>();
		List<String> list = new ArrayList<>();
		Set<String> outputSet = new HashSet<>();
		try {
//			String entry = "bedagat";
			entry = entry.toLowerCase();
			int ind = -1;
			System.out.println("searching for string: " + entry);
			
			String entryAddress = String.format("https://www.mso.anu.edu.au/~ralph/OPTED/v003/wb1913_%c.html", entry.charAt(0));
            URL url = new URL(entryAddress);
            BufferedReader br = new BufferedReader(
        			new InputStreamReader(
        			new BufferedInputStream(url.openStream()),"UTF-8"));
			String line;
//			System.out.println("prije liste");
			
			while ( (line = br.readLine()) != null) { 
				list.add(line);
			}
			
			
			
			for (String curr : list) {
				String upper = curr;
				curr = curr.toLowerCase();
				if (curr.contains(entry)) {
//					System.out.println(curr);
					if (entry.length() + HTML > curr.length()) continue;
					String pom = curr.substring(HTML, entry.length() + HTML);
					if (pom.equals(entry)) {
							outputSet.add(upper);
					}
				}
			}
			// TODO revert changes to line 70, 71
//			frenchList.addAll(GetFrench.get());
//			System.out.println("french list size is " + frenchList.size());
			for (String curr : frenchList) {
				String upper = curr;
				curr = curr.toLowerCase();
				if (curr.contains(entry)) {
//					System.out.println(curr);
					if (entry.length() > curr.length()) continue;
					String pom = curr.substring(0, entry.length());
					if (pom.equals(entry)) {
							outputSet.add(upper);
					}
				}
			}
//			if (ind == -1) {
//				System.out.println("found close matches:");
				outputSet.stream()
				.sorted(compByLength) // nek uzme najkrace makar to ukljucuje i opis 
				.limit(outputSize).forEach(e -> {
//					System.out.println(e);
					returnList.add(e);
				});
//			}
        } 
		catch (Exception exp) {
			exp.printStackTrace();
        }
		return returnList;
	}
	/*
	 
	public static void main (String[] args) {
		for (String s : get("part", 10)) {
			System.out.println(s);
		}
	}
	*/
	
	static Comparator<String> compByLength = (aName, bName) -> (aName.length() - bName.length());
	
}

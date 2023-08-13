package textEditor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class GetFrench {

	
	/**
	 * Location of french dictionary.
	 */
	static String fileName = "src/main/resources/dico.csv";

	/**
	 * Gets entries from a french dictionary in UTF-8 encoding.
	 * @return list of entries
	 */
	public static List<String> get() {
		List<String> returnList = new ArrayList<>();
		int zas = 0;
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
					new BufferedInputStream(
					new FileInputStream(fileName)),"UTF-8"));
					String line;
					while ( (line = br.readLine()) != null) { 
						if (zas == 0) {
							zas = 1;
							continue;
						}
						returnList.add(deAccent(line));
					}
					br.close();
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
		
		return returnList;
	}
	/*
	
	public static void main (String[] args) {
		for (String s : get()) {
			System.out.println(s);
		}
	}*/
	
	
	/**
	 * 
	 * @param str {@code String} to remove accents from
	 * @return cleaned string
	 */
	public static String deAccent(String str) {
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
	
	static Comparator<String> compByLength = (aName, bName) -> (aName.length() - bName.length());
	
}

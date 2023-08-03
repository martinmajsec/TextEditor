package textEditor;

public class IndexOf {

	static int ind = -1;
	static int cnter;
	
	
	
	public int getInd() {
		return ind;
	}

	public int getCnt() {
		return cnter;
	}

	public IndexOf (String big, String small, int startIndexExclusive) {
		
//		System.out.println("big is " + skrati(big));
//		System.out.println("small is " + skrati(small));
		
		++startIndexExclusive;
		
		for (int i = startIndexExclusive;i < big.length();i++) {
			int cnt = 0;
			if (i + small.length() > big.length()) return;
			if (big.charAt(i) == small.charAt(0)) {
				if (big.substring(i, i + small.length()).equals(small)) {
					for (int j = 0;j <= i;j++) {
						if (big.charAt(j) == '\n') cnt++;
					}
//					System.out.println("returning " + i + " " + cnt);
					ind = i;
					cnter = cnt;
					return;
				}
			}
			
		}
		
	}
	/*
	public static void main (String[] args) {
		String big = "bla\nbla\nbla\n", small = "bla";
		int ind1 = new IndexOf(big, small, -1).getInd();
		
		System.out.println("\n-----------------------");
		int ind2 = new IndexOf(big, small, ind1).getInd();
		System.out.println("\n-----------------------");
		int ind3 = new IndexOf(big, small, ind2).getInd();
		System.out.println("\n-----------------------");
		System.out.println("ind1 is " + ind1);
		System.out.println("ind2 is " + ind2);
		System.out.println("ind3 is " + ind3);
	}
	*/
	private static String skrati(String x) {
		return "\n" + (x.length() > 100 ? ("...\n" + x.substring(x.length()-100)) : x);
	}
	
}

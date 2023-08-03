package textEditor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;


public class MyFindFrame extends JFrame {

	/**
	 * Instance of main frame.
	 */
	static TextEditor parent;
	/**
	 * {@code String} to be found in the text.
	 */
	String selectedString;
	
	final String DONE_REPLACING = "DONE_REPLACING";
	
	/**
	 * Label for the text above the text field where {@code String} to be found is entered.
	 */
	JLabel lbMsgFind = new JLabel("Find string:\n");
	/**
	 * Label for the text above the text field where replacing {@code String} is entered.
	 */
	JLabel lbMsgReplace = new JLabel("Replace string:\n");
	/**
	 * Text field for {@code String} to be found.
	 */
	JTextField txFieldFind;
	/**
	 * Text field for replacing {@code String}.
	 */
	JTextField txFieldReplace = new JTextField();
	/**
	 * Confirms {@code Find} action.
	 */
	JButton findBtn = new JButton("find");
	/**
	 * Confirms {@code Replaced} action.
	 */
	JButton replaceBtn = new JButton("replace");
	/**
	 * Confirms {@code Replace All} action.
	 */
	JButton replaceAllBtn = new JButton("replace all");
	/**
	 * String to be found, entered in the corresponding text field.
	 */
	String findString = null;
	/**
	 * Text in which strings are looked up.
	 */
	String content = null;
	/**
	 * Replacing string, entered in the corresponding text field.
	 */
	String replaceString = null;
	/**
	 * Remembers position so that the find option progresses through the text.
	 */
	int fromIndex = -1;
	/**
	 * {@link JMenuItem} with find functionality.
	 */
	JMenuItem findItem = new JMenuItem("find");
	/**
	 * {@link JMenuItem} with (find and) replace functionality.
	 */
	JMenuItem replaceItem = new JMenuItem("find and replace");
	/**
	 * {@link JMenuItem} with replace all functionality.
	 */
	JMenuItem replaceAllItem = new JMenuItem("replace all");
//	JMenuItem selectAllItem = new JMenuItem("select all");
	/**
	 * {@link JMenu} that contains all actions so they could be accelerated.
	 */

	JMenu editMenu = new JMenu("shortcuts");
	/**
	 * Selects wrap search option - starting the search over when end of file is reached.
	 */
	JCheckBox wrapBtn = new JCheckBox("wrap search");
	/**
	 * Selects if the search should be case sensitive.
	 */
	JCheckBox caseSensitiveBtn = new JCheckBox("case sensitive");
	/**
	 * Contains wrap search state.
	 */
	boolean wrapSearch = false;
	/**
	 * Contains replace all state.
	 */
	boolean isReplaceAll = false;
	/**
	 * Contains select all state.
	 */
	boolean isSelectAll = false;
	/**
	 * Contains case sensitive state.
	 */
	boolean isCaseSensitive = false;
	/**
	 * Counts number of words replaced by replace all action.
	 * <p>
	 * Currently not displayed in GUI.
	 */
	int replaceCnt = 1;
//	JTextArea txStatus = new JTextArea("status");
	/**
	 * Contains indexes of beginnings of strings found in the text.
	 */
	Set<Integer> indeksi = new HashSet<>();
	
	/**
	 * Creates a {@link JFrame} for finding a {@code String} in text from main frame text panel.
	 * @param parent instance of main frame
	 * @param selectedString text in which the string is to be found
	 */
	public MyFindFrame(TextEditor parent, String selectedString) {
		this.parent = parent;
		this.selectedString = selectedString;
		
		// layout setup
		
		txFieldFind = new JTextField(selectedString);
		
		JPanel northPanel = new JPanel();
		
		add(northPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel();
		JPanel findPanel = new JPanel(new GridLayout(2,1,5,5));
		JPanel replacePanel = new JPanel(new GridLayout(2,1,5,5));
		findPanel.add(lbMsgFind);
		findPanel.add(txFieldFind);
		replacePanel.add(lbMsgReplace);
		replacePanel.add(txFieldReplace);
		centerPanel.setLayout(new GridLayout(2,1,5,5));
		centerPanel.add(findPanel);
		centerPanel.add(replacePanel);
		add(centerPanel, BorderLayout.CENTER);
		
		
		JPanel southPanel = new JPanel(new GridLayout(2,1,5,5));
		JPanel southPanelUp = new JPanel();
		JPanel southPanelDown = new JPanel();
		southPanelUp.add(wrapBtn);
		southPanelUp.add(caseSensitiveBtn);
		southPanelDown.add(findBtn);
		southPanelDown.add(replaceBtn);
		southPanelDown.add(replaceAllBtn);
		southPanel.add(southPanelUp);
		southPanel.add(southPanelDown);
//		southPanel.add(txStatus);
		add(southPanel, BorderLayout.SOUTH);
		
		// accelerators
		
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		findItem.setAccelerator(enter);
		editMenu.add(findItem);
		
		KeyStroke shiftEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ActionEvent.SHIFT_MASK);
		replaceItem.setAccelerator(shiftEnter);
		editMenu.add(replaceItem);
			
		KeyStroke ctrlShiftEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ActionEvent.SHIFT_MASK + ActionEvent.CTRL_MASK );
		replaceAllItem.setAccelerator(ctrlShiftEnter);
		editMenu.add(replaceAllItem);
		
		// listeners 
		
		findItem.addActionListener(e -> {
			findBtn.doClick();
		});
		replaceItem.addActionListener(e -> {
			replaceBtn.doClick();
		});
		replaceAllItem.addActionListener(e -> {
			replaceAllBtn.doClick();
		});
		
		addEscapeListener(this);
		
		JMenuBar menubar = new JMenuBar();
	    menubar.add(editMenu);
	        
	    setJMenuBar(menubar);
		
	    setLocation(500, 500);
		pack();
		
		findBtn.addActionListener(findActionListener(parent));
		
		
		replaceBtn.addActionListener(replaceActionListener(parent));
		
		wrapBtn.addActionListener(e -> {
			 wrapSearch = wrapBtn.isSelected();
		});
		wrapBtn.doClick();
		
		caseSensitiveBtn.addActionListener(e -> {
			isCaseSensitive = caseSensitiveBtn.isSelected();
		});
		
		replaceAllBtn.addActionListener(replaceAllListener(parent));
	}

	/**
	 * Logic for replacing all occurences. 
	 * Displays a message if a string is replaced with its' suffix.
	 * Doesn't do anything if string to be found is null or contains only whitespaces.
	 * @param parent instance of main frame
	 * 
	 */
	private ActionListener replaceAllListener(TextEditor parent) {
		return e -> {
			
			findString = txFieldFind.getText();
			replaceString = txFieldReplace.getText();
			
			
//			System.out.println("replaceString is |" + replaceString + "|");
			if (findString == null || findString.strip().equals("")) return;
			if (replaceString.indexOf(findString) > 0) { //ok je dok god string nije sufiks onog cime ga zelim zamjenit
				JOptionPane.showMessageDialog(this, "Can't replace given strings.");
				return;
			}
			
			boolean pom = wrapSearch;
			wrapSearch = false;
			wrapBtn.setSelected(false);
/*			if (pom) {
				fromIndex = -1; System.out.println("moved to 0");
			}
*/			while (true) {
				
				
				findBtn.doClick();
				
				if (replaceString.equals(DONE_REPLACING)) {
					if (pom) {
						wrapSearch = true;
						wrapBtn.setSelected(true);
					}
					System.out.println(String.format("replaced %d matches", replaceCnt-1));
					return;
				}
				parent.textPane.replaceSelection(replaceString);
				replaceCnt++;
				
			}
		};
	}

	private ActionListener replaceActionListener(TextEditor parent) {
		return e -> {

			findString = txFieldFind.getText();
			replaceString = txFieldReplace.getText();
			if (findString == null || findString.strip().equals("")) return;
			if (replaceString.indexOf(findString) > 0) { //ok je dok god string nije sufiks onog cime ga zelim zamjenit
				JOptionPane.showMessageDialog(this, "Can't replace given strings.");
			
				return;
			}
//			System.out.println("replaceString is |" + replaceString + "|");
			boolean pom = wrapSearch;
			wrapSearch = false;
			wrapBtn.setSelected(false);
        	findBtn.doClick();
        	
        	if (replaceString.equals(DONE_REPLACING)) return;
        	
        	parent.textPane.replaceSelection(replaceString);
        	if (isReplaceAll) {
        		replaceBtn.doClick();
        		replaceCnt++;
        		System.out.println(replaceCnt);
        	}
        	
        	if (pom) {
        		wrapSearch = true;
        		wrapBtn.setSelected(true);
    			
        	}
		};
	}

	/**
	 * Logic to find and select a string in text from caret position onward. 
	 * If string is null or contains only whitespace characters, nothing happens. <p>
	 * If wrapped search is enabled, when end of file is reached, the search will begin from the top again.
	 * If not, the frame will automatically close after last match is reached.  
	 * Wrapped search is enabled by default. <p>
	 * If search is case sensitive and there are no matches, a blank is thrown before displaying not found message.
	 * If string is not found, a message is displayed.
	 * @param parent instance of main frame which contains the text panel with the text to search in
	 * 
	 */
	private ActionListener findActionListener(TextEditor parent) {
		return e -> {
//			System.out.println("BRAND NEW");
//			System.out.println(parent.textPane.getCaretPosition());
//			System.out.println("caret is at " + (parent.textPane.getCaretPosition() - 1));
//			fromIndex = parent.textPane.getCaretPosition() - 1;
			findString = txFieldFind.getText();
//			System.out.println("findString is |" + findString + "|");
			if (findString == null || findString.strip().equals("")) return;
			
        	content = parent.textPane.getText();
//       	System.out.println("content is" + skrati(content));
/*        	if (findString != null && !findString.equals("") && content.indexOf(findString, 0) == -1) {
        		JOptionPane.showMessageDialog(this, "String not found.");
        		indeksi = new HashSet<>();
				fromIndex = -1;
				setVisible(false);
				dispose();
				new MyFindFrame(parent, selectedString).setVisible(true);
        	}
 */       	
//       	if (isCaseSensitive) fromIndex = content.indexOf(findString, fromIndex + 1);
//        	else fromIndex = content.toLowerCase().indexOf(findString.toLowerCase(), fromIndex + 1);
        	IndexOf myIndex;
        	if (isCaseSensitive) {
        		myIndex = new IndexOf(content, findString, fromIndex);
        	}
        	
        	else {
        		myIndex = new IndexOf (content.toLowerCase(), findString.toLowerCase(), fromIndex);
        		
//       		System.out.println("calling with " + skrati(content.toLowerCase()) + " | " + findString.toLowerCase() + " | " + fromIndex);
        		
        	}
        	int pomCnt = myIndex.getCnt();
        	fromIndex = myIndex.getInd() - pomCnt;
//        	System.out.println("getInd is " + myIndex.getInd() + " | getCnt is " + pomCnt);
        	
			if (indeksi.contains(fromIndex) || fromIndex == -1) {  
//				System.out.println("contains is true");
				if (wrapSearch == false) {
					setVisible(false);
					dispose();
					indeksi = new HashSet<>();
					fromIndex = -1;
					replaceString = DONE_REPLACING;
					return;
				}
				else if (content.indexOf(findString, 0) != -1) {
					indeksi = new HashSet<>();
					fromIndex = -1;
					System.out.println("wrapped search");
					findItem.doClick();
				}
				else if (content.indexOf(findString, 0) == -1) {
					JOptionPane.showMessageDialog(this, "String not found.");
				}
			}
			else {

	    			parent.textPane.select(fromIndex, fromIndex + findString.length());	 
//	    			parent.textPane
			}
			indeksi.add(fromIndex);				 // mozda ce trebat uvecat za cnt
//			System.out.println("adding " + fromIndex);
			fromIndex += pomCnt;
//			if (isSelectAll) findBtn.doClick();
		};
	}
	
	/**
	 * Closes the frame when esc key is pressed.
	 * @param frame instance of frame
	 */
	public static void addEscapeListener(final JFrame frame) {
	    ActionListener escListener = new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            frame.setVisible(false);
	            frame.dispose();
	        }
	    };

	    frame.getRootPane().registerKeyboardAction(escListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);

	}

	/**
	 * Unused code to cut off {@code String} after 100 characters.
	 * @param x String to truncate
	 */
	@SuppressWarnings("unused")
	private static String skrati(String x) {
		return "\n" + (x.length() > 100 ? ("...\n" + x.substring(x.length()-100)) : x);
	}	


}

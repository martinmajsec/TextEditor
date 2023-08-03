package textEditor;



import java.awt.BorderLayout;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;


import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;



public class GetNameFrame extends JFrame{

	/**
	 * Very long string to make sure frame isn't minimized when packed.
	 */
	private final String DEFAULT_LONG_STRING = "Lorem ipsum dolor sit amet consectetuer";
	/**
	 * Name of the directory where documents are to be saved. 
	 */
	final String dirName;
	
	/**
	 * Message label above the field where file name is entered.
	 */
	private JLabel msg = new JLabel("Enter file name and extension:");
	/**
	 * Text field where file name is entered.
	 */
	private JTextField txField = new JTextField(DEFAULT_LONG_STRING);

	/**
	 * Save button.
	 */
	private JButton okButton = new JButton("save (Enter)");
	/**
	 * Cancel button.
	 */
	private JButton cancelButton = new JButton("cancel ");

	/**
	 * {@link JMenu} that contains all actions so they could be accelerated.
	 */
	JMenu fileMenu = new JMenu("Menu");
	/**
	 * {@link JMenu} option to save file, accelerated by enter.
	 */
	JMenuItem saveItem = new JMenuItem("Save");
	/**
	 * {@link JMenu} option to cancel saving file.
	 */
	JMenuItem cancelItem = new JMenuItem("Cancel");
	/**
	 * {@link MenuBar} that cotains menu.
	 */
	JMenuBar menuBar = new JMenuBar();
	/**
	 * Instance of main frame.
	 */
	static TextEditor parent;
	/**
	 * Default suggested extension.
	 */
	String suggestExtension =".txt";
	
//	boolean runningNameFrame = false;
	/**
	 * Title of parent frame.
	 */
	static String parentTitle;
	

	/**
	 * Creates frame that gets the name for new file. Has {@link Explorer} in the main panel.
	 * Can be closed by esc key. 
	 * Location is set to center by default.
	 * 
	 * @param parent instance of main frame
	 * @param extension suggested file extension
	 */
	public GetNameFrame(TextEditor parent, String extension) {
		super("save as");
//		runningNameFrame = true;
		this.parent = parent;
		this.suggestExtension = extension;
		this.dirName = parent.dirName;
		parentTitle = parent.saveTitle;
//		System.out.println("parentTitle at constructor is " + parentTitle);
		parent.setTitle("PREVIEW FILE");
		
		setSize(1000,500);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		
		JPanel southContainer = createSouthContainer();
		
		

		add(new Explorer(dirName, parent), BorderLayout.CENTER); 
		add(southContainer, BorderLayout.SOUTH);

		KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		saveItem.setAccelerator(enterStroke);
		fileMenu.add(saveItem);
		fileMenu.add(cancelItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		
		saveItem.addActionListener(e -> {
			okButton.doClick();
		});
		
		cancelItem.addActionListener(e -> {
			cancelButton.doClick();
		});
		
		okButton.setMnemonic(KeyEvent.VK_ENTER);
		okButton.addActionListener(okButtonActionListener());

		cancelButton.addActionListener(e -> {
			setVisible(false); //you can't see me!
        	updateContent();
			dispose(); //Destroy the JFrame object
			// kad ponovno pises, overwritea se Placeholder i to je ok
			// parent.returnText();
		});
		addEscapeListener(this);
		
	//	pack();
		String content = parent.savedTxt == null ? parent.textPane.getText() : parent.savedTxt;
		String firstLine = content.split("\n")[0].strip();
		
//		System.out.println("my running name is:\n" + firstLine + suggestExtension);
		txField.setText(firstLine + suggestExtension);
//		System.out.println("suggestExtension is " + extension);
		
		addWindowListener( new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
		    {
		      // Here you can give your own implementation according to you.
			   updateContent();
			   setVisible(false); //you can't see me!
			   dispose(); //Destroy the JFrame object
		     }
		  });
		
	}

	private JPanel createSouthContainer() {
		
		JPanel southContainer = new JPanel();
		southContainer.setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		northPanel.add(msg);
//		add(northPanel, BorderLayout.NORTH);
		southContainer.add(northPanel, BorderLayout.NORTH);

		
		
		JTextPane centerPanel = new JTextPane();
		
		centerPanel.add(txField);
//		add(txField, BorderLayout.CENTER);
		southContainer.add(txField, BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		southPanel.add(okButton);
		southPanel.add(cancelButton);
//		add(southPanel, BorderLayout.SOUTH);
		southContainer.add(southPanel, BorderLayout.SOUTH);
		
		return southContainer;
	}

	/**
	 * File saving logic. Moves content from placeholder file to new file with name entered in frame.
	 * 
	 */
	private ActionListener okButtonActionListener() {
		return e -> {
			String currFileName = dirName + "Placeholder.txt";
			String newFileName = dirName + txField.getText();
			
			
			
			File checkFile = new File(newFileName);
			if (checkFile.exists() && !checkFile.isDirectory()) {
				// overwrite logic
				int isOverwrite = JOptionPane.showConfirmDialog(
					    this,
					    "You are about to overwrite file " + newFileName + "! Continue?",
					    "ALERT",
					    JOptionPane.YES_NO_OPTION);
				isOverwrite = 1 - isOverwrite;
				System.out.println("confirm overwrite is " + (isOverwrite == 1 ? "yes" : "no")); 


				if (isOverwrite == 1) {
					if (checkFile.delete()) System.out.println("deleted " + newFileName);	
				}
				if (isOverwrite == 0) {
					String inputDialog = (String)JOptionPane.showInputDialog(this, "Enter new file name", suggestExtension);
					System.out.println("new file name is " + inputDialog);
					if (inputDialog == null) { 
						// cancel is pressed
						return;
					}
					newFileName = dirName + inputDialog;
					
				}
			}
			Path source = Path.of(currFileName);
//			System.out.println("currfilename is " + currFileName );
			Path target = null;
			try {
				target = Path.of(newFileName);
			}
			catch (InvalidPathException exc) {
				JOptionPane.showMessageDialog(this, "The given path is invalid. Please select a new one.\nNote: try deleting the suggested path and write it again.");
				new GetNameFrame(parent, suggestExtension).setVisible(true);
			}
//			System.out.println("source " + source);
//			System.out.println("target " + target);
			try {
				if (target != null && !(new File(target.toString())).exists()) Files.move(source, target);
				// if new file doesn't already exist, change placeholder to new file
				// to prevent exception when dialog is escaped
			}

			catch (Exception exc) {
				System.out.println("source exists " + new File(source.toString()).exists());
				System.out.println("target exists " + new File(target.toString()).exists());
				exc.printStackTrace();
			}
			parent.setTitle(newFileName);
			parentTitle = newFileName;
			System.out.println("newFileName is " + newFileName);

			try {
				String realExtension = txField.getText().split("\\.")[1].strip();
					if (realExtension.equals("html")) {
//						System.out.println("found suggest extension, new file name is " + newFileName);
						String content = parent.textPane.getText();
						content = new MarkdownHandler(content, dirName).getContent();
						writeToFile(newFileName, content);
					}
					
//				System.out.println("realExtension is " + realExtension);
				}
				catch (Exception exc) {
					exc.printStackTrace();
				}
			
			// dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); // koristi ak ces iamt listenere
			setVisible(false); //you can't see me!
        	updateContent();
			dispose(); //Destroy the JFrame object
//			runningNameFrame = false;
		};
	}

/*	public boolean isRunningNameFrame() {
		return runningNameFrame;
	}
*/
	
	/**
	 * Closes the frame when esc key is pressed.
	 * @param frame instance of frame
	 */
	public static void addEscapeListener(final JFrame frame) {
	    ActionListener escListener = new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	updateContent();
	        	parent.setTitle(parentTitle);
//	        	System.out.println("parentTitle is " + parentTitle);
	            frame.setVisible(false);
	            frame.dispose();
	        }
	    };

	    frame.getRootPane().registerKeyboardAction(escListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);

	}	

	/**
	 * Updates parent text panel.
	 */
	private static void updateContent() {
//		System.out.println("updating");
		parent.content = parent.savedTxt;
		parent.textPane.setText(parent.content);
	//	parent.setTitle(parentTitle);
	}
	
	
	private void writeToFile(String fileName, String content) {
		try {
			Writer bw = new BufferedWriter(
					new OutputStreamWriter(
					new BufferedOutputStream(
					new FileOutputStream(fileName)),"UTF-8"));
					bw.write(content);
					// skracen ispis
//					System.out.println("writing:" + skrati(content)); 
					bw.close(); 
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	private String skrati(String x) {
		return "\n" + (x.length() > 100 ? ("...\n" + x.substring(x.length()-100)) : x);
	}	
	
}

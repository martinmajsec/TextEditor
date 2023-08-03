package textEditor;



import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;










public class TextEditor extends JFrame{

	/**
	 * main panel in frame
	 */
	JSplitPane splitPane = new JSplitPane();
	/**
	 * default frame title
	 */
	static final String defaultTitle = "text editor";
	/**
	 * default suggested extension to file name
	 */
	final String defaultExtension = ".txt";
	/**
	 * {@link JMenu} with all file actions.
	 * @see #saveItem
	 * @see #saveAsItem
	 * @see #openItem
	 * @see #renderItem
	 * @see #exportItem
	 * @see #newItem
	 * 
	 */
	JMenu fileMenu = new JMenu("File");
	/**
	 * {@link JMenu} with all style actions.
	 */
	JMenu styleMenu = new JMenu("Style");
	/**
	 * {@link JMenu} with all edit actions.
	 * @see #undoItem
	 * @see #redoItem
	 * @see #findItem
	 * @see #inspectItem
	 */
	JMenu editMenu = new JMenu("Edit");
	/**
	 * {@link JMenuItem} with save functionality. 
	 * If the file hasn't been saved yet, a new frame is opened to prompt the user for the file name.
	 * When a file's name is clicked, a preview of the file's contents is shown in the main frame. 
	 * When the new frame is closed, the main frame reverts to the previous state.
	 * Otherwise, saves the new version without additional frames.
	 * @see GetNameFrame
	 */
	JMenuItem saveItem = new JMenuItem("Save");
	/**
	 * {@link JMenuItem} with save as functionality. 
	 * Opens new frame that gets the name of the new file and displays a file browser. 
	 * When a file's name is clicked, a preview of the file's contents is shown in the main frame. 
	 * When the new frame is closed, the main frame reverts to the previous state.
	 * @see #saveItem
	 */
	JMenuItem saveAsItem = new JMenuItem("Save As");
	/**
	 * {@link JMenuItem} with open functionality. Opens new frame that displays a file browser.
	 * @see FileChooserDemo
	 */
	JMenuItem openItem = new JMenuItem("Open");
	/**
	 * {@link JMenuItem} that renders text from left half of textpane (in Markdown format) 
	 * in the right half in HTML format.
	 * @see MarkdownHandler
	 */
	JMenuItem renderItem = new JMenuItem("Render");
	/**
	 * {@link JMenuItem} that saves the current document and suggests .html extension.
	 * @see #renderItem
	 */
	JMenuItem exportItem = new JMenuItem("Export MD > html");
	/**
	 * {@link JMenuItem} that restarts the main frame.
	 * 
	 */
	JMenuItem newItem = new JMenuItem("New File");
	/**
	 * {@link JMenuItem} with find functionality. 
	 * @see MyFindFrame
	 */
	JMenuItem findItem = new JMenuItem("Find");
	/**
	 * {@link JMenuItem} with word lookup functionality. 
	 * @see GetDefinition
	 */
	JMenuItem inspectItem = new JMenuItem("Inspect");
	/**
	 * main menubar that contains all menus
	 */
	JMenuBar menuBar = new JMenuBar();
	/**
	 * main panel
	 */
	JTextPane textPane;
	
//	GetNameFrame nameFrameInstance;
	/**
	 * DefinitionFrame instance, used to close the instance if main frame is closed
	 */
	DefinitionFrame dfInstance;
	/**
	 * text contained in the main frame
	 */
	String savedTxt, content; 	// TODO remove duplication
	/**
	 * instance of this, used in extracted methods
	 */
	TextEditor me;
//	boolean runningNameFrame = false, runningExplorerFrame = false;
	
	/**
	 * reference to the current thread, used for restarting 
	 * @see #newItem  
	 */
	static Thread myThread;
	
	/**
	 * contains the extension user has entered in {@link GetNameFrame}
	 */
	String extension = defaultExtension;
	
	/**
	 * default font size 
	 * @see #defaultFont
	 */
	final int defaultFontSize = 16;
	/**
	 * default font for text that user writes
	 * @see #defaultFontSize
	 */
	Font defaultFont = new Font("defaultFont", Font.PLAIN, defaultFontSize);

	/**
	 * default directory name where the files are to be saved
	 */
	final String dirName = "C:/arhiva/GhostWriter/Documents/";
//	final String tmpName = dirName + "tmp.txt";
	/**
	 * name of temporary file where user input is written
	 */
	final String fileName = dirName + "Placeholder.txt";
	/**
	 * panel that user writes in, contained in {@link #textPane}
	 */
	JScrollPane leftPane;
	/**
	 * panel where HTML render is displayed, contained in {@link #textPane}
	 */
	JScrollPane rightPane;
	/**
	 * counter for number of runs, incremented by {@link #newItem}
	 */
	static int runcnt = 0;
	
//	final String newline = "\n";
	
	/**
	 * contains actions, used for undo/redo
	 */
	HashMap<Object, Action> actions;
	/**
	 * text cast to AbstractDocument, used for undo/redo 
	 */
	AbstractDocument doc;
//	static final int MAX_CHARACTERS = 300;
	static final int INFINITY = (int) 1e9+7;
	
	/**
	 * used for undo/redo 
	 */
	protected UndoAction undoAction = new UndoAction();
	/**
	 * used for undo/redo 
	 */
    protected RedoAction redoAction = new RedoAction();
    /**
     * used for undo/redo 
     */
    protected UndoManager undo = new UndoManager();

    /**
     * contains copy of main frame's title
     */
    public String saveTitle = null;
    
    /**
     * creates a frame with two parts. User writes text in the laft part and it is rendered in the right part.
     */
	public TextEditor() {
		
		super(defaultTitle);
		
		me = this;

		// layout setup

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setFeel(); 
		textPane = new JTextPane();
		textPane.setFont(defaultFont);
		splitPane = createSplitPane();
		add(splitPane);

		// menu setup
		
		createFileMenu();
		menuBar.add(fileMenu);
		styleMenu = createStyleMenu();
		menuBar.add(styleMenu);
		actions=createActionTable(textPane);
        editMenu = createEditMenu();
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
        textPane.setCaretPosition(0);
        textPane.setMargin(new Insets(5,5,5,5));

        // undo/redo setup
		
        StyledDocument styledDoc = textPane.getStyledDocument();
        if (styledDoc instanceof AbstractDocument) {
            doc = (AbstractDocument)styledDoc;
            doc.setDocumentFilter(new DocumentSizeFilter(INFINITY));
        } else {
            System.err.println("Text pane's document isn't an AbstractDocument!");
            System.exit(-1);
        }
        doc.addUndoableEditListener(new MyUndoableEditListener());

        // listeners
        
		saveItem.addActionListener(
				saveItemActionListener());
				
		openItem.addActionListener(e -> {
			runFileChooserDemo();
		});
		saveAsItem.addActionListener(e -> {
			saveTitle = getTitle(); 
			setTitle(defaultTitle);
			 
			saveItem.doClick();
		});
		renderItem.addActionListener(renderListener());
		exportItem.addActionListener(exportListener());
		newItem.addActionListener(e -> {
			writeToFile(fileName, textPane.getText());
			reset();
		});
		findItem.addActionListener(e -> {
			String selected = textPane.getSelectedText(); // prva tri znaka su ...\n
			if (selected != null && selected.length() > 100) {
				selected = skrati(selected).substring(5);
			}
//			System.out.println("selected string is " + selected);
        	new MyFindFrame(this, selected).setVisible(true);
        });
		inspectItem.addActionListener(inspectListener());
		addWindowListener(closingListener());
		
		// placement
		
		setSize(1000,500);
		setLocationRelativeTo(null);
//		device.setFullScreenWindow(this);
	}



	/**
	 * Listens to {@link #exportItem}
	 */
	private ActionListener exportListener() {
		return e -> {
			renderItem.doClick();
			String tmp = textPane.getText();
//			savedTxt = tmp;
			textPane.setText(new MarkdownHandler(content, dirName).getContent());
			extension = ".html";
			
			saveAsItem.doClick();

			System.out.println("tmp is" + skrati(tmp));
			textPane.setText(tmp);
			savedTxt = tmp;
			extension = defaultExtension;
		};
	}



	/**
	 * Handles closing logic. If main frame is closed and changes to file haven't been saved, user will be asked
	 * whether it is to be saved or not. 
	 * <p> However, if there is no text or it contains only whitespace characters, 
	 * the frame will close without additional prompts.
	 * <p> Closing the main frame also closes all child frames.
	 */
	private WindowAdapter closingListener() {
		return new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
		    {
//				System.out.println("saved txt is " + savedTxt); // mozda ce trebal neke null sredivat
		      // Here you can give your own implementation according to you.
				if ((textPane.getText() == null || textPane.getText().equals("")) && getTitle().equals(defaultTitle)) /*close, no edits yet*/;
				else if (savedTxt == null || getTitle() == defaultTitle || (!savedTxt.equals(textPane.getText()) && !textPane.getText().equals(""))) {
				   int isSave = JOptionPane.showConfirmDialog(
						    me,
						    "Would you like to save changes to: " + 
						    //textPane.getText().split("\n")[0].strip() + "?",
						    (getTitle() == defaultTitle ? textPane.getText().split("\n")[0].strip() : getTitle().strip()) + "?", 
						    "ALERT",
						    JOptionPane.YES_NO_CANCEL_OPTION);
//				   System.out.println("save is " + isSave);
				   
				   if (isSave == 0) {
					   saveItem.doClick();
//					   runNameFrame(defaultExtension);
				   }
				   else if (isSave == -1 || isSave == 2) return; // ESC ili cancel
			   }
			   
			   setVisible(false); //you can't see me!
			   dispose(); //Destroy the JFrame object
			   if (dfInstance != null) dfInstance.dispose(); // ugasi definitionFrame kad se ugasi glavni prozor
		     }
		  };
	}



	/**
	 * Listens to {@link #inspectItem}
	 */
	private ActionListener inspectListener() {
		return e -> {
			String selected = textPane.getSelectedText(); // prva tri znaka su ...\n
			if (selected == null || selected.split(" ").length > 1) {
				JOptionPane.showMessageDialog(this, "You can only inspect one word with no whitespaces at a time", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else {
				List<String> definitions = GetDefinition.get(selected, 10);
				String allDefs = "";
				for (String s : definitions) {
					allDefs += s;
				}
				if (allDefs == "") {
					JOptionPane.showMessageDialog(this, "No matches found.", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
//				System.out.println("waiting to show you defs" + allDefs);
				/*
				JOptionPane.showMessageDialog(this,
				    ("<html>" + allDefs + "</html>"),
				    "definition",
				    JOptionPane.PLAIN_MESSAGE);
				*/
				runDefinitionFrame(allDefs);
			}
		};
	}


	
	/**
	 * Starts the program anew. Used in {@link #newItem}.
	 */
	private void reset() {
		setVisible(false); //you can't see me!
		dispose(); //Destroy the JFrame object
		if (dfInstance != null) dfInstance.dispose(); // ugasi definitionFrame kad se ugasi glavni prozor
//		myThread.interrupt();
		main(null);
	}

	

	private JSplitPane createSplitPane() {
		leftPane = new JScrollPane(textPane);
		rightPane = new JScrollPane(new MarkdownHandler(content, dirName));
		leftPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		rightPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//		leftPane.setEnabled(true);
//		rightPane.setEnabled(true);
		
//		rightPane.addFocusListener();
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.5);
		
		return splitPane;
	}


	/**
	 * Creates and accelerates all {@link #fileMenu} options.
	 */
	private void createFileMenu() {
		KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK);
		saveItem.setAccelerator(ctrlS);
		KeyStroke ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK);
		openItem.setAccelerator(ctrlO);
		KeyStroke ctrlShiftS = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK);
		saveAsItem.setAccelerator(ctrlShiftS);
		KeyStroke ctrlR = KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK);
		renderItem.setAccelerator(ctrlR);
		KeyStroke ctrlE = KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK);
		exportItem.setAccelerator(ctrlE);
		KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK);
		newItem.setAccelerator(ctrlN);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(openItem);
		fileMenu.add(renderItem);
		fileMenu.add(exportItem);
		fileMenu.add(newItem);
		
	}

	
	/**
	 * Listens to {@link #renderItem}
	 */
	private ActionListener renderListener() {
		return e -> {
	//		setFeel();
//			content = savedTxt == null ? textPane.getText() : savedTxt;
			content = textPane.getText();
//			textPane.setText(savedTxt);
			// ovo bi mogao biti feature da mozes renderat i vidjet kak izgleda otvoren file
	//		content = textPane.getText();
			if (content == null) return;
			
//			System.out.println("savedTxt is: " + skrati(content));
			rightPane = new JScrollPane(new MarkdownHandler(content, dirName));
			rightPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			splitPane.setRightComponent(rightPane);
			scrollToBottom(rightPane);
			
//			pack();
		};
	}
	
	
 
 

	// credit Matthias Braun
	/**
	 * scrolls rendered text to bottom every time it's rendered
	 * @param scrollPane panel for rendered text
	 */
	private void scrollToBottom(JScrollPane scrollPane) {
	    JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
	    AdjustmentListener downScroller = new AdjustmentListener() {
	        @Override
	        public void adjustmentValueChanged(AdjustmentEvent e) {
	            Adjustable adjustable = e.getAdjustable();
	            adjustable.setValue(adjustable.getMaximum());
	            verticalBar.removeAdjustmentListener(this);
//	            System.out.println("this is "+  this);
	        }
	    };
	    verticalBar.addAdjustmentListener(downScroller);
	 // speed up scrollbar
	    verticalBar.setUnitIncrement(32); 
	}


	private void setFeel() {
		try {
		    UIManager.setLookAndFeel( new FlatMacDarkLaf() );
		
		    
		} catch( Exception exc ) {
		    System.err.println( "Failed to initialize LaF" );
		}
	}



	/**
	 * Listens to {@link #saveItem}
	 */
	private ActionListener saveItemActionListener() {
		return e -> {
			if (saveTitle == null) saveTitle = getTitle(); 
			if (getTitle() == defaultTitle) {
				// zapisujem u privremeni dokument
				Path documentsPath = Path.of(dirName);
				System.out.println(documentsPath.toAbsolutePath().toString());
					new File(documentsPath.toString()).mkdirs();
				savedTxt = textPane.getText();
				writeToFile(fileName, savedTxt);
				// trazim novo ime tog dokumenta
				runNameFrame(extension);
				
			}
			else {
// trebalo bi bit ok jer dir sigurno vec postoji jer file postoji
				
//				Path documentsPath = Path.of(getTitle());
//				System.out.println(documentsPath.toAbsolutePath().toString());
//					new File(documentsPath.toString()).mkdirs();
				savedTxt = textPane.getText();
				writeToFile(getTitle(), savedTxt);
//				System.out.println("getTitle is " + getTitle());
				// trazim novo ime tog dokumenta
		//		runNameFrame(extension);
			}
			
		};
		
	}

	
	
	private void runNameFrame(String extension) {
		SwingUtilities.invokeLater(new Runnable() {
			 @Override
			 public void run() {
//				 System.out.println("runNameFrame is getting extension " + extension);
				 new GetNameFrame(me, extension).setVisible(true);
//				 System.out.println("runNameFrame's thread:" + Thread.currentThread());
			 }
		 });
	}
	
	private void runDefinitionFrame(String content) {
		SwingUtilities.invokeLater(new Runnable() {
			 @Override
			 public void run() {
				 dfInstance = new DefinitionFrame(content);
				 dfInstance.setVisible(true);
			 }
		 });
	}
	
	private void runFileChooserDemo() {
		SwingUtilities.invokeLater(new Runnable() {
			 @Override
			 public void run() {
				 new FileChooserDemo(me, dirName);
			 }
		 });
	}

	/**
	 * writes content to file in UTF-8 encoding, saves it
	 */
	private void writeToFile(String fileName, String content) {
		try {
			Writer bw = new BufferedWriter(
					new OutputStreamWriter(
					new BufferedOutputStream(
					new FileOutputStream(fileName)),"UTF-8"));
					// bw.write(textPane.getText());
					bw.write(content);
					// skracen ispis
//					System.out.println("writing:" + skrati(content)); 
					
					savedTxt = savedTxt == null ? textPane.getText() : savedTxt;
//					textPane.setText("");
					bw.close(); 
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			 @Override
			 public void run() {
				 if (runcnt++ == 0) {
					 TextEditor te = new TextEditor();
					 te.dispose();
//					 System.out.println("runcnt is " + runcnt);	
					 run();
				 }
				 else {
					 TextEditor te = new TextEditor();
					 te.setVisible(true);
					 System.out.println(Thread.currentThread());
					 myThread = Thread.currentThread();
				 }
			 }
		 });
	}
	
	private String skrati(String x) {
		return "\n" + (x.length() > 100 ? ("...\n" + x.substring(x.length()-100)) : x);
	}
	
	
    
    //Create the style menu.
    protected JMenu createStyleMenu() {
        JMenu menu = new JMenu("Style");
 
        Action action = new StyledEditorKit.BoldAction();
        action.putValue(Action.NAME, "Bold");
        KeyStroke ctrlB = KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK);
        JMenuItem menuItem = new JMenuItem(action);
        menuItem.setAccelerator(ctrlB);
        menu.add(menuItem);
 
        action = new StyledEditorKit.ItalicAction();
        action.putValue(Action.NAME, "Italic");
        KeyStroke ctrlI = KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK);
        menuItem = new JMenuItem(action);
        menuItem.setAccelerator(ctrlI);
        menu.add(menuItem);
 
        action = new StyledEditorKit.UnderlineAction();
        action.putValue(Action.NAME, "Underline");
        KeyStroke ctrlU = KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK);
        menuItem = new JMenuItem(action);
        menuItem.setAccelerator(ctrlU);
        menu.add(menuItem);
        
        menu.addSeparator();
 
        menu.add(new StyledEditorKit.FontSizeAction("Default 16", defaultFontSize));
        menu.add(new StyledEditorKit.FontSizeAction("12", 12));
        menu.add(new StyledEditorKit.FontSizeAction("14", 14));
        menu.add(new StyledEditorKit.FontSizeAction("18", 18));
 
        menu.addSeparator();
 
        menu.add(new StyledEditorKit.FontFamilyAction("Serif", "Serif"));
        menu.add(new StyledEditorKit.FontFamilyAction("SansSerif", "SansSerif"));
 
        menu.addSeparator();
        
        menu.add(new StyledEditorKit.ForegroundAction("Red", Color.red));
        menu.add(new StyledEditorKit.ForegroundAction("Green", Color.green));
        menu.add(new StyledEditorKit.ForegroundAction("Blue", Color.blue));
        menu.add(new StyledEditorKit.ForegroundAction("Black", Color.black));
        menu.add(new StyledEditorKit.ForegroundAction("White", Color.white));
 
        return menu;
        
        
    }
 
    // EDIT option shit
    
    protected class MyUndoableEditListener implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
		//Remember the edit and update the menus.
		undo.addEdit(e.getEdit());
		undoAction.updateUndoState();
		redoAction.updateRedoState();
		}
	}
    
  //The following two methods allow us to find an
    //action provided by the editor kit by its name.
    private HashMap<Object, Action> createActionTable(JTextComponent textComponent) {
        HashMap<Object, Action> actions = new HashMap<Object, Action>();
        Action[] actionsArray = textComponent.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
    return actions;
    }
 
    private Action getActionByName(String name) {
        return actions.get(name);
    }
 
    class UndoAction extends AbstractAction {
        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }
 
        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            redoAction.updateRedoState();
        }
 
        protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }
 
    class RedoAction extends AbstractAction {
        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }
 
        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }
 
        protected void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }
    
    protected JMenu createEditMenu() {
        JMenu menu = new JMenu("Edit");
 
        //Undo and redo are actions of our own creation.
        undoAction = new UndoAction();
        JMenuItem undoActionItem = new JMenuItem(undoAction);
        KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK);
		undoActionItem.setAccelerator(ctrlZ);
		menu.add(undoActionItem);
 
        redoAction = new RedoAction();
        JMenuItem redoActionItem = new JMenuItem(redoAction);
        KeyStroke ctrlY = KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK);
		redoActionItem.setAccelerator(ctrlY);
        menu.add(redoActionItem);
        
        KeyStroke ctrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK);
		findItem.setAccelerator(ctrlF);
		menu.add(findItem);
		
		KeyStroke ctrlD = KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK);
		inspectItem.setAccelerator(ctrlD);
		menu.add(inspectItem);
 
//      menu.addSeparator();
        
        return menu;
        
    }
    
    
    
}


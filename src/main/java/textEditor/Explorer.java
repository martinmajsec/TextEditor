package textEditor;



import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;




public class Explorer extends JPanel implements TreeSelectionListener {

	/**
	 * JTree item that will keep file hiearchy.
	 */
	private JTree tree;
	/**
	 * Root note of {@link tree}.
	 */
	private static DefaultMutableTreeNode top;
	/**
	 * Instace of {@link MyFileVisitor} to walk the tree.
	 */
	static MyFileVisitor visitor;
	/**
	 * Instance of parent frame.
	 */
	TextEditor parent;
//	GetNameFrame nameFrame;
	
	/**
	 * Creates a panel that displays file hierarchy.
	 * 
	 * @param rootName tree root
	 * @param parent reference to main frame
	 */
	public Explorer (String rootName, TextEditor parent) {
//		super("Explorer 3000");
		this.parent = parent;
//		this.nameFrame = nameFrame;
//		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		createNodes(rootName);
		tree = new JTree(top);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addTreeSelectionListener(this);
		

		JScrollPane treeView = new JScrollPane(tree);

		add(treeView, BorderLayout.CENTER);
//		setSize(500,500);
//		setMinimumSize(new Dimension(500,500));
        setVisible(true);

	}

	
	/**
	 * Creates tree.
	 * 
	 * @param rootName root file to the tree
	 */
	private static void createNodes(String rootName) {
		visitor = new MyFileVisitor();

		try {
			Path myPath = Path.of(rootName);
			Files.walkFileTree(myPath, visitor);
		} catch (Exception e) {e.printStackTrace();}
		top = MyFileVisitor.getRoot();

		Math.abs(ABORT);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
		if (node == null) return;
		Path path = visitor.getPathTree().get(node);
		if (path == null) return;
		revalidate();
//		System.out.println("running path: " + path.toString());
		readFile(path);
		
//        setVisible(false); //you can't see me!
//		dispose(); //Destroy the JFrame object
	}

	/**
	 * Reads content from {@code path} and writes it to a {@code JTextPane} in main frame.
	 * 
	 * @param path file to be read
	 */
	private void readFile(Path path) {
		String content = "";
		
		File file = new File(path.toString());

		if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }
        try {
//	        Desktop desktop = Desktop.getDesktop();
	        if(file.exists()) {
//	        	System.out.println("file exists");
    			BufferedReader br = new BufferedReader(
    			new InputStreamReader(
    			new BufferedInputStream(
    			new FileInputStream(path.toString())),"UTF-8"));
    			String line;
    			
    			while ( (line = br.readLine()) != null) {
 //   				System.out.println(line);
    				content += line + "\n";
    			}
 //   			System.out.println("content from opened file:" + skrati(content));
    			br.close(); // na kraju
	        }

        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        parent.textPane.setText(content);
	}

	


	private String skrati(String x) {
		return "\n" + (x.length() > 100 ? ("...\n" + x.substring(x.length()-100)) : x);
	}	
	
}



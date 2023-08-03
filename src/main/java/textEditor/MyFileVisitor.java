package textEditor;



import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

public class MyFileVisitor extends SimpleFileVisitor<Path> {

	/**
	 * Root node for {@code File} hierarchy tree.
	 */
	public static  DefaultMutableTreeNode root; // final
	/**
	 * Current node for walking {@code File} hierarchy tree.
	 */
	public  DefaultMutableTreeNode current = root;
	/**
	 * Collection that contains directory path as key and the corresponding node as value.
	 */
	private Map<Path, DefaultMutableTreeNode> myTree; // <dir, directory node>
	/**
	 * Collection that contains node as key and the correspoding file as value.
	 */
	private Map<DefaultMutableTreeNode, Path> pathTree; // <node, file node>

	/**
	 * Creates a visitor to walk {@code File} hierarchy tree.
	 */
	public MyFileVisitor() {
		root = new DefaultMutableTreeNode("Root");
		myTree = new HashMap<>();
		pathTree = new HashMap<>();
		myTree.put(null, root);
	}





	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//		System.out.println("file parent is: " + file.getParent());
	//	if (file.toFile().canExecute() == false) return FileVisitResult.TERMINATE;


		DefaultMutableTreeNode current = new DefaultMutableTreeNode(file.getFileName());
		
		
		pathTree.put(current, file);

		if (file.getParent() == null) {
			System.out.println("adding to lv 1: " + file.getFileName());
			root.add(current);
			return super.visitFile(file, attrs); // break
		}
		DefaultMutableTreeNode parentNode = myTree.get(file.getParent());
		if (parentNode == null) {
			System.out.println("ovaj slucaj bi trebao biti pokriven s prvim if");
		}
		
		parentNode.add(current);
		return super.visitFile(file, attrs);
	}

	// TODO svaki novi direktorij treba bit novo stablo

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		

		// TODO Auto-generated method stub
		DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(dir.getFileName());
		DefaultMutableTreeNode parentNode = myTree.get(dir.getParent());

//		System.out.println(dir.toString() + ", my parent is " + parentNode.toString());
		
		if (parentNode == null || parentNode == root) {


//			System.out.println("new dir is " + dir.toString());
			root.add(dirNode);
//			System.out.println("root.add(dirNode)");
		}
		else {
			
			parentNode.add(dirNode);
//			System.out.println("dir is " + dir.toString());
		}
		myTree.put(dir, dirNode);



		return super.preVisitDirectory(dir, attrs);
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		// TODO Auto-generated method stub
		return super.postVisitDirectory(dir, exc);
	}

	public static DefaultMutableTreeNode getRoot() {
		return root;
	}

	public Map<Path, DefaultMutableTreeNode> getMyTree() {
		return myTree;
	}

	public Map<DefaultMutableTreeNode, Path> getPathTree() {
		return pathTree;
	}

}

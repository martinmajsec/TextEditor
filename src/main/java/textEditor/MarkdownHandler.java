package textEditor;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class MarkdownHandler extends JPanel {

	/**
	 * Default font for text in text panel.
	 */
	Font defaultFont = new Font("defaultFont", Font.PLAIN, 16);
	/**
	 * {@code String} to save formatted content.
	 */
	String html;
	
	/**
	 * Formats content, saves it to a backup file and displays it in a panel.
	 * <p>
	 * Content is avaiable with {@link #getContent()}. 
	 * 
	 * @param content text to be formatted
	 * @param dirName name of directory where backup file is to be saved
	 */
	public MarkdownHandler(String content, String dirName) {
		
		// this = panel
		
		JEditorPane jEditorPane = new JEditorPane();
	    jEditorPane.setEditable(false); 
		
		render(content);
//		System.out.println("html is:" + skrati(html));
		
		new File(dirName).mkdirs();
		String fileName = dirName + "htmlOutput.html"; 
		File htmlFile = new File(fileName);
		try {
			// write to file
			Writer bw = new BufferedWriter(
					new OutputStreamWriter(
					new BufferedOutputStream(
					new FileOutputStream(htmlFile)),"UTF-8"));
			bw.write(html);
			bw.close();
	
			// openFile(htmlFile);
	        
			// display formatted content in panel
			try {
				URL url = new File(fileName).toURI().toURL();
				jEditorPane.setPage(url);
				jEditorPane.setFont(defaultFont);
			}
			catch (Exception exc) {
				exc.printStackTrace();
				jEditorPane.setContentType("text/html");
		        jEditorPane.setText("<html>Page not found.</html>");
			}
			JScrollPane jScrollPane = new JScrollPane(jEditorPane);		
		    add(jScrollPane);     
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * Unused code to open file.
	 * @param htmlFile file to be opened
	 * @throws IOException if file can't be opened
	 */
	@SuppressWarnings("unused")
	private void openFile(File htmlFile) throws IOException {
		if(!Desktop.isDesktopSupported()){
		    System.out.println("Desktop is not supported");
		    return;
		}
      
		Desktop desktop = Desktop.getDesktop();
		if(htmlFile.exists()) {
			desktop.open(htmlFile);
		}
	}

	/**
	 * Saves HTML formatted content to {@link #html}.
	 * @param content text to be formatted
	 */
	private void render(String content) {
		MutableDataSet options = new MutableDataSet();
		options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
		
		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();
		Node document = parser.parse(content);
		html = renderer.render(document);
	}

	// UI driver
	/*
	public static void main(String[] args) {
		String content  = "-neki ludi\n-tekst\n";
		createWindow(content);
	}
	 */
	
	private String skrati(String x) {
		return "\n" + (x.length() > 100 ? "...\n" + x.substring(x.length()-100) : x);
	}
	
	/**
	 * Gets HTML formatted content.
	 * @return
	 */
	public String getContent () {
		return html;
	}
}

package textEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

public class DefinitionFrame extends JFrame {

	/**
	 * Default value for the number of characters in a line. 
	 */
	final int LINE_WIDTH = 100;
	
	/**
	 * Creates a new frame that displays given {@code content}. 
	 * By default, the frame is positioned in the middle of the screen.
	 * 
	 * @param content a dictionary entry
	 */
	
	public DefinitionFrame(String content) {
		super("Definition");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JTextPane txPane = new JTextPane();
		txPane.setContentType("text/html");
		txPane.setText(format(content));
//		System.out.println("content size is " + content.length());
//		System.out.println(content);
		add(txPane);
		addEscapeListener(this);
		pack();
		setLocationRelativeTo(null);
	}
	
	/**
	 * Creates a listener that closes the frame when esc key is hit.
	 * 
	 * @param frame parent frame to listen to
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
	 * Adds HTML tags as the {@code content} is in HTML format. 
	 * Adds line break at first space after {@link LINE_WIDTH} characters. 
	 * In other words, after HTML tags are passed, after each {@link LINE_WIDTH} 
	 * characters, when a space is encountered, a line break will be added.
	 * 
	 * 
	 * @param content {@code String} to format
	 * @return {@code String} in HTML format
	 */
	
	private String format (String content) {
		String out = "<html>";
		int cnt = 0;
		for (int i = 0;i < content.length();i++) {
			out += content.charAt(i);
			if (content.charAt(i) == '>') cnt = 0; // reset counter if HTML tag is found
			cnt++;
			if (cnt > LINE_WIDTH && content.charAt(i) == ' ') { 
				// new line at first space after LINE_WIDTH characters
				out += "<br>";
				cnt = 0;
			}
		}
		out += "</html>";
		return out;
	}
	
}

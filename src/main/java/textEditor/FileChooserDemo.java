
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
package textEditor;
 
import java.io.*;
import java.nio.file.Path;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;


 



public class FileChooserDemo {

	/**
	 * {@link JFileChooser} item to select file.
	 */
    JFileChooser fileChooser;
    /**
     * Reference to main frame.
     */
    TextEditor parent;
 
    public FileChooserDemo(TextEditor parent, String rootName) {
    	
        this.parent = parent;
        //Create a file chooser
        fileChooser = new JFileChooser(rootName);

        // restricted file chooser
//		FileSystemView fsv = new DirectoryRestrictedFileSystemView(new File(rootName));
//      fileChooser = new JFileChooser(fsv.getHomeDirectory(),fsv);
        
 
        //Uncomment one of the following lines to try a different
        //file selection mode.  The first allows just directories
        //to be selected (and, at least in the Java look and feel,
        //shown).  The second allows both files and directories
        //to be selected.  If you leave these lines commented out,
        //then the default mode (FILES_ONLY) will be used.
        //
        //fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
 
        int returnVal = fileChooser.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //This is where a real application would open the file.
            runFile(Path.of(file.getPath()));
        } 
    }

    /**
	 * Reads content from {@code path} in UTF-8 encoding and writes it to a {@link JTextPane} in main frame.
	 * 
	 * @param path file to be read
	 */
 
    private void runFile(Path path) {
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
//    			System.out.println("content from opened file:" + skrati(content));
    			br.close(); // na kraju
	        }
	        
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        parent.textPane.setText(content);
        parent.savedTxt = content;
        parent.setTitle(path.toString());
	}

	


	private String skrati(String x) {
		return "\n" + (x.length() > 100 ? ("...\n" + x.substring(x.length()-100)) : x);
	}
	
	
//	@Override
	public class DirectoryRestrictedFileSystemView extends FileSystemView
	{
	    private final File[] rootDirectories;

	    public DirectoryRestrictedFileSystemView(File rootDirectory)
	    {
	        this.rootDirectories = new File[] {rootDirectory};
	    }

	    public DirectoryRestrictedFileSystemView(File[] rootDirectories)
	    {
	        this.rootDirectories = rootDirectories;
	    }

	    @Override
	    public File createNewFolder(File containingDir) throws IOException
	    {       
	        throw new UnsupportedOperationException("Unable to create directory");
	    }

	    @Override
	    public File[] getRoots()
	    {
	        return rootDirectories;
	    }

	    @Override
	    public boolean isRoot(File file)
	    {
	        for (File root : rootDirectories) {
	            if (root.equals(file)) {
	                return true;
	            }
	        }
	        return false;
	    }
	    public File getHomeDirectory()
	    {
	      return rootDirectories[0];
	    }
	}
	
    
}

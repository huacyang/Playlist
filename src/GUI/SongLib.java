package GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Hua Yang
 */
public class SongLib extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	 int previousPoint;
	 static LinkedList<String[]> linked_list;
	 JList<String> song_list, detail_list;
     JButton add, edit;
     JSplitPane jsplitpanel;
     DefaultListModel<String> song_list_model, detail_list_model;
    
    /**
     * Creates a bordered layout for JFrame
     */
    public SongLib() throws IOException
    {
        super(new BorderLayout());
        
        previousPoint = -2;
        linked_list = new LinkedList<String[]>();
        song_list_model = new DefaultListModel<String>();
        detail_list_model = new DefaultListModel<String>();
        
        // creates the scroll for the song panel
        song_list = new JList<String>(song_list_model);
        song_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        song_list.setSelectedIndex(0);
        /*song_list.setVisibleRowCount(15);*/
        song_list.addListSelectionListener(new selectListener());
        JScrollPane song_scrollpanel = new JScrollPane(song_list);
        
        // opens the default save file
        read_text("SongLib.txt");
        
        // creates the scroll for the detail panel
        detail_list = new JList<String>(detail_list_model);
        detail_list.clearSelection();
        detail_list.setSelectedIndex(0);
        detail_list.setVisibleRowCount(15);
        JScrollPane detail_scrollpanel = new JScrollPane(detail_list); 
        
        jsplitpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, song_scrollpanel, detail_scrollpanel);
        jsplitpanel.setOneTouchExpandable(true);
        jsplitpanel.setDividerLocation(250);
        
        // creates the label panel with given string
        String[] title = {"Song Library"};
        JPanel titlePanel = makeListPanel(title, true);
        
        // creates the panel with add/edit/save buttons
        JPanel buttonPanel = makeButtonPanel();
        
        // lays out the panels
        add(titlePanel, BorderLayout.NORTH);
        add(jsplitpanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        validate();
    }

	/**
     * Checks if there are duplicates by using the song's name and artist,
     *  returns true if it is a duplicate, false otherwise
     */
    public boolean checkDuplicates(String song, String artist)
    {
    	ListIterator<String[]> iterator = linked_list.listIterator();
		// Traverse through the linked list
		while (iterator.hasNext())
		{
			String[] detail = iterator.next();
			if (detail[0].equalsIgnoreCase(song) == true && detail[1].equalsIgnoreCase(artist) == true)
				return true;
		}
    	return false;
    }

    /**
     * Checks the user's input on the dialog box,
     *  returns true if there were input, false otherwise
     */
    public boolean checkInput(String input)
    {
    	if (input != null) { // when user hits 'Ok' 
			if (input.length() == 0) // when user enters nothing
				showMessage("empty");
			else // when user enters something
				return true;
    	}
		else // when user hits 'Cancel'
			showMessage("cancel");
    	return false;
    }
    
    /**
     * Creates a button panel of 'Add/Edit/Save' buttons 
     */
    public JPanel makeButtonPanel()
    {
    	JPanel panel = new JPanel();
    	add = new JButton("Add");
    	add.addActionListener(new addListener());
    	edit = new JButton("Edit");
    	edit.addActionListener(new editListener());
    	panel.add(add);
        panel.add(Box.createHorizontalStrut(50));
    	panel.add(edit);
    	return panel;
    }
    
    /**
     * Creates a panel of labels with the given 'String[] titles',
     *  set 'boolean separators' to true to insert separators
     */
    public JPanel makeListPanel(String[] titles, boolean separators)
    {
    	JPanel panel = new JPanel();
    	for (int i = 0; i < titles.length; i++)
    	{
            panel.add(Box.createHorizontalStrut(25));
	    	if (i != 0 && separators == true)
	    		panel.add(new JSeparator(SwingConstants.VERTICAL));
	    	
	    	JLabel jlabel = new JLabel(titles[i]);
		    panel.add(jlabel);
            panel.add(Box.createHorizontalStrut(25));
    	}
    	return panel;
    }
    
    /**
     * Opens and stores the given text file into the linked list
     */
    public void read_text(String textfile) throws IOException
    {
    	File file = new File(textfile);
    	if (file.exists()) {
	    	BufferedReader readed = new BufferedReader(new FileReader(file));
	    	String text = null;
	    	String[] array = new String[4];
	    	int count = 0;
	    	
	    	text = readed.readLine();
	    	if (text != null) { // reads through the file line by line
		    	while (text != null)
		    	{
		    		array[count] = text;
		    		count++;
		    		if (count == 1) { // adds the name of the song onto the song panel
			    		song_list_model.addElement(text);
		    		} else if (count == 4) { // after adding the 4 elements into the array, store the array in the linked list
		    			linked_list.add(array);
		    			// refreshes the array and the counter
		    			array = new String[4];
		    			count = 0;
		    		}
		    		text = readed.readLine();
		    	}
		    	JOptionPane.showMessageDialog(null,
						"There were contents in the default save file 'SongLib.txt',\n" +
						"and it has been loaded.", "Loading file", JOptionPane.WARNING_MESSAGE);
	    	}
    	}
    }
    
    /**
     * Attempts to save the information into file
     */
    public static void save()
    {
    	try {
			FileWriter save_file = new FileWriter("SongLib.txt");
			ListIterator<String[]> iterator = linked_list.listIterator();
			// Traverses through the linked list
			while (iterator.hasNext())
			{
				String[] detail = iterator.next();
				for (int n = 0; n < detail.length; n++)
					save_file.write(detail[n] + "\n");
			}
			save_file.close();
			JOptionPane.showMessageDialog(null,
					"Data has been saved to the default file @ 'SongLib.txt'.",
					"Save data", JOptionPane.WARNING_MESSAGE);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    
    /**
     * Shows the message dialog corresponding to the given command
     */
    public void showMessage(String option)
    {
    	if (option.equalsIgnoreCase("empty")) {
			JOptionPane.showMessageDialog(null,
					"You did not enter anything! The process will now end, please try again later.",
					"Error: empty", JOptionPane.ERROR_MESSAGE);
    	} else if (option.equalsIgnoreCase("wrong")) {
    		JOptionPane.showMessageDialog(null,
					"You have entered the wrong option! The process will now end, please try again later.",
					"Error: wrong format", JOptionPane.ERROR_MESSAGE);
    	} else if (option.equalsIgnoreCase("cancel")) {
    		JOptionPane.showMessageDialog(null,
					"The process has been terminated per your request.",
					"Cancellation", JOptionPane.WARNING_MESSAGE);
    	}
    }
    
    /**
     * Implements the action listener for the add button
     * @author Hua Yang
     */
    class addListener implements ActionListener
    {
		public void actionPerformed(ActionEvent e)
		{
			String song_name, song_album, song_artist, song_year;
			// Will continue only if user enters something
			song_name = JOptionPane.showInputDialog(null, 
					"Please enter the song's name:", 
					"Input song's name", JOptionPane.INFORMATION_MESSAGE);
			if (checkInput(song_name) == true) { // after entering the song's name
				song_artist = JOptionPane.showInputDialog(null,
						"Please enter the song's artist:",
						"Input song's artist", JOptionPane.INFORMATION_MESSAGE);
				if (checkInput(song_artist) == true) { // after entering the song's artist
					if (checkDuplicates(song_name, song_artist) == true) { // checks for duplicates
						JOptionPane.showMessageDialog(null,
								"The song '" + song_name + "' by '" + song_artist + 
								"' already exist,\n therefore, it will not be added.",
								"Duplicates", JOptionPane.WARNING_MESSAGE);
					} else {
						song_album = JOptionPane.showInputDialog(null,
								"Please enter the song's album:",
								"Input song's album", JOptionPane.INFORMATION_MESSAGE);
						if (checkInput(song_album) == true) { // after entering the song's album
							song_year = JOptionPane.showInputDialog(null,
									"Please enter the song's year:",
									"Input song's year", JOptionPane.INFORMATION_MESSAGE);
							if (checkInput(song_year) == true) { // after entering the song's year
								song_list_model.addElement(song_name);
								String[] elements = {song_name, song_artist, song_album, song_year};
								linked_list.add(elements);
							}
						}
					}
				}
			}
		}
    }
    
    /**
     * Implements the action listener for the edit button
     * @author Hua Yang
     */
    class editListener implements ActionListener
    {
		public void actionPerformed(ActionEvent e)
		{
			int pointAt = song_list.getSelectedIndex();
			
			if (pointAt < 0) { // user have not selected anything
				JOptionPane.showMessageDialog(null,
						"You have not selected anything!",
						"Error: no selection", JOptionPane.ERROR_MESSAGE);
			} else {
				String songOption = JOptionPane.showInputDialog(null,
						"You are editing the song '" + song_list_model.elementAt(pointAt)
						+ "',\n please enter the respective number:\n"
						+ "	 0. Song's name\n"
						+ "	 1. Song's artist\n"
						+ "	 2. Song's album\n"
						+ "	 3. Song's year",
						"Pick option", JOptionPane.INFORMATION_MESSAGE);
				if (songOption != null) { // when user hits 'Ok' 
					if (songOption.length() == 0) {	// when user enters nothing
						showMessage("empty");
					} else if (!songOption.equals("0") && !songOption.equals("1") 
							&& !songOption.equals("2") && !songOption.equals("3")) { // when user enters wrong option
						showMessage("wrong");
					} else { // when user enters an option to edit
						String songinfo = JOptionPane.showInputDialog(null, 
								"Please enter the new information", 
								"Update song", JOptionPane.INFORMATION_MESSAGE);
						if (songinfo != null) { // when user hits 'Ok'
							if (songOption.length() == 0) {	// when user enters nothing
								showMessage("empty");
							} else if (songOption.length() > 0){ // when user enters something
								// updates the linked list with the given piece of information
								String[] detail = linked_list.get(pointAt);
								detail[(Integer.parseInt(songOption))] = songinfo;
								// updates the two panels accordingly
								if (songOption.equals("0")) {
									song_list_model.setElementAt(songinfo, pointAt);
								} else {
									int at = 1;
									if (songOption.equals("2"))
										at = 4;
										else if (songOption.equals("3"))
											at = 7;
									detail_list_model.setElementAt(songinfo, at);
								}
							} else { // when user exits
								showMessage("cancel");
							}
						}
					}
				} else { // when user hits 'Cancel'
					showMessage("cancel");
				}
			}
		}
    }
    
    /**
     * Implements the list selection listener
     * @author Hua Yang
     */
    class selectListener implements ListSelectionListener
    {
		public void valueChanged(ListSelectionEvent arg0)
		{
			int pointAt = song_list.getSelectedIndex();
			// this will erase the previously shown elements
			if (pointAt != previousPoint) {
				previousPoint = pointAt;
				detail_list_model.removeAllElements();
			} else {
				String[] detail = linked_list.get(pointAt);
				detail_list_model.addElement("Artist:");
				detail_list_model.addElement(detail[1]);
				detail_list_model.addElement("------");
				detail_list_model.addElement("Album:");
				detail_list_model.addElement(detail[2]);
				detail_list_model.addElement("------");
				detail_list_model.addElement("Year:");
				detail_list_model.addElement(detail[3]);
			}
		}
    }
	
	public static void main(String[] args) throws IOException
	{
		JFrame jframe = new JFrame("SongLib");
		
		JComponent jcomponent = new SongLib();
		jcomponent.setOpaque(true);
		// saves and then close program upon exit
		jframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				save();
				System.exit(0);
			}
		});
		
		jframe.setSize(400,350);
		jframe.setVisible(true);
		jframe.setMinimumSize(jframe.getSize());
		jframe.setContentPane(jcomponent);
		jframe.setLocationRelativeTo(null);
	}
}

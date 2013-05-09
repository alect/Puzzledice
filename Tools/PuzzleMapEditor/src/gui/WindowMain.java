package gui;
import puzzledice.*;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.Component;
//import org.eclipse.wb.swing.FocusTraversalOnArray;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.BorderLayout;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import javax.swing.JSeparator;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import javax.swing.JTextPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class WindowMain {

	private static JFrame frmPuzzledicePuzzleEditor;

	//The layout component available for editing the graph 
	private static mxOrganicLayout areaGraphLayout;
	private static mxGraph areaGraph;
	private static mxGraphComponent areaGraphComponent;
	
	private static mxOrganicLayout puzzleGraphOrganicLayout;
	private static mxHierarchicalLayout puzzleGraphHierLayout;
	private static mxGraph puzzleGraph;
	private static mxGraphComponent puzzleGraphComponent;
	private static mxCell hierarchyRoot;
	
	private AreaEditPanel areaEditPanel;
	private PuzzleEditPanel puzzleEditPanel;
	
	JTabbedPane graphPanel;
	
	private static JLabel lblTextPanel;
	private static JTextArea txtTextPanel;
	
	private File _openFile;
	private String _emptyXml;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Puzzle Map Editor");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WindowMain window = new WindowMain();
					window.frmPuzzledicePuzzleEditor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WindowMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPuzzledicePuzzleEditor = new JFrame();
		frmPuzzledicePuzzleEditor.setTitle("Puzzledice Puzzle Map Editor");
		frmPuzzledicePuzzleEditor.setBounds(100, 100, 743, 585);
		frmPuzzledicePuzzleEditor.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmPuzzledicePuzzleEditor.getContentPane().setLayout(new BorderLayout(0, 0));
		frmPuzzledicePuzzleEditor.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				if (onExit()) 
					System.exit(0);
			}
		});
		// Set up the program to catch OSX quit events 
		try {
			OSXAdapter.setQuitHandler(this, this.getClass().getMethod("onExit", new Class[] {}));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		JMenuBar menuBar = new JMenuBar();
		frmPuzzledicePuzzleEditor.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				clear();
				_openFile = null;
			}
		});
		mnFile.add(mntmNew);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				// Run the file dialogue later since it's blocking
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						FileDialog chooser = new FileDialog(frmPuzzledicePuzzleEditor, "Open", FileDialog.LOAD);
						chooser.setVisible(true);
						if (chooser.getFile() != null) {
 							clear();
							File file = new File(chooser.getDirectory(), chooser.getFile());
							if(Loader.LoadFromXML(file)) {
								areaEditPanel.justLoaded();
								puzzleEditPanel.justLoaded();
								_openFile = file;
							}
							else { 
								JOptionPane.showMessageDialog(frmPuzzledicePuzzleEditor, "File failed to open!", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				});
			}
		});
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Invoke the save later since it's a blocking method
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						save();
					}
				});
				
			}
		});
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						String xml = xmlDigest();
						saveAs(xml);
					}
				});
			}
		});
		mnFile.add(mntmSaveAs);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (onExit())
					System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmUndo = new JMenuItem("Undo (TODO)");
		mnEdit.add(mntmUndo);
		
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JPanel panel = new JPanel();
		frmPuzzledicePuzzleEditor.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel editPanel = new JPanel();
		panel.add(editPanel);
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setMinimumSize(new Dimension(5, 0));	
		separator_2.setMaximumSize(new Dimension(5, 0));
		separator_2.setPreferredSize(new Dimension(5, 0));
		editPanel.add(separator_2);
		
		areaEditPanel = new AreaEditPanel();
		editPanel.add(areaEditPanel);
		
		JSeparator separator = new JSeparator();
		separator.setMinimumSize(new Dimension(5, 0));	
		separator.setMaximumSize(new Dimension(5, 0));
		separator.setPreferredSize(new Dimension(5, 0));
		editPanel.add(separator);
		
		puzzleEditPanel = new PuzzleEditPanel();
		editPanel.add(puzzleEditPanel);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setMinimumSize(new Dimension(5, 0));	
		separator_1.setMaximumSize(new Dimension(5, 0));
		separator_1.setPreferredSize(new Dimension(5, 0));
		editPanel.add(separator_1);
		
		graphPanel = new JTabbedPane(JTabbedPane.TOP);
		graphPanel.setPreferredSize(new Dimension(frmPuzzledicePuzzleEditor.getBounds().width, frmPuzzledicePuzzleEditor.getBounds().height));
		//graphPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		panel.add(graphPanel);
		graphPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		
		areaGraph = new mxGraph();
		
		
		areaGraph.setAutoSizeCells(true);
		areaGraphComponent = new mxGraphComponent(areaGraph);
		areaGraphComponent.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				areaGraphLayout = new mxOrganicLayout(areaGraph, new Rectangle(0, 0, graphPanel.getBounds().width, graphPanel.getBounds().height));
				areaGraphLayout.execute(areaGraph.getDefaultParent());
			}
		});
		areaGraphComponent.setEnabled(false);
		areaGraphLayout = new mxOrganicLayout(areaGraph, new Rectangle(0, 0, graphPanel.getBounds().width, graphPanel.getBounds().height));
		//areaGraphLayout.setDisableEdgeStyle(false);
		
		areaGraph.setEnabled(false);
		
		graphPanel.addTab("Area Graph", null, areaGraphComponent, null);
		
		
		puzzleGraph = new mxGraph();
		
		puzzleGraph.setAutoSizeCells(true);

		
		puzzleGraphComponent = new mxGraphComponent(puzzleGraph);
		puzzleGraphComponent.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
			}
		});
		puzzleGraphComponent.setEnabled(false);
		puzzleGraphOrganicLayout = new mxOrganicLayout(puzzleGraph, new Rectangle(0, 0, graphPanel.getBounds().width, graphPanel.getBounds().height));
		puzzleGraphHierLayout = new mxHierarchicalLayout(puzzleGraph, SwingConstants.WEST);
		
		
		graphPanel.addTab("Puzzle Graph", null, puzzleGraphComponent, null);
		//textPanel.addTab("Text Panel 1", null, lblTextPanel, null);
		txtTextPanel = new JTextArea("Textual description");
		panel.add(txtTextPanel);
		txtTextPanel.setLineWrap(true);
		txtTextPanel.setWrapStyleWord(true);
		txtTextPanel.setEditable(false);
		
		lblTextPanel = new JLabel("Textual description");
		frmPuzzledicePuzzleEditor.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				graphPanel.setPreferredSize(new Dimension(frmPuzzledicePuzzleEditor.getBounds().width, frmPuzzledicePuzzleEditor.getBounds().height));
			}
		});
		
		_emptyXml = xmlDigest();
	}
	
	// function that executes the area graph layout through an animation. 
	public static void updateAreaGraph()
	{
		frmPuzzledicePuzzleEditor.setEnabled(false);
		areaGraph.getModel().beginUpdate();
		try
		{
			areaGraphLayout.execute(areaGraph.getDefaultParent());
		}
		finally
		{
			mxMorphing morph = new mxMorphing(areaGraphComponent, 20, 1.2, 20);
			morph.addListener(mxEvent.DONE, new mxIEventListener() {
				public void invoke(Object sender, mxEventObject evt)
				{
					frmPuzzledicePuzzleEditor.setEnabled(true);
					areaGraph.getModel().endUpdate();
				}
			});
			
			morph.startAnimation();
		}
		
	}
	
	// Create a new document
	public void clear() 
	{
		areaEditPanel.clear();
		puzzleEditPanel.clear();
		
		areaGraph = new mxGraph();
		areaGraph.setAutoSizeCells(true);
		areaGraphComponent.setGraph(areaGraph);
		areaGraphLayout = new mxOrganicLayout(areaGraph, new Rectangle(0, 0, graphPanel.getBounds().width, graphPanel.getBounds().height));
		//areaGraphLayout.setDisableEdgeStyle(false);
		areaGraph.setEnabled(false);
		
		puzzleGraph = new mxGraph();
		puzzleGraph.setAutoSizeCells(true);
		puzzleGraphComponent.setGraph(puzzleGraph);
		puzzleGraphOrganicLayout = new mxOrganicLayout(puzzleGraph, new Rectangle(0, 0, graphPanel.getBounds().width, graphPanel.getBounds().height));
		puzzleGraphHierLayout = new mxHierarchicalLayout(puzzleGraph, SwingConstants.WEST);
		hierarchyRoot = null;
		
		AreaEditPanel.reset();
		CombinePuzzleBlock.reset();
		InsertionPuzzleBlock.reset();
		DoorUnlockBlock.reset();
		FilterBlock.reset();
		InsertionPuzzleBlock.reset();
		ItemRequestPuzzleBlock.reset();
		PropertyChangePuzzleBlock.reset();
		SpawnPuzzleBlock.reset();
		OutputBlock.reset();
		ORBlock.reset();
	}
	
	// function that executes the puzzle graph layout(s) through an animation
	// Uses the organic layout to first separate independent nodes, then uses the 
	// hierarchical layout to make it look nifty
	public static void updatePuzzleGraph()
	{
		frmPuzzledicePuzzleEditor.setEnabled(false);
		puzzleGraph.getModel().beginUpdate();
		try
		{
			puzzleGraphHierLayout.execute(puzzleGraph.getDefaultParent());
		}
		finally
		{
			mxMorphing morph = new mxMorphing(puzzleGraphComponent, 20, 1.2, 20);
			morph.addListener(mxEvent.DONE, new mxIEventListener() {
				public void invoke(Object sender, mxEventObject evt)
				{
					puzzleGraph.getModel().endUpdate();
					frmPuzzledicePuzzleEditor.setEnabled(true);
				}
			});
			morph.startAnimation();
		}
	}

	// Public function that allows the editing panels access to the graph layout
	public static mxGraphLayout getAreaGraphLayout() 
	{
		return areaGraphLayout;
	}
	
	public static mxGraph getAreaGraph()
	{
		return areaGraph;
	}
	
	public static mxGraph getPuzzleGraph()
	{
		return puzzleGraph;
	}
	
	public static mxCell getHierarchyRoot()
	{
		if(hierarchyRoot == null) {
			hierarchyRoot = (mxCell)puzzleGraph.insertVertex(puzzleGraph.getDefaultParent(), null, "Start", 0, 0, 0, 0);
			puzzleGraph.updateCellSize(hierarchyRoot);
		}
			
		return hierarchyRoot;
	}
	
	public static mxGraphLayout getPuzzleGraphOrganicLayout()
	{
		return puzzleGraphOrganicLayout;
	}
	
	
	public static void updateTextDescription(String newDescription)
	{
		txtTextPanel.setText(newDescription);
	}
	
	private boolean save() 
	{
		String xml = xmlDigest();
		if (_openFile != null) {
			try { 
				BufferedWriter writer = new BufferedWriter(new FileWriter(_openFile));
				writer.write(xml);
				writer.close();
				return true;
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
				return false;
			}
		}
		else 
			return saveAs(xml);
	}
	
	private boolean saveAs(String xml) 
	{
		FileDialog chooser = new FileDialog(frmPuzzledicePuzzleEditor, "Save", FileDialog.SAVE);
		chooser.setVisible(true);
		if (chooser.getFile() != null) {
			try { 
				File saveFile = new File(chooser.getDirectory(), chooser.getFile());
				BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
				writer.write(xml);
				writer.close();
				_openFile = saveFile;
				return true;
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	
	public static String xmlDigest()
	{
		// Go through our whole list of areas and puzzles and print their xml
		String xml = "<puzzleMap>\n";
		xml += "<areas>\n";
		AreaBlock[] areas = AreaEditPanel.getAreaList();
		for (AreaBlock area : areas) {
			xml += area.toXML() + "\n";
		}
		xml += "</areas>\n<puzzles>\n";
		PuzzleBlock[] puzzles = PuzzleEditPanel.getBlockList();
		for (PuzzleBlock puzzle : puzzles) {
			xml += puzzle.toXML() + "\n";
		}
		xml += "</puzzles>\n</puzzleMap>";
		return xml;
	}
	
	public boolean onExit() {
		if (!unsavedChanges())
			return true;
		else { 
			int response = JOptionPane.showConfirmDialog(frmPuzzledicePuzzleEditor, "Save Unsaved Changes?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION);
			if ((response == JOptionPane.YES_OPTION && save()) || response == JOptionPane.NO_OPTION)
				return true;
		}
		return false;
	}
	
	private Boolean unsavedChanges() {
		String currentXml = xmlDigest();
		String oldXml;
		if (_openFile == null)
			oldXml = _emptyXml;
		else { 
			
			try {
				FileInputStream stream = new FileInputStream(_openFile);
				FileChannel fc = stream.getChannel();
				MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
				/* Instead of using default, pass in a decoder. */
				oldXml = Charset.defaultCharset().decode(bb).toString();
			 }
			 catch (Exception e) {
				 e.printStackTrace();
				 return true;
			 }
		}
		return !(currentXml.trim()).equals(oldXml.trim());
	}
	
	
}

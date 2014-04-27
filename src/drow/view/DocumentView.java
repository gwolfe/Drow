package drow.view;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;

import drow.document.DrowDocument;
import drow.document.DrowDocumentManager;
import drow.document.DrowPage;
import drow.gui.DrowGui;
import drow.gui.WordTabs;
import drow.io.Filters;

public class DocumentView extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public static int WINDOW_WIDTH = 750;
	public static int WINDOW_HEIGHT = 500;
	
	public static boolean IS_IN_DEV_MODE = false;
	
	private DrowDocumentManager docManager;
	private DrowDocument wordDoc;
	
	private DrowGui gui;

	public DocumentView() {
		
		Filters.setUp();
		
		wordDoc = new DrowDocument();
		
		docManager = new DrowDocumentManager(this);
		
		gui = new DrowGui(this, new WordTabs(this));
		
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setTitle("Drow Word");
		this.setVisible(true);
		
		this.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {
				WINDOW_WIDTH = getWidth();
				WINDOW_HEIGHT = getHeight();
				wordDoc.determinePageX();
				wordDoc.setPreferredSize(new Dimension(DrowPage.WIDTH, DrowDocument.BOTTOM_OF_LAST_PAGE));
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				WINDOW_WIDTH = getWidth();
				WINDOW_HEIGHT = getHeight();
				wordDoc.determinePageX();
				wordDoc.setPreferredSize(new Dimension(DrowPage.WIDTH, DrowDocument.BOTTOM_OF_LAST_PAGE));
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
			}
		});
		
		wordDoc.determinePageX();
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public DrowDocumentManager getDrowDocumentManager() {
		return docManager;
	}
	
	public DrowDocument getDrowDocument() {
		return wordDoc;
	}
	
	public DrowGui getGui() {
		return gui;
	}

	public DocumentView getRoot() {
		return this;
	}
}

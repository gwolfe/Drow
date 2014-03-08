package drow.document;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.StyleContext;

import sl.docx.DocxDocument;
import drow.styles.DrowStyleActions;
import drow.styles.DrowStyles;

public class DrowDocument extends JPanel implements CaretListener {

	private static final long serialVersionUID = 1L;
	
	private StyleContext styleContext;
    private DocxDocument styledDocument;
	private JTextPane textPane;
	
	private int dot;
	private int mark;
	
	public DrowDocument(JPanel view) {
		
		new DrowStyles();
		
		styleContext = new StyleContext();
		styledDocument = new DocxDocument(styleContext);
		styleContext.addStyle("MainStyle", styleContext.getStyle(StyleContext.DEFAULT_STYLE));
		textPane = new JTextPane(styledDocument);

		view.add(textPane);

		textPane = new JTextPane();
		view.add(textPane); 
	}
	
	@Override
	public void caretUpdate(CaretEvent e) {
		this.dot = e.getDot();
		this.mark = e.getDot();
	}

	public StyleContext getStyleContext() {
		return styleContext;
	}

	public void setStyleContext(StyleContext styleContext) {
		this.styleContext = styleContext;
	}

	public DocxDocument getStyledDocument() {
		return styledDocument;
	}

	public void setStyledDocument(DocxDocument styledDocument) {
		this.styledDocument = styledDocument;
	}
	
	public JTextPane getTextPane() {
		return textPane;
	}

	public void setTextPane(JTextPane textPane) {
		this.textPane = textPane;
	}
	
	public int getDot() {
		return dot;
	}
	
	public int getMark() {
		return mark;
	}
}

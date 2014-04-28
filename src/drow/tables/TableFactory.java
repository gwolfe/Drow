package drow.tables;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * <h1>TableFactory</h1>
 * @author Lee Mc Donald
 */
class TableFactory implements ViewFactory {
	
	@Override
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new LabelView(elem);
            }
            else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                return new ParagraphView(elem);
            }
            else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new BoxView(elem, View.Y_AXIS);
            }
            else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(elem);
            }
            else if (kind.equals(TableDocument.ELEMENT_NAME_TABLE)) {
                return new TableView(elem);
            }
            else if (kind.equals(TableDocument.ELEMENT_NAME_ROW)) {
                return new RowView(elem);
            }
            else if (kind.equals(TableDocument.ELEMENT_NAME_CELL)) {
                return new CellView(elem);
            }
            else if (kind.equals(StyleConstants.IconElementName)) {
                return new IconView(elem);
            }
        }

        // default to text display
        return new LabelView(elem);
    }
}

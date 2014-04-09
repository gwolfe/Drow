package drow.pagination;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

public class PaginationKit extends StyledEditorKit {


	private static final long serialVersionUID = 1L;
	
	PageableViewFactory factory = new PageableViewFactory();
    protected int pageWidth = 200;
    protected int pageHeight = 320;
    public static int DRAW_PAGE_INSET = 15;
    protected Insets pageMargins = new Insets(10, 10, 10, 10);

    public PaginationKit() {
    }
    
    public void setPageWidth(int width) {
        pageWidth = width;
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public void setPageHeight(int height) {
        pageHeight = height;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public void setPageMargins(Insets margins) {
        pageMargins = margins;
    }

    public void init(JFrame frame, JTextPane textPane) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textPane.setEditorKit(this);
        JScrollPane scroll = new JScrollPane(textPane);
        frame.getContentPane().add(scroll);
        frame.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());

        //return frame;
    }

    /*public static void main(String[] args) {
        new PaginationKit().init().setVisible(true);
    }*/

    public ViewFactory getViewFactory() {
        return factory;
    }

    class PageableViewFactory implements ViewFactory {
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new LabelView(elem);
                }
                else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new PageableParagraphView(elem);
                }
                else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new SectionView(elem, View.Y_AXIS);
                }
                else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                }
                else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }
            // default to text display
            return new LabelView(elem);
        }

    }

    class SectionView extends BoxView {
        int pageNumber = 0;

        public SectionView(Element elem, int axis) {
            super(elem, axis);
        }

        public int getPageCount() {
            return pageNumber;
        }

        protected void layout(int width, int height) {
            width = pageWidth - 2 * DRAW_PAGE_INSET - pageMargins.left - pageMargins.right;
            this.setInsets( (short) (DRAW_PAGE_INSET + pageMargins.top), (short) (DRAW_PAGE_INSET + pageMargins.left), (short) (DRAW_PAGE_INSET + pageMargins.bottom),
                           (short) (DRAW_PAGE_INSET + pageMargins.right));
            super.layout(width, height);
        }

        public float getMaximumSpan(int axis) {
            return getPreferredSpan(axis);
        }

        public float getMinimumSpan(int axis) {
            return getPreferredSpan(axis);
        }

        public float getPreferredSpan(int axis) {
            float span = 0;
            if (axis == View.X_AXIS) {
                span = pageWidth;
            }
            else {
                span = pageHeight * getPageCount();
            }
            return span;
        }

        protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
            super.layoutMajorAxis(targetSpan, axis, offsets, spans);
            int totalOffset = 0;
            int n = offsets.length;
            pageNumber = 0;
            for (int i = 0; i < n; i++) {
                offsets[i] = totalOffset;
                View v = getView(i);
                if (v instanceof MultiPageView) {
                    ( (MultiPageView) v).setBreakSpan(0);
                    ( (MultiPageView) v).setAdditionalSpace(0);
                }

                if ( (offsets[i] + spans[i]) > (pageNumber * pageHeight - DRAW_PAGE_INSET * 2 - pageMargins.top - pageMargins.bottom)) {
                    if ( (v instanceof MultiPageView) && (v.getViewCount() > 1)) {
                        MultiPageView multipageView = (MultiPageView) v;
                        int space = offsets[i] - (pageNumber - 1) * pageHeight;
                        int breakSpan = (pageNumber * pageHeight - DRAW_PAGE_INSET * 2 - pageMargins.top - pageMargins.bottom) - offsets[i];
                        multipageView.setBreakSpan(breakSpan);
                        multipageView.setPageOffset(space);
                        multipageView.setStartPageNumber(pageNumber);
                        multipageView.setEndPageNumber(pageNumber);
                        int height = (int) getHeight();

                        int width = ( (BoxView) v).getWidth();
                        if (v instanceof PageableParagraphView) {
                            PageableParagraphView parView = (PageableParagraphView) v;
                            parView.layout(width, height);
                        }

                        pageNumber = multipageView.getEndPageNumber();
                        spans[i] += multipageView.getAdditionalSpace();
                    }
                    else {
                        offsets[i] = pageNumber * pageHeight;
                        pageNumber++;
                    }
                }
                totalOffset = (int) Math.min( (long) offsets[i] + (long) spans[i], Integer.MAX_VALUE);
            }
        }

        public void paint(Graphics g, Shape a) {
            Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
            Shape baseClip = g.getClip().getBounds();
            int pageCount = getPageCount();
            
            Rectangle page = new Rectangle();
            page.x = alloc.x;
            page.y = alloc.y;
            page.height = pageHeight;
            page.width = pageWidth;
            
            String sC = Integer.toString(pageCount);
            
            for (int i = 0; i < pageCount; i++) {
                page.y = alloc.y + pageHeight * i;
                paintPageFrame(g, page, (Rectangle) baseClip);
                g.setColor(Color.blue);
                String sN = Integer.toString(i + 1);
                String pageStr = "Page: " + sN;
                pageStr += " of " + sC;
                g.drawString(pageStr,
                             page.x + page.width - 100,
                             page.y + page.height - 3);
            }
            
            super.paint(g, a);
            
            g.setColor(Color.lightGray);
            // Fills background of pages
            int currentWidth = (int) alloc.getWidth();
            int currentHeight = (int) alloc.getHeight();
            int w = 0;
            int h = 0;
            if (pageWidth < currentWidth) {
                w = currentWidth;
                h = currentHeight;
                g.fillRect(page.x + page.width, alloc.y, w, h);
            }
            
            if (pageHeight * pageCount < currentHeight) {
                w = currentWidth;
                h = currentHeight;
                g.fillRect(page.x, alloc.y + page.height * pageCount, w, h);
            }
        }
        
        public void paintPageFrame(Graphics g, Shape page, Rectangle container) {
            Rectangle alloc = (page instanceof Rectangle) ? (Rectangle) page : page.getBounds();
            
            //frame
            g.setColor(Color.black);
            g.drawLine(alloc.x + DRAW_PAGE_INSET, alloc.y + DRAW_PAGE_INSET, alloc.x + alloc.width - DRAW_PAGE_INSET, alloc.y + DRAW_PAGE_INSET);
            g.drawLine(alloc.x + DRAW_PAGE_INSET, alloc.y + DRAW_PAGE_INSET, alloc.x + DRAW_PAGE_INSET, alloc.y + alloc.height - DRAW_PAGE_INSET);
            g.drawLine(alloc.x + DRAW_PAGE_INSET, alloc.y + alloc.height - DRAW_PAGE_INSET, alloc.x + alloc.width - DRAW_PAGE_INSET, alloc.y + alloc.height - DRAW_PAGE_INSET);
            g.drawLine(alloc.x + alloc.width - DRAW_PAGE_INSET, alloc.y + DRAW_PAGE_INSET, alloc.x + alloc.width - DRAW_PAGE_INSET, alloc.y + alloc.height - DRAW_PAGE_INSET);
            //g.setColor(oldColor);
        }
    }

    class PageableParagraphView extends ParagraphView implements MultiPageView {
        protected int additionalSpace = 0;
        protected int breakSpan = 0;
        protected int pageOffset = 0;
        protected int startPageNumber = 0;
        protected int endPageNumber = 0;

        public PageableParagraphView(Element elem) {
            super(elem);
        }

        public void layout(int width, int height) {
            super.layout(width, height);
        }

        protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
            super.layoutMajorAxis(targetSpan, axis, offsets, spans);
            performMultiPageLayout(targetSpan, axis, offsets, spans);
        }

        public void performMultiPageLayout(int targetSpan, int axis, int[] offsets, int[] spans) {
            if (breakSpan == 0)
                return;
            int space = breakSpan;

            additionalSpace = 0;
            endPageNumber = startPageNumber;
            int topInset = this.getTopInset();
            int offs = 0;
            for (int i = 0; i < offsets.length; i++) {
                if (offs + spans[i] + topInset > space) {
                    int newOffset = endPageNumber * pageHeight;
                    int addSpace = newOffset - (startPageNumber - 1) * pageHeight - pageOffset - offs - topInset;
                    additionalSpace += addSpace;
                    offs += addSpace;
                    for (int j = i; j < offsets.length; j++) {
                        offsets[j] += addSpace;
                    }
                    endPageNumber++;
                    space = (endPageNumber * pageHeight - 2 * DRAW_PAGE_INSET - pageMargins.top - pageMargins.bottom) - (startPageNumber - 1) * pageHeight - pageOffset;
                }
                offs += spans[i];
            }
        }

        public int getStartPageNumber() {
            return startPageNumber;
        }

        public int getEndPageNumber() {
            return endPageNumber;
        }

        public int getAdditionalSpace() {
            return additionalSpace;
        }

        public int getBreakSpan() {
            return breakSpan;
        }

        public int getPageOffset() {
            return pageOffset;
        }

        public void setStartPageNumber(int startPageNumber) {
            this.startPageNumber = startPageNumber;
        }

        public void setEndPageNumber(int endPageNumber) {
            this.endPageNumber = endPageNumber;
        }

        public void setAdditionalSpace(int additionalSpace) {
            this.additionalSpace = additionalSpace;
        }

        public void setBreakSpan(int breakSpan) {
            this.breakSpan = breakSpan;
        }

        public void setPageOffset(int pageOffset) {
            this.pageOffset = pageOffset;
        }
    }
}

/*
 * Copyright 2015 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.util.spreadsheet.html;

import ec.util.spreadsheet.helpers.ArrayBook;
import ec.util.spreadsheet.helpers.ArraySheet;
import java.util.Arrays;
import javax.annotation.Nonnull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Philippe Charles
 */
final class JsoupBookReader {

    private JsoupBookReader() {
        // static class
    }

    @Nonnull
    public static ArrayBook read(@Nonnull Document doc) {
        ArrayBook.Builder bookBuilder = ArrayBook.builder();
        ArraySheet.Builder sheetBuilder = ArraySheet.builder();
        RowSpans rowSpans = new RowSpans();

        int sheetIndex = 0;
        for (Element table : doc.getElementsByTag("table")) {
            String name = getName(table);
            sheetBuilder.name(name != null ? name : ("Sheet " + sheetIndex));
            int i = 0;
            for (Element row : table.getElementsByTag("tr")) {
                if (!row.parent().tagName().equals("tfoot")) {
                    int j = 0;
                    for (Element cell : row.children().select("td, th")) {
                        while (rowSpans.decrease(j)) {
                            j++;
                        }
                        String cellValue = cell.text();
                        if (!cellValue.isEmpty()) {
                            sheetBuilder.value(i, j, cellValue);
                        }
                        rowSpans.increase(j, parseSpan(cell.attr("rowspan")));
                        j += parseSpan(cell.attr("colspan"));
                    }
                    i++;
                }
            }
            bookBuilder.sheet(sheetBuilder.build());
            sheetBuilder.clear();
            rowSpans.clear();
            sheetIndex++;
        }

        return bookBuilder.build();
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static String getName(Element table) {
        if (table.childNodeSize() > 0) {
            Element first = table.child(0);
            if (first.tagName().equals("caption")) {
                String result = first.text();
                if (result != null && !result.isEmpty()) {
                    return result;
                }
            }
        }
        return null;
    }

    private static int parseSpan(String value) {
        try {
            int result = Integer.parseInt(value);
            return result > 0 ? result : 1;
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private static final class RowSpans {

        private int[] data = new int[0];

        private void checkSize(int columnIndex) {
            if (data.length < columnIndex + 1) {
                int[] old = data;
                data = new int[columnIndex + 1];
                System.arraycopy(old, 0, data, 0, old.length);
            }
        }

        public void increase(int columnIndex, int count) {
            if (count > 1) {
                checkSize(columnIndex);
                data[columnIndex] += (count - 1);
            }
        }

        public boolean decrease(int columnIndex) {
            if (data.length < columnIndex + 1) {
                return false;
            }
            int value = data[columnIndex];
            if (value > 0) {
                data[columnIndex] = value - 1;
                return true;
            }
            return false;
        }

        public void clear() {
            Arrays.fill(data, 0);
        }
    }
    //</editor-fold>
}

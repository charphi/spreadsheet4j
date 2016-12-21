/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package ec.util.spreadsheet.helpers;

import static ec.util.spreadsheet.helpers.CellRefHelper.getCellRef;
import static ec.util.spreadsheet.helpers.CellRefHelper.getColumnIndex;
import static ec.util.spreadsheet.helpers.CellRefHelper.getColumnLabel;
import static ec.util.spreadsheet.helpers.CellRefHelper.getRowLabel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class CellRefHelperTest {

    @Test
    public void testGetCellRef() {
        assertThat(getCellRef(0, 0)).isEqualTo("A1");
        assertThat(getCellRef(0, 1)).isEqualTo("B1");
        assertThat(getCellRef(1, 0)).isEqualTo("A2");
        assertThat(getCellRef(0, 26)).isEqualTo("AA1");
        assertThatThrownBy(() -> getCellRef(-1, 0)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> getCellRef(0, -1)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void testGetColumnIndex() {
        assertThat(getColumnIndex("A")).isEqualTo(0);
        assertThat(getColumnIndex("Z")).isEqualTo(25);
        assertThat(getColumnIndex("AA")).isEqualTo(26);
        assertThat(getColumnIndex("AB")).isEqualTo(27);
        assertThat(getColumnIndex("BA")).isEqualTo(52);
    }

    @Test
    public void testGetColumnLabel() {
        assertThat(getColumnLabel(0)).isEqualTo("A");
        assertThat(getColumnLabel(26)).isEqualTo("AA");
        assertThatThrownBy(() -> getColumnLabel(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void testGetRowLabel() {
        assertThat(getRowLabel(0)).isEqualTo("1");
        assertThat(getRowLabel(1)).isEqualTo("2");
        assertThatThrownBy(() -> getRowLabel(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void testParse() {
        CellRefHelper cellRef = new CellRefHelper();
        assertThatThrownBy(() -> cellRef.getColumnIndex()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> cellRef.getRowIndex()).isInstanceOf(IllegalStateException.class);

        assertThat(cellRef.parse("A1")).isTrue();
        assertThat(cellRef.getColumnIndex()).isEqualTo(0);
        assertThat(cellRef.getRowIndex()).isEqualTo(0);

        assertThat(cellRef.parse("A2")).isTrue();
        assertThat(cellRef.getColumnIndex()).isEqualTo(0);
        assertThat(cellRef.getRowIndex()).isEqualTo(1);

        assertThat(cellRef.parse("B1")).isTrue();
        assertThat(cellRef.getColumnIndex()).isEqualTo(1);
        assertThat(cellRef.getRowIndex()).isEqualTo(0);

        assertThat(cellRef.parse("hello")).isFalse();
        assertThatThrownBy(() -> cellRef.getColumnIndex()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> cellRef.getRowIndex()).isInstanceOf(IllegalStateException.class);
    }
}

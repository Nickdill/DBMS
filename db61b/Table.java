package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author Nick Dill and P. N. Hilfinger
 */
class Table implements Iterable<Row> {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain dupliace names. */
    Table(String[] columnTitles) {
        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        _columnTitles = columnTitles;
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _columnTitles.length;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return _columnTitles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < this.columns(); i++) {
            if (this.getTitle(i).equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of Rows in this table. */
    public int size() {
        return _rows.size();
    }

    /** Returns an iterator that returns my rows in an unspecfied order. */
    @Override
    public Iterator<Row> iterator() {
        return _rows.iterator();
    }

    /** Add ROW to THIS if no equal row already exists.  Return true if anything
     *  was added, false otherwise. */
    public boolean add(Row row) {
        Boolean bool = true;
        for (Row r : this) {
            for (int i = 0; i < r.size(); i++) {
                if (!r.get(i).equals(row.get(i))) {
                    break;
                }
                if (i == r.size() - 1) {
                    bool = false;
                }
            }
        }
        if (bool) {
            _rows.add(row);
        }
        return bool;
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            String line;
            while ((line = input.readLine()) != null) {
                Row r = new Row(line.split(","));
                table.add(r);
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            for (int i = 0; i < _columnTitles.length - 1; i++) {
                output.append(_columnTitles[i]).append(",");
            }
            output.append(_columnTitles[_columnTitles.length - 1])
                .append('\n');
            for (Row r : this) {
                for (int j = 0; j < r.size() - 1; j++) {
                    output.append(r.get(j)).append(",");
                }
                output.append(r.get(r.size() - 1));
                output.append('\n');
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output. */
    void print() {
        Iterator<Row> itr = iterator();
        while (itr.hasNext()) {
            System.out.print(" ");
            Row tmpRow = itr.next();
            for (int i = 0; i < tmpRow.size(); i++) {
                System.out.print(" " + tmpRow.get(i));
            }
            System.out.println("");
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        List<Column> columnList = new ArrayList<Column>();
        Iterator<Row> itr = iterator();
        Boolean bool = true;
        for (String s : columnNames) {
            columnList.add(new Column(s, this));
        }
        while (itr.hasNext()) {
            Row tmpRow = itr.next();
            if (conditions.size() > 0) {
                bool = true;
                for (Condition c : conditions) {
                    if (!c.test(tmpRow)) {
                        bool = false;
                    }
                }
                if (bool) {
                    result.add(new Row(columnList, tmpRow));
                }
            } else {
                result.add(new Row(columnList, tmpRow));
            }
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames); Table tmpTable = result;
        List<Column> columnList = new ArrayList<Column>();
        if (this.size() > 0) {
            tmpTable = this;
        } else if (table2.size() > 0) {
            tmpTable = table2;
        }
        if (this.size() > 0 && table2.size() > 0) {
            for (String s : columnNames) {
                columnList.add(new Column(s, this, table2));
            }
            for (Row r : this) {
                for (Row r2 : table2) {
                    if (colCheck(this, table2)) {
                        if (doubleCheck(r, r2)) {
                            if (conditions.size() > 0) {
                                if (Condition.test(conditions, r, r2)) {
                                    result.add(new Row(columnList, r, r2));
                                }
                            } else {
                                result.add(new Row(columnList, r, r2));
                            }
                        }
                    } else {
                        if (conditions.size() == 0) {
                            result.add(new Row(columnList, r, r2));
                        } else if (Condition.test(conditions, r, r2)) {
                            result.add(new Row(columnList, r, r2));
                        }
                    }
                }
            }
        } else {
            for (String s : columnNames) {
                columnList.add(new Column(s, tmpTable));
            }
            Row r = new Row(new String[]{});
            for (Row r2 : tmpTable) {
                if (colCheck(this, table2)) {
                    if (conditions.size() > 0) {
                        if (Condition.test(conditions, r2)) {
                            result.add(new Row(columnList, r2));
                        }
                    } else {
                        result.add(new Row(columnList, r2));
                    }
                } else {
                    if (conditions.size() > 0) {
                        if (Condition.test(conditions, r, r2)) {
                            result.add(new Row(columnList, r2));
                        }
                    } else {
                        result.add(new Row(columnList, r2));
                    }
                }
            }
        }
        return result;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 come, respectively,
     *  from those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    Row row1, Row row2) {
        Boolean bool = true;
        return bool;
    }

    /** Checks Table T and Table T2 to see if they have a similar column.
     *  If true returns true, it will set match and match2 to the column number
     *  of that column for each row. */
    private boolean colCheck(Table t, Table t2) {
        for (int i = 0; i < t.columns(); i++) {
            for (int j = 0; j < t2.columns(); j++) {
                if (t.getTitle(i).equals(t2.getTitle(j))) {
                    tmp = new int[match.length + 1];
                    tmp[tmp.length - 1] = i;
                    for (int x = 0; x < tmp.length - 1; x++) {
                        tmp[x] = match[x];
                    }
                    match = tmp;
                    tmp2 = new int[match2.length + 1];
                    tmp2[tmp.length - 1] = j;
                    for (int x = 0; x < tmp2.length - 1; x++) {
                        tmp2[x] = match2[x];
                    }
                    match2 = tmp2;
                }
            }
        }
        if (match.length > 0) {
            return true;
        }
        return false;
    }

    /** Double checks to that every matching column (R and R2) that was stored
     * in match and match2 does indeed match for every row comparison.
     *  If it does, return true else return false and don't add the row to
     *  the RESULT table of select(). */
    private boolean doubleCheck(Row r, Row r2) {
        for (int i = 0; i < match.length; i++) {
            if (!r2.get(match2[i]).equals(r.get(match[i]))) {
                return false;
            }
        }
        return true;
    }

    /** My rows. */
    private HashSet<Row> _rows = new HashSet<>();

    /** Contents of this row. */
    private String[] _columnTitles;

    /** A new joined row created by 2 rows. */
    private Row joined;

    /** Variable to store a similar column in the 1st row. */
    private int[] match = new int[0];

    /** Variable to store what column of the 2nd row is similar to the first. */
    private int[] match2 = new int[0];

    /** Variable temporarily increase the size of match arrays. */
    private int[] tmp = new int[1];

    /** Variable temporarily increase the size of match2 arrays. */
    private int[] tmp2 = new int[1];


}

package db61b;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

/** This class uses JUnit tests
 *  to test the functionality of the db61b package.
 *  @author Nick Dill */
public class BasicTests {

    /** Tests the Row class. */
    @Test
    public void testRow() {
        Row r = new Row(new String[]{"Nick", "Is", "Cool."});
        Row e = r;
        Row y = new Row(new String[]{"Nick", "Is", "Not", "Cool."});
        assertEquals(3, r.size());
        assertEquals("Is", r.get(1));
        assertEquals(true, e.equals(r));
        assertEquals(false, e.equals(y));
    }

    /** Tests the table class. */
    @Test
    public void testTable() {
        Table t = new Table(new String[]{"Name", "Age", "Gender"});
        assertEquals(3, t.columns());
        assertEquals("Age", t.getTitle(1));
        assertEquals(1, t.findColumn("Age"));
        assertEquals(-1, t.findColumn("Ages"));
        Row r = new Row(new String[]{"Nick", "18", "Male"});
        Row r2 = new Row(new String[]{"Lexi", "16", "Female"});
        assertEquals(true, t.add(r));
        assertEquals(1, t.size());
        t.add(r2);
        assertEquals(2, t.size());
        assertEquals(false, t.add(r));
        assertEquals(2, t.size());
        Table a = Table.readTable("TEST");
    }

    /** Tests the database class. */
    @Test
    public void testDatabase() {
        Database d = new Database();
        Table t = new Table(new String[]{"Name", "Age", "Gender"});
        Table g = new Table(new String[]{"Points", "Wins", "Losses"});
        d.put("people", t);
        assertEquals(t, d.get("people"));
        assertEquals(null, d.get("stats"));
        d.put("stats", g);
        assertEquals(g, d.get("stats"));
    }

    /** Test everything in phase 1 and basic Column commands. */
    @Test
    public void testPhase1() {
        Table t = new Table(new String[]{"Name", "Age", "Gender"});
        Row r2 = new Row(new String[]{"Nick", "18", "Male"});
        Row r = new Row(new String[]{"Lexi", "16", "Female"});
        t.add(r);
        t.add(r2);
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("Name", t));
        columns.add(new Column("Age", t));
        Table que = new Table(new String[]{"Name", "Age"});
        que.add(new Row(columns, new Row[]{r, r2}));
        que.add(new Row(columns, new Row[]{r2}));
    }

    /** This will run the JUnit tests. Has parameter args. */
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(BasicTests.class));
    }



}

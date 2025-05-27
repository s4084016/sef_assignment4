package person;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;


public class PersonTest {

    private final String validID = "56s_d%&fAB";
    private final String validAddress = "12|Main Street|Melbourne|Victoria|Australia";
    private final String validDOB = "01-01-2000";

    @BeforeEach
    public void setup() throws IOException {
        new FileWriter("people.txt", false).close();
    }

    // addPerson Tests
    @Test
    public void testAddPersonSuccess() {
        Person p = new Person(validID, "Alice", "Smith", validAddress, validDOB);
        assertTrue(p.addPerson());
    }

    @Test
    public void testAddPersonInvalidID() {
        Person p = new Person("12INVALID", "Alice", "Smith", validAddress, validDOB);
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPersonInvalidAddress() {
        Person p = new Person(validID, "Alice", "Smith", "123|Street|Mel|NSW|Australia", validDOB);
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPersonInvalidDOB() {
        Person p = new Person(validID, "Alice", "Smith", validAddress, "1990-December-01");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPersonWriteFile() {
        Person p = new Person(validID, "Test", "User", validAddress, validDOB);
        p.addPerson();
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("people.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Test|User")) found = true;
            }
        } catch (IOException e) { fail("File read error"); }
        assertTrue(found);
    }

    // updatePersonalDetails Tests
    @Test
    public void testUpdateSuccess() {
        Person p = new Person(validID, "Alice", "Smith", validAddress, validDOB);
        p.addPerson();
        assertTrue(p.updatePersonalDetails(validID, "Bob", "Jones", validAddress, validDOB));
    }

    @Test
    public void testUpdateUnder18ChangeAddress() {
        String dob = "01-01-2010";
        Person p = new Person(validID, "Young", "One", validAddress, dob);
        p.addPerson();
        assertFalse(p.updatePersonalDetails(validID, "Young", "One", "13|Another|Melbourne|Victoria|Australia", dob));
    }

    @Test
    public void testUpdateChangeDOBWithOtherChanges() {
        Person p = new Person(validID, "Alice", "Smith", validAddress, validDOB);
        p.addPerson();
        assertFalse(p.updatePersonalDetails("56!@#$%^&CD", "Bob", "Jones", validAddress, "02-02-2002"));
    }

    @Test
    public void testUpdateEvenFirstDigitID() {
        Person p = new Person("24!@#$%^&XY", "Even", "ID", validAddress, validDOB);
        p.addPerson();
        assertFalse(p.updatePersonalDetails("22!@#$%^&XY", "Even", "ID", validAddress, validDOB));
    }

    @Test
    public void testUpdateWriteFile() {
        Person p = new Person(validID, "Alpha", "Beta", validAddress, validDOB);
        p.addPerson();
        p.updatePersonalDetails(validID, "Gamma", "Delta", validAddress, validDOB);
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("people.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Gamma|Delta")) found = true;
            }
        } catch (IOException e) { fail("File read error"); }
        assertTrue(found);
    }

    // addDemeritPoints Tests
    @Test
    public void testAddDemeritSuccess() {
        Person p = new Person(validID, "Test", "User", validAddress, validDOB);
        p.addPerson();
        assertEquals("Success", p.addDemeritPoints("01-05-2023", 3));
    }

    @Test
    public void testAddDemeritInvalidDate() {
        Person p = new Person(validID, "Test", "User", validAddress, validDOB);
        p.addPerson();
        assertEquals("Failed", p.addDemeritPoints("2023/01/01", 3));
    }

    @Test
    public void testAddDemeritValidDate() {
        Person p = new Person(validID, "Test", "User", validAddress, validDOB);
        p.addPerson();
        assertEquals("Success", p.addDemeritPoints("05-02-2020", 5));
    }

    @Test
    public void testAddDemeritInvalidPointsLow() {
        Person p = new Person(validID, "Test", "User", validAddress, validDOB);
        p.addPerson();
        assertEquals("Failed", p.addDemeritPoints("01-01-2023", 0));
    }

    @Test
    public void testAddDemeritValidPoints() {
        Person p = new Person(validID, "Test", "User", validAddress, validDOB);
        p.addPerson();
        assertEquals("Success", p.addDemeritPoints("01-01-2023", 3));
    }

    @Test
    public void testAddDemeritInvalidPointsHigh() {
        Person p = new Person(validID, "Test", "User", validAddress, validDOB);
        p.addPerson();
        assertEquals("Failed", p.addDemeritPoints("01-01-2023", 7));
    }

    @Test
    public void testSuspensionStatusUnder21() {
        String dob = "01-01-2007"; // under 21
        Person p = new Person(validID, "Test", "User", validAddress, dob);
        p.addPerson();
        p.addDemeritPoints("01-01-2024", 3);
        p.addDemeritPoints("02-01-2024", 1);
        p.addDemeritPoints("04-05-2024", 4);
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("people.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("suspended=true") && line.contains(validID)) found = true;
            }
        } catch (IOException e) { fail("File read error"); }
        assertTrue(found);
    }
}

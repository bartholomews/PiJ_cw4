import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 *
 */
public class TestContactManager {
    private ContactManager test;

    @Before
    public void setUp() {
        test = new ContactManagerImpl();
    }

    @After
    public void tearDown() {
        test = null;
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    // testing addNewContact() and getContacts()   -----------------------------------------------------

    public void addContacts(int n) {
        for (int i = 1; i <=n; i++) {
            test.addNewContact("Contact" + i, "Notes about Contact" + i);
        }
    }

    public void addContactsSmith(int n) {
        for (int i = 1; i<=n; i++) {
            test.addNewContact("Smith", "Notes about Smith" + "(" + i + ")");
        }
    }

    @Test
    public void testAddNewContactWithNullNameShouldThrowNullPointerException() {
        exception.expect(NullPointerException.class);
        test.addNewContact(null, "Some notes");
    }

    @Test
    public void testAddNewContactWithNullNotesShouldThrowNullPointerException() {
        exception.expect(NullPointerException.class);
        test.addNewContact("Contact1", null);
    }

    @Test
    public void testAddNewContactGetContactByName() {
        test.addNewContact("Contact1", "Notes about Contact1");
        Contact first = (Contact)test.getContacts("Contact1").toArray()[0];
        assertEquals(first.getName(), "Contact1");
    }

    @Test
    public void testAddNewContactsShouldGetTheSameOneBothByNameAndId() {
        addContacts(10);
        Set<Contact> byNameshouldBeSize1 = test.getContacts("Contact1");
        assertEquals(byNameshouldBeSize1.size(), 2);
        Contact first = (Contact)byNameshouldBeSize1.toArray()[0];
        Set<Contact> byIDshouldBeSize1 = test.getContacts(first.getId());
        Contact firstcopy = (Contact)byIDshouldBeSize1.toArray()[0];
        assertEquals(first, firstcopy);
    }

    @Test
    public void testAddTwoNewContactsShouldIssueTwoDifferentIds() {
        test.addNewContact("Contact1", "Notes about Contact1");
        test.addNewContact("Contact2", "Notes about Contact2");
        int id1 = ((Contact)test.getContacts("Contact1").toArray()[0]).getId();
        int id2 = ((Contact)test.getContacts("Contact2").toArray()[0]).getId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testAdd10ContactsWhoseNameContainsSameStringGetByName() {
        addContacts(10);
        Set<Contact> list = test.getContacts("Contact");
        assertEquals(list.size(), 10);
        for(Contact contact : list) {
            assertTrue(contact.getName().contains("Contact"));
        }
    }

    @Test
    public void testAdd10ContactsWhoseNameIsSameStringPlusOtherThatDoesntGetByName() {
        addContacts(5);
        addContactsSmith(10);
        Set<Contact> list = test.getContacts("Smith");
        assertEquals(list.size(), 10);
        for(Contact contact : list) {
            assertTrue(contact.getName().equals("Smith"));
        }
    }

    @Test
    public void testAddMoreNewContactsWhoseNameContainsSameStringGetByNameCheckAllIDsAreUnique() {
        addContacts(10);
        addContacts(10);
        Set<Contact> list = test.getContacts("Contact");
        assertEquals(list.size(), 20);
        Contact[] contacts = test.getContacts("Contact").toArray(new Contact[list.size()]);
        for(int i=0; i<contacts.length; i++) {
            for(int j=i+1; j<contacts.length; j++) {
                assertNotEquals(contacts[i].getId(), contacts[j].getId());
            }
        }
    }

    @Test
    public void testAddMoreContactsWithSomeDifferentSomeSameNameMergeSetsAndCheckAllIDsAreUnique() {
        addContacts(5);
        addContactsSmith(5);
        Set<Contact> different = test.getContacts("Contact");
        Set<Contact> same = test.getContacts("Smith");
        Set<Contact> merged = new HashSet<Contact>();
        merged.addAll(same);
        merged.addAll(different);
        Contact[] mergedArray = merged.toArray(new Contact[merged.size()]);
        for (int i = 0; i < mergedArray.length; i++) {
            for (int j = 0; i < mergedArray.length; i++) {
                if (i == j) {
                    continue;
                }
                assertTrue(mergedArray[i] != mergedArray[j]);
            }
        }
    }

    @Test
    public void testAddMoreNewContactsWhoseNameIsSameStringGetByNameMergeSetsAndCheckAllIDsAreUnique() {
        addContactsSmith(10);
        Set<Contact> list1 = test.getContacts("Smith");
        addContactsSmith(5);
        Set<Contact> list2 = test.getContacts("Smith");
        Set<Contact> merged = new HashSet<Contact>();
        merged.addAll(list1);
        merged.addAll(list2);
        Contact[] arrayList = merged.toArray(new Contact[merged.size()]);
        for(int i=0; i<arrayList.length; i++) {
            for(int j=0; j<arrayList.length; j++) {
                if(i==j) {
                    continue;
                }
                assertNotEquals(arrayList[i].getId(), arrayList[j].getId());
            }
        }
    }

    @Test
    public void testAddMoreNewContactsGetAllContactsByNameAndByArrayOfIDsCompareSetsSize() {
        addContacts(5);
        Set<Contact> listByName = test.getContacts("Contact");
        int[] IDs = new int[listByName.size()];
        int i = 0;
        for(Contact c : listByName) {
            IDs[i] = c.getId();
            i++;
        }
        Set<Contact> listByIDs = test.getContacts(IDs);
        assertEquals(listByIDs.size(), listByName.size());
    }

    @Test
    public void testAddOneNewContactGetContactByIdOneValidOneNotCorresponding() {
        exception.expect(IllegalArgumentException.class);
        addContacts(1);
        Set<Contact> list = test.getContacts("Contact1");
        assertEquals(list.size(), 1);
        Contact first = (Contact)list.toArray()[0];
        int validID = first.getId();
        test.getContacts(validID, 1231);
    }

    @Test
    public void testGetContactEmptyStringShouldThrowNullPointerException() {
        exception.expect(NullPointerException.class);
        test.getContacts("");
    }

    @Test
    public void testGetContactByIdOnEmptyListShouldThrowIllegalArgumentException() {
        exception.expect(IllegalArgumentException.class);
        test.getContacts(100);
    }

    @Test
    public void testGetContactNullShouldThrowNullPointerException() {
        exception.expect(NullPointerException.class);
        String nullString = null;
        test.getContacts(nullString);
    }

    @Test
    public void testGetContactNameNotInTheListShouldReturnAnEmptySet() {
        Set<Contact> empty = test.getContacts("Name not in the list");
        assertEquals(empty.size(), 0);
    }

    // testing addFutureMeeting(), getFutureMeeting(), getMeeting() ------------------------------------

    public Set<Contact> createUnknownSetOfContacts(int n) {
        Set<Contact> contacts = new HashSet<Contact>();
        while(n>0) {
            Contact contact = new ContactImpl("Contact" + n, n);
            contacts.add(contact);
            n--;
        }
        return contacts;
    }

    public Calendar getFutureDate() {
        Calendar date = new GregorianCalendar(3015, 12, 12, 12, 12);
        return date;
    }

    public Calendar getPastDate() {
        Calendar date = new GregorianCalendar(2001, 12, 12, 12, 12);
        return date;
    }

    @Test
    public void addFutureMeetingWithNullContactsShouldThrowNullPointerException() {
        exception.expect(NullPointerException.class);
        test.addFutureMeeting(null, getFutureDate());
    }

    @Test
    public void addFutureMeetingWithNullDateShouldThrowNullPointerException() {
        exception.expect(NullPointerException.class);
        test.addNewContact("Valid", "Notes");
        test.addFutureMeeting(test.getContacts("Valid"), null);
    }

    @Test
    public void addFutureMeetingWithUnknownContactsShouldThrowIllegalArgumentException() {
        exception.expect(IllegalArgumentException.class);
        test.addFutureMeeting(createUnknownSetOfContacts(1), getFutureDate());
    }

    @Test
    public void addFutureMeetingWithMatchingContactNotRetrievedFromGetContactsSHOULDDOWHAT() {
        // TODO
    }

    @Test
    public void addFutureMeetingWithSomeKnownSomeUnknownContactsShouldThrowIllegalArgumentException() {
        exception.expect(IllegalArgumentException.class);
        Set<Contact> mixed = new HashSet<Contact>();
        test.addNewContact("Valid", "Notes");
        Set<Contact> valid = test.getContacts("Valid");
        Contact validContact = valid.toArray(new Contact[valid.size()])[0];
        Contact invalidContact = new ContactImpl("Not Valid", 100, "Notes");
        mixed.add(validContact);
        mixed.add(invalidContact);
        test.addFutureMeeting(mixed, getFutureDate());
    }

    @Test
    public void addFutureMeetingWithPastDateShouldThrowIllegalArgumentException() {
        exception.expect(IllegalArgumentException.class);
        test.addNewContact("Valid", "Notes");
        test.addFutureMeeting(test.getContacts("Valid"), getPastDate());
    }

    @Test
    public void addFutureMeetingGetFutureMeetingCheckIfTheIdCorresponds() {
        test.addNewContact("Valid", "Notes");
        int thisID = test.addFutureMeeting(test.getContacts("Valid"), getFutureDate());
        FutureMeeting thisMeeting = test.getFutureMeeting(thisID);
        assertEquals(thisID, thisMeeting.getId());
    }

    @Test
    public void addFutureMeetingGetMeetingCheckIfTheIdCorresponds() {
        test.addNewContact("Valid", "Notes");
        int thisID = test.addFutureMeeting(test.getContacts("Valid"), getFutureDate());
        Meeting thisMeeting = test.getMeeting(thisID);
        assertEquals(thisID, thisMeeting.getId());
    }

    @Test
    public void addFutureMeetingGetMeetingCompareContacts() {
        test.addNewContact("Valid", "Notes");
        Set<Contact> set1 = test.getContacts("Valid");
        Contact contact1 = set1.toArray(new Contact[set1.size()])[0];
        int thisID = test.addFutureMeeting(set1, getFutureDate());
        Meeting thisMeeting = test.getMeeting(thisID);
        Set<Contact> set2 = thisMeeting.getContacts();
        Contact contact2 = set2.toArray(new Contact[set2.size()])[0];
        assertEquals(contact1, contact2);
    }

    @Test
    public void addFutureMeetingGetFutureMeetingCompareSetsOfContacts() {
        test.addNewContact("Valid1", "Notes1");
        test.addNewContact("Valid2", "Notes2");
        Set<Contact> set1 = test.getContacts("Valid");
        int thisID = test.addFutureMeeting(set1, getFutureDate());
        FutureMeeting thisMeeting = test.getFutureMeeting(thisID);
        Set<Contact> set2 = thisMeeting.getContacts();
        Contact[] first = set1.toArray(new Contact[set1.size()]);
        Contact[] second = set2.toArray(new Contact[set2.size()]);
        assertThat(first[0], Matchers.either(Matchers.is(second[0])).or(Matchers.is(second[1])));
        assertThat(first[1], Matchers.either(Matchers.is(second[0])).or(Matchers.is(second[1])));
    }

    @Test
    public void getMeetingNonExistentIdShouldReturnNull() {
        assertNull(test.getMeeting(100));
    }

    @Test
    public void getFutureMeetingWithNonExistentIdShouldReturnNull() {
        assertNull(test.getFutureMeeting(100));
    }

    @Test
    public void getFutureMeetingWithPastMeetingIdShouldThrowIllegalArgumentException() {
        // TODO
    }




}

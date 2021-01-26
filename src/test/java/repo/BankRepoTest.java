package repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

public class BankRepoTest {


    @BeforeClass
    public static void beforeClass() {
        System.setProperty("dataset", "small");
    }

    @Test
    public void readAllSmallDataset() {
        BankRepo repo = new BankRepo();
        assertNotNull(repo.getAll());
        assertEquals(2, repo.getAll().size());
    }

    @Test
    public void readAllLargeDataset() {
        System.setProperty("dataset", "large");
        BankRepo repo = new BankRepo();
        assertNotNull(repo.getAll());
        assertEquals(5, repo.getAll().size());
    }
}

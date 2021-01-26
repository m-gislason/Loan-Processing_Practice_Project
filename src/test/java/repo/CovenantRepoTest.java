package repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import model.Restriction;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CovenantRepoTest {

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("dataset", "small");
    }

    @Before
    public void before() {
        System.setProperty("dataset", "small");
    }

    @Test
    public void readAllSmallDataset() {
        CovenantRepo repo = new CovenantRepo();
        assertNotNull(repo.getAll());
        assertEquals(3, repo.getAll().size());
    }

    @Test
    public void readAllLargeDataset() {
        System.setProperty("dataset", "large");
        CovenantRepo repo = new CovenantRepo();
        assertNotNull(repo.getAll());
        assertEquals(36, repo.getAll().size());
    }

    @Test
    public void getAllCovenantsForFacility() {
        System.setProperty("dataset", "large");
        CovenantRepo repo = new CovenantRepo();
        Restriction restriction = repo.getFacilityRestriction(5, 1);
        System.out.println(restriction);
//        assertEquals(2, covs.size());
    }
}

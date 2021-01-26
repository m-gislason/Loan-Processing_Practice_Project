package repo;

import java.util.Comparator;
import java.util.List;
import model.Facility;

public class FacilityRepo extends AbstractRepo<Facility> {

    private static final String file = "facilities.csv";

    public FacilityRepo() {
        super();
    }

    public List<Facility> getAll() {
        return super.getAll(Facility.class, file, new Comparator<Facility>() {
            @Override
            public int compare(Facility o1, Facility o2) {
                return o1.getInterestRate().compareTo(o2.getInterestRate());
            }
        });
    }
}

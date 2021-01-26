package repo;

import java.util.List;
import lombok.SneakyThrows;
import model.Loan;

public class LoanRepo extends AbstractRepo<Loan> {

    private static final String file = "loans.csv";

    public LoanRepo() {
        super();
    }

    @SneakyThrows
    public List<Loan> getAll() {
        return super.getAll(Loan.class, file);
    }


}

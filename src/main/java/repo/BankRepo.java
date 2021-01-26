package repo;

import java.util.List;
import lombok.SneakyThrows;
import model.Bank;

public class BankRepo extends AbstractRepo<Bank> {

    private static final String file = "banks.csv";

    public BankRepo() {
        super();
        // getAll(Bank.class, file);
    }


    @SneakyThrows
    public List<Bank> getAll() {
        return super.getAll(Bank.class, file);
    }

}

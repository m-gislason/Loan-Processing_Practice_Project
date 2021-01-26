import java.util.List;

import lombok.extern.slf4j.Slf4j;

import model.Facility;
import model.Loan;
import repo.LoanRepo;

@Slf4j
public class Application {

    public static void main(String[] args) {
        log.info("Starting application, creating loan processor");

        //Get the list of loans to process
        LoanRepo loanRepo = new LoanRepo();
        List<Loan> loans = loanRepo.getAll();

        LoanProcessor processor = new LoanProcessor();

        //For each loan, check if it can be assigned to a facility, and if so write to file
        loans.stream().forEach(loan -> {
            Facility f = processor.process(loan);
            //if a loan can't be assigned, the LoanProcessor returns null & doesn't write anything to the assignmentsFile
            if (f == null) {
                log.info("Loan {} could not be assigned, no assignment written to file", loan);
            } else {
                log.info("Loan {} assigned to facility {}", loan, f);
            }
        });

        //Write the each facility's yield to file
        processor.persistFacilityYields();
    }

}

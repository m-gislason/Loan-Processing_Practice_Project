import com.google.common.base.Joiner;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;
import lombok.NoArgsConstructor;

import model.Facility;
import repo.FacilityRepo;
import model.Loan;
import model.Restriction;
import repo.CovenantRepo;

@Slf4j
@NoArgsConstructor
public class LoanProcessor {
    private FacilityRepo facilityRepo = new FacilityRepo();
    private CovenantRepo covenantRepo = new CovenantRepo();

    private File assignmentsFile = new File("myOutput/myAssignments.csv");;
    private File yieldsFile = new File("myOutput/myYields.csv");

    //file initialization block
    {
        try {
            initFiles(assignmentsFile, yieldsFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot initialize yieldsFile & assignmentsFile in LoanProcessor", e);
        }
    }

    /**
    * For all facilities in the LoanProcessor's facilityRepo, write "facility_id,expected_yield" to yieldsFile (CSV format)
     * example (if facilityRepo only contains one facility, then file after write operation looks like):
     *          facility_id,expected_yield
     *          1,          323890.25
     */
    public void persistFacilityYields() {
        for (Facility facility: facilityRepo.getAll()) {
            try {
                String yieldData = String.format("%d,%.2f\n", facility.getFacilityId(), facility.getYield());
                Files.append(yieldData, yieldsFile, Charset.defaultCharset());

            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Cannot write to yieldsFile in LoanProcessor", e);
            }
        }

        log.info(String.format("Facility yields were successfully persisted to %s", yieldsFile));
    }

    /**
     * Given a loan, for each facility figure out the restrictions to apply
     * and then see if the loan can be applied, if so then assign and update the facility's capacity(amount) and yield
     *
     * @param loan object to be processed
     * @return facility object (in facilityRepo) to which the loan is allocated. If no valid facility exists, returns null
     */
    public Facility process(Loan loan) {
        //local variables
        Facility facility;
        float defaultLikelihood;
        float expectedYield;

        //iterate through facilities to (hopefully) get a match
        Iterator<Facility> facilityIterator = facilityRepo.getAll().iterator();
        while (facilityIterator.hasNext()) {
            facility = facilityIterator.next();

            if (loanMeetsFacilityCriteria(loan, facility)) {
                defaultLikelihood = loan.getDefaultLikelihood();

                //calculate expected yield of the loan (formula taken from READme)
                expectedYield = (1 - defaultLikelihood) * loan.getInterestRate() * loan.getAmount()
                        - (defaultLikelihood * loan.getAmount())
                        - (loan.getAmount() * facility.getInterestRate());

                //if expected yield is negative, the loan is unprofitable and shouldn't be assigned to this facility
                //otherwise, assign loan to this facility w/side effect of updating facility's capacity(amount) and yield
                if (expectedYield >= 0) {
                    assignLoan(loan, expectedYield, facility);
                    return facility;
                }
            }
        }
        //if no facility can accommodate the loan, return null
        return null;
    }

    private boolean loanMeetsFacilityCriteria(Loan loan, Facility facility) {
        //get Restriction object for current Facility
        int facilityId = facility.getFacilityId();
        Restriction activeRestriction = covenantRepo.getFacilityRestriction(facilityId, facility.getBankId());

        //we must check that the loan's amount, state, and default_likelihood are all valid for this facility
        if (!facilityAmountIsExhaustedForTheLoan(loan.getAmount(),facility.getAmount())
                && !loanStateIsBanned(loan.getState(), activeRestriction)
                && loanDefaultLikelihoodSatisfied(loan.getDefaultLikelihood(), activeRestriction)) {
            return true;
        } else {
            return false;
        }

    }

    private void assignLoan(Loan loan, float expectedYield, Facility facility) {
        facility.setAmount(facility.getAmount() - loan.getAmount());
        facility.setYield(facility.getYield() + expectedYield);
        try {
            String assignmentData = Joiner.on(",").useForNull("").join(loan.getId(), facility.getFacilityId()) + System.lineSeparator();
            Files.append(assignmentData, assignmentsFile, Charset.defaultCharset());

        } catch (IOException e) {
            throw new IllegalStateException("Cannot write to assignmentsFile", e);
        }

    }

    private boolean facilityAmountIsExhaustedForTheLoan(Integer loanAmount, Integer facilityMaxAmount) {
        return facilityMaxAmount - loanAmount < 0 ? true : false;
    }

    private boolean loanStateIsBanned(String loanState, Restriction restriction) {
        if (StringUtils.isEmpty(loanState)) {
            return false;
        }

        return restriction.getBannedStates().contains(loanState);
    }

    private boolean loanDefaultLikelihoodSatisfied(float loanDefaultLikelihood, Restriction restriction) {
        if (restriction.getMaxDefaultLikelyhood() != null && restriction.getMaxDefaultLikelyhood() >= loanDefaultLikelihood) {
            return true;
        }

        return false;
    }

    private static void initFiles(File assignmentsFile, File yieldsFile) throws IOException {
        //clear contents of assignments file and write header to first line
        if (assignmentsFile.delete()) {
            assignmentsFile.createNewFile();
        }
        String assignmentFileHeader = "loan_id,facility_id\n";
        Files.append(assignmentFileHeader, assignmentsFile, Charset.defaultCharset());

        //clear contents of yields file and write header to first line
        if (yieldsFile.delete()) {
            yieldsFile.createNewFile();
        }
        String yieldsFileHeader = "facility_id,expected_yield\n";
        Files.append(yieldsFileHeader, yieldsFile, Charset.defaultCharset());
    }

}

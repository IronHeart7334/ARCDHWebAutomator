package automationSamples;

import automationTools.AbstractPeopleSoftAutomation;
import io.CsvFileRequirements;
import java.util.HashMap;
import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class BlanketPurchaseOrder extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Queries the PeopleSoft Purchase Order Balance Information page to extract funds remaining in blanket purchase order accounts";
    private static final String[] HEADERS = new String[]{
        "BPO ID"
    };
    
    private static final CsvFileRequirements FILE_REQ = new CsvFileRequirements(
        "Input files should be in CSV format, "
        + "and should contain at least one column, labeled 'BPO ID'",
        HEADERS
    );
    
    private final HashMap<String, String> bpoIdToFunds;
    
    public BlanketPurchaseOrder(){
        super(
            "Blanket Purchase Order Balance",
            DESC,
            "https://psreports.losrios.edu/PurchaseOrderInformation.asp", 
            FILE_REQ,
            "https://psreports.losrios.edu/PurchaseOrderInformationQ.asp"
        );
        
        bpoIdToFunds = new HashMap<>();
    }
    
    @Override
    public void initResult(){
        bpoIdToFunds.clear();
    }
    
    @Override
    public void inputQuery(String query) {
        String[] params = query.split(",");
        awaitFindElement(By.xpath("//input[@name='PurchaseOrderNumber']")).sendKeys(params[0]);
        awaitFindElement(By.name("B1")).click();
    }

    @Override
    public String readQueryResult() {
        HtmlTable t = new HtmlTable(awaitFindElement(By.xpath("//table[@border=1]")));
        return t.toCsv(new String[]{"PO Balance"});
    }
}

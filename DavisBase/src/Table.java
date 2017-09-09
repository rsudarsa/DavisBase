import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by ramya on 8/4/17.
 */
public class Table {

    Page page = null;
    public Table(String tableName, int leafNode ) throws FileNotFoundException {
        if(!isTablePresent(tableName)) {
            page = new Page(tableName);
            page.populatePageNewHeaders(page, leafNode);
        }
    }

    public  Table(String tableName) throws FileNotFoundException {
        this.page = new Page(tableName);
        }
    public static boolean isTablePresent(String tableName) throws FileNotFoundException {
        boolean isPresent = false;
        File file = new File(tableName);
        if(file.exists()){
            isPresent = true;
        }
        return isPresent;
    }


}

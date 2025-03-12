package dataaccess;


public class SQLDAO {
    static {
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

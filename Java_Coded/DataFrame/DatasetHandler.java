package DataFrame;

public class DatasetHandler {
    
    static String main_path;
    
    public static void main(String [] args){
        System.out.println("Hello World");
        main_path = System.getProperty("user.dir");
        System.out.println(main_path);
    }

    // Constructor
    public DatasetHandler(){
        main_path = System.getProperty("user.dir");
    }

    public String getDatasetList(int startIndex, int endIndex){
        // TO-DO: Implement method to get list of datasets from a directory
        return "Not yet implemented";
    }
    
}
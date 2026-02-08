package DataFrame;

public class DatasetHandler {
    
    private String main_path;
    private String subPath;
    
    public static void main(String [] args){
        System.out.println("Hello World");
        DatasetHandler handler = new DatasetHandler();
        System.out.println(handler.getRootDirectory());
    }

    // Constructor
    public DatasetHandler(){
        this.main_path = System.getProperty("user.dir") + "\\Datasets";
        this.subPath = "";
    }

    
    public int getTotalDatasets(){
        return 0;
    }
    
    /**
     * Gets the filepath path where the Datasets are stored
     * @return String A path where the Datasets are stored
     */
    public String getRootDirectory(){
        return this.main_path;
    }

    public String getDatasetPath(){
        return this.main_path + "\\" + this.subPath;
    }

    /*
        Things to consider when programming a Dataset Handler:
        1. Folder inside a folder
        2. CSV inside a folder
        3. An empty folder 
    */

    /**
     * A recursive function that returns the 
     * @return
     */
    public String [] getDatasetTypes(){
        
        return null;
    }

    private String [] _getDatasetTypes(int total, String [] datasetTypes){
        return null;
    }
    
    public String getDatasetList(int startIndex, int endIndex){
        // TO-DO: Implement method to get list of datasets from the given directory
        String dir = this.getDatasetPath();
        System.out.print(dir);
        return "Not yet implemented";
    }
}
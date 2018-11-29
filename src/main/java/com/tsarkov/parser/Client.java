import com.tsarkov.parser.*;

public class Client {
    public static void main(String[] args){
        Document document = new Document();
        document.parseFile("./src/main/resources/sourceFile.txt");
        document.sendResultInFile("./src/main/resources/resultFile.txt");
        document.sendResultInDB();
    }
}

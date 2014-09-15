
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author cTomsyck
 * 
 * Zappos application takes in amount user wants to spend, and the amount of items they would like for money spent.
 * I was not familiar with JSON and Treemap before this project.  Always glad to learn new things.
 * Creating database initial takes a few minutes.
 */
public class ZapposNew {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, java.text.ParseException, InterruptedException {

        System.out.println("Please wait while a database is created.");
        System.out.println();

        List<JSONObject> jsonValues = createDatabase();
        TreeMap<Double, String> tm = sortTreeMap(jsonValues);

        //Get user input
        boolean response = false;
        int n;
        double x, items;
        Scanner input = new Scanner(System.in);
        
        do{
        System.out.println("Please enter desired number of products(Enter integer number only):");
        while (!input.hasNextInt()){
            System.out.println("Enter integer number only:");
            input.next();
        }
        n = input.nextInt();

        System.out.println("Please enter desired amount you would like to spend(Example for $125.50 enter 125.50):");
        while (!input.hasNextDouble()){
            System.out.println("please enter number in correct format (Example for $125.50 enter 125.50):");
            input.next();
        }
        x = input.nextDouble();
        
        //could get closer to actual amount by not rounding, but it will search slower.
        int rounded = (int) x;

        //average per item
        items = rounded / n;

        int counter = n;
        URL urlZappos = new URL("http://www.zappos.com/");

        while (counter != 0) {
            if (tm.containsKey(items)) {
                
                System.out.println("productId to purchase is: " + tm.get(items));
                System.out.println("Link to product is: " + urlZappos + tm.get(items));
                tm.remove(items);
                counter--;
            }
            //change to items = items - .01 for more accurate results, but slows program down
            items = items - .25;
        }
        
        System.out.println();
        System.out.println("Would you like to do another combination? (Yes or No)");
        String userResponse = input.next();
        
        if (userResponse.equals("No")){
       response = true;
        }
        
        } while(response == false);

    }

    public static List<JSONObject> createDatabase() throws FileNotFoundException, IOException, ParseException {
        List<JSONObject> jsonValues = new ArrayList<>();
        jsonValues.clear();
        String line1;
        ArrayList<String> brands = new ArrayList<>();
        int size = 1062;

        //file to read
        File file1 = new File("brands.txt");
        BufferedReader br1 = new BufferedReader(new FileReader(file1));

        //add all brands to an ArrayList to be used for searching
        while ((line1 = br1.readLine()) != null) {
            brands.add(line1);
        }

        //Connect to Zappos and grab product information 
        for (int i = 0; i < size; i++) {

            String item = brands.get(i);

            // Make an URL for searching by brand types
            URL url = new URL("http://api.zappos.com/Search/term/" + item
                    + "?excludes=[%22brandName%22,%22styleId%22,%22colorId%22,%22productName%22,%22productUrl%22,"
                    + "%22thumbnailImageUrl%22,%22originalPrice%22,%22percentOff%22,%22currentResultCount%22,%22totalResultCount%22]"
                    + "&key=52ddafbe3ee659bad97fcce7c53592916a6bfd73");

            // Get the input stream through URL Connection
            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            //String line = null;
            String line2 = "";
            File file = new File("output.txt");
            // read each line and write to System.out
            while ((line2 = br.readLine()) != null) {

                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.txt")))) {
                    out.println(line2);
                } catch (IOException e) {

                }
            }

            // read the json file
            FileReader reader = new FileReader("output.txt");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            // get an array from the JSON object
            JSONArray products = (JSONArray) jsonObject.get("results");
            // take the elements of the json array
            for (Object product : products) {
                jsonValues.add((JSONObject) product);
            }

        }
        return jsonValues;
    }

    //move values to treemap for sorting and to compare to user input
    public static TreeMap<Double, String> sortTreeMap(List<JSONObject> jsonValues) throws java.text.ParseException {
        TreeMap<Double, String> tm = new TreeMap<>();
        tm.clear();
        String moveToTreeMap;

        for (int i = 0; i < jsonValues.size(); i++) {

            moveToTreeMap = jsonValues.get(i).toString();

            StringTokenizer st2 = new StringTokenizer(moveToTreeMap, "\"$");

            st2.nextElement();
            st2.nextElement();
            st2.nextElement();
            String productId = (String) st2.nextElement();
            st2.nextElement();
            st2.nextElement();
            st2.nextElement();
            String price = (String) st2.nextElement();

            NumberFormat format = NumberFormat.getInstance();
            Number number = format.parse(price);
            double price1 = number.doubleValue();
            tm.put(price1, productId);

        }
        tm.comparator();
        //System.out.println(tm);
        return tm;
    }

}

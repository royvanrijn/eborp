import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TSharkParser {

    /**
	Find my phone in input, see if location has changed?
     */

    public static void main(String[] args) throws Exception {
    	new TSharkParser().run();
    }
    
    private void run() {
        /**
         * Open the stream and parse the input.
         * Make a REST call for each line (or do we want to bundle them to decrease load?)
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        Map<String, Measurement> hits = new HashMap<String, Measurement>();
        long updateTime = System.currentTimeMillis();
        while(true) {
        	if(System.currentTimeMillis() > updateTime+10000) {
        		updateTime = System.currentTimeMillis();
        		System.out.println("Amount of unique MACs: "+hits.keySet().size());
        		for(String macKey:hits.keySet()) {
        			System.out.println(macKey+"\t"+hits.get(macKey));
        		}
        		//hits.clear();
        	}
            try {
                String input = reader.readLine();
		if (input == null) {
                	continue;
		}
                String[] values = input.replace(" ", "").split(",");
                if(values.length < 2) {
                    continue;
                }
                String time = values[0];
                String MAC = values[1];
                Integer dBm = null;
                if(values.length >= 3) {
                    dBm = Integer.parseInt(values[2]);
                }

                long epochInMillis = (long)(Double.parseDouble(time) * 1000L);
                Date date = new Date(epochInMillis);
                
                System.out.println("At: " + date + " we found " + MAC + " with strength " + dBm);
                Measurement measurement = hits.getOrDefault(MAC, new Measurement());
                measurement.getStrengths().add(dBm);
                hits.put(MAC, measurement);
 
            } catch(Exception e) {
            	e.printStackTrace();
                //Silently ignore...
            }
        }
	}

	private class Measurement {
    	
    	private List<Integer> strengths = new ArrayList<Integer>();
    	
    	public List<Integer> getStrengths() {
			return strengths;
		}
    	
    	@Override
    	public String toString() {
    		List<Integer> copy = new ArrayList<Integer>(strengths);
    		Collections.sort(copy);
    		int median = copy.get(copy.size()/2);
    		
    		int average = 0;
    		for(int s:copy) {
    			average += s;
    		}
    		average /= copy.size();
    		return "[Amount: " + strengths.size() + " \t Avg dBm: " +  average + " \t Median: " + median + "]";
    	}
    	
    }
    
}


/**
* TransitionAOI data Matrix
* DTU
*
* @author  amab@dtu.dk
* @version 2.1
*/

package TransitionAOIs.TransitionAOIs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class process {

	static String delimiter = ";";
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		/*
		 * for this version only the file should have the following format:
		 * ParticipantName	FixationIndex AOI1 AOI2 ....
		 * if your file has different format, you can use the parameter offset to specify the offset that should not be considered while creating the data matrix
		 * in this example since we have only ParticipantName, FixationIndex, and window that should not be included in the data matrix, the offset is 2
		 *
		 * Update: 
		 *  the function areAllcells0 is used in order to ignore the events where all AOI's have 0  (except the ones already removed by the offset like the "window" AOI in our case)
		 *
		 */
		
		String filename = "1.2T2.csv";
		int offset = 3;
		
		// enter the columns to show in the data matrix
		String[] showColumns = new String[] {"AOI[title]Hit","AOI[legend]Hit","AOI[Q1]Hit","AOI[Q2]Hit","AOI[Q3]Hit","AOI[Q4]Hit"};
	
		
		ArrayList<String> headerdata = readHeader(filename);
		
		ArrayList<Integer> selectedColumnsIndexes = getcolumnsSetsIndexes(showColumns,headerdata);
		
		processfile(filename,headerdata,selectedColumnsIndexes,offset);
	
	}
	
	public static ArrayList<Integer> getcolumnsSetsIndexes(String[] selectedColumnsSets,ArrayList<String>  headerdata){
		
		ArrayList<Integer> selectedColumnsIndexes = new ArrayList<Integer>();
		
		for(String column :  selectedColumnsSets){
			selectedColumnsIndexes.add(getAOIindex(column,headerdata ));
		}
		
		return selectedColumnsIndexes;
	}

	public static Integer[][] genreateDataMatrixForParticipant(HashMap<String, Integer> transitionsmapForParticipant,ArrayList<String>  headerdata,int leftoffset){
	    
	    
		   Iterator transitionsit = transitionsmapForParticipant.entrySet().iterator();
		
			  Integer[][] dataMatrix = new Integer[headerdata.size()-2][headerdata.size()-2];
				for(int i=0 ; i<headerdata.size()-leftoffset ; i++){
					for(int j=0; j<headerdata.size()-leftoffset; j++){
						dataMatrix[i][j] = 0;
					}
				}
			  
	    	  while(transitionsit.hasNext()){
			    	HashMap.Entry entry = (HashMap.Entry) transitionsit.next();
			    	  String transition = (String)entry.getKey();
			    	
					  Integer count = (Integer) entry.getValue();
					  
					  StringTokenizer st = new StringTokenizer(transition,"-");
						
						ArrayList<Integer> aoisIndexes = new ArrayList<Integer>();
						
						while(st.hasMoreTokens()){
							aoisIndexes.add(getAOIindex(st.nextToken(),headerdata));
						}
						
						dataMatrix[aoisIndexes.get(0)-leftoffset][aoisIndexes.get(1)-leftoffset] = count ;
						

	
	    	  }
	    	  
	    	  return dataMatrix ;
	}
	
   public static int CalculateSum(Integer[][] dataMatrix, ArrayList<String>  headerdata,ArrayList<Integer>  selectedColumnsIndexes, int leftoffset){
	   int sum = 0 ;
  
		for(int i=0; i<selectedColumnsIndexes.size(); i++){
			for(int j=0 ; j<selectedColumnsIndexes.size(); j++){
				sum += dataMatrix[selectedColumnsIndexes.get(i)-leftoffset][selectedColumnsIndexes.get(j)-leftoffset];
			}
			}
		
	   return sum ;
   }
   
   
   public static Integer[][] UpdateCasesMatrix(Integer[][] dataMatrix, Integer[][] casesMatrix,int size){
	   
	   for(int i=0; i<size ; i++){
		   for(int j=0 ; j<size; j++){
			   casesMatrix[i][j] +=  dataMatrix[i][j] ;
		   }
	   }
	   
	   return casesMatrix ;
   }
   
   public static void writeDataMatrix(String participant, workbook wb, Integer[][] dataMatrix,ArrayList<String>  headerdata,ArrayList<Integer>  selectedColumnsIndexes, int leftoffset) throws IOException{
	   
		Map<Integer, Object[]> empinfo = new TreeMap<Integer, Object[]>();
		int countkey = 1;

		int sum = CalculateSum(dataMatrix, headerdata, selectedColumnsIndexes, leftoffset);

		// first line:
		Object[] newline = new Object[selectedColumnsIndexes.size() + 1];
		int countindex = 0;
		newline[countindex++] = sum;

		for (int i = 0; i < selectedColumnsIndexes.size(); i++) {

			newline[countindex++] = headerdata.get(selectedColumnsIndexes.get(i));
		}
		empinfo.put(countkey++, newline);

		// next lines
		for (int i = 0; i < selectedColumnsIndexes.size(); i++) {
			newline = new Object[selectedColumnsIndexes.size() + 1];
			countindex = 0;
			newline[countindex++] = headerdata.get(selectedColumnsIndexes.get(i));

			
			for (int j = 0; j < selectedColumnsIndexes.size(); j++) {
				newline[countindex++] = dataMatrix[selectedColumnsIndexes.get(i)
						- leftoffset][selectedColumnsIndexes.get(j) - leftoffset];
			}
			
			empinfo.put(countkey++, newline);
		}

		wb.createSpreadSheet(participant, empinfo);

	}
	
	public static void processfile(String filename, ArrayList<String> headerdata,
			ArrayList<Integer> selectedColumnsIndexes, int leftoffset) throws IOException {

		HashMap<String, ArrayList<ArrayList<String>>> dataentries = readEntries(filename);

		HashMap<String, HashMap<String, Integer>> transitionsmap = generateTransitionMap(headerdata, dataentries,leftoffset);

		// init casesMatrix (matrix for all participants)
		Integer[][] casesMatrix = new Integer[headerdata.size() - leftoffset][headerdata.size() - leftoffset];
		for (int i = 0; i < headerdata.size() - leftoffset; i++) {
			for (int j = 0; j < headerdata.size() - leftoffset; j++) {
				casesMatrix[i][j] = 0;
			}
		}

		Iterator cases = transitionsmap.entrySet().iterator();

		// init workbook
		workbook wb = new workbook(filename + "_wb.xlsx");

		while (cases.hasNext()) {
			HashMap.Entry Case = (HashMap.Entry) cases.next();
			String participant = (String) Case.getKey();
			HashMap<String, Integer> value = (HashMap<String, Integer>) Case.getValue();

			Integer[][] dataMatrix = genreateDataMatrixForParticipant(value, headerdata, leftoffset);
			casesMatrix = UpdateCasesMatrix(dataMatrix, casesMatrix, headerdata.size() - leftoffset);
			writeDataMatrix(participant, wb, dataMatrix, headerdata, selectedColumnsIndexes, leftoffset);
		}

		writeDataMatrix("All", wb, casesMatrix, headerdata, selectedColumnsIndexes, leftoffset);

	}
	
	
	static int getAOIindex(String AOI,ArrayList<String> headerdata ){
		int index = 0 ;
		
		while(index<headerdata.size()){
			
			if(headerdata.get(index).equals(AOI)){
				return index ;
			}
			
			index++;
		}
		return -1;
	}
	

	public static HashMap<String, HashMap<String, Integer>> generateTransitionMap(ArrayList<String> headerdata, HashMap<String,ArrayList<ArrayList<String>>> cases, int leftoffset){
		
		HashMap<String, HashMap<String, Integer>> transitionsmap = new HashMap<String, HashMap<String, Integer>>();
		
		// for each data entry in dataentries
		
		Iterator it = cases.entrySet().iterator();
	    int count = 0 ;
	    while (it.hasNext()) {
	    	count++;
	    	HashMap.Entry pair = (HashMap.Entry)it.next();
	    	
	    	String caseId = (String) pair.getKey();
	    	ArrayList<ArrayList<String>> caseevents = (ArrayList<ArrayList<String>>) pair.getValue();
	
	    	
		for(int i=0 ; i<caseevents.size()-1;i++){
			
			int starteventindex = i ;
			
			
			
			while(starteventindex<caseevents.size()-1 && areAllcells0(caseevents.get(starteventindex),leftoffset)){
				starteventindex++;
			}
			
			if(starteventindex>=caseevents.size()-1){
				// reached the end
				break ;
			}
			int endeventindex = starteventindex+1 ;
			
			while(endeventindex<caseevents.size()-1 && areAllcells0(caseevents.get(endeventindex),leftoffset)){
				endeventindex++;
			}
			
			i = starteventindex ;
			
			ArrayList<ArrayList<String>> startsends = findTransitionsFromStatesChanges(caseevents.get(starteventindex), caseevents.get(endeventindex),headerdata,leftoffset);
			
			
			// add each transition to the hashmap if does not exists yet, or increment the value			
			ArrayList<String> disabled =  startsends.get(0);
			ArrayList<String> enabled =  startsends.get(1);

			
			
			for(int k=0; k<disabled.size(); k++){
				for(int l=0; l<enabled.size(); l++){
					
					String transition = disabled.get(k)+"-"+enabled.get(l);
					
			
					HashMap<String, Integer> casetransitions = new HashMap<String, Integer>();
					
					if(transitionsmap.containsKey(caseId)){
					    casetransitions = transitionsmap.get(caseId);
					}

					if(casetransitions.containsKey(transition)){
						
							Integer newvalue = casetransitions.get(transition) +1 ;
							casetransitions.put(transition, newvalue);
					
					}
					else {
						casetransitions.put(transition, 1);
					}
					
//					if(caseId.equals("P04")){
//						  Iterator it2 = casetransitions.entrySet().iterator();
//						    while (it2.hasNext()) {
//						    	HashMap.Entry pair2 = (HashMap.Entry)it2.next();
//						    	String trans = (String) pair2.getKey();
//						    	Integer countt = (Integer) pair2.getValue();
//						    	
//						    	StringTokenizer st = new StringTokenizer(trans,"-");
//								int count2 = 0 ;
//								while(st.hasMoreTokens()){
//									 if(st.nextToken().contains("Q")){
//										 count2++;
//									 }
//								}
//						    	
//						    	if(count2==2){
//						    		System.out.println(trans.replace("AOI[", "").replace("]Hit","")+" : "+countt);
//						    	}
//						    	
//						    }
//					}
					
					transitionsmap.put(caseId, casetransitions);
					
				}
			}
			
			
		}
	    }
		return  transitionsmap;
		
	}
	
	
	
	public static boolean areAllcells0(ArrayList<String> event, int offset){
		
		for(int i=offset ; i<event.size(); i++){
			if(!event.get(i).equals("0")){
				return false ;
			}
		}
		return true ;
	}
	
	public static ArrayList<ArrayList<String>> findTransitionsFromStatesChanges(ArrayList<String> e1, ArrayList<String> e2,ArrayList<String> headerdata, int leftoffset){
	
		System.out.println("Case: "+e1.get(0)+"--between "+e1.get(1)+", and "+e2.get(1));
		
		
		ArrayList<ArrayList<String>> changed = new ArrayList<ArrayList<String>>();
		ArrayList<String> disabled = new ArrayList<String>();
		ArrayList<String> enabled = new ArrayList<String>();
		
		
		for(int i=leftoffset; i<e1.size(); i++){
			if(!e1.get(i).equals(e2.get(i))){
			
			
			
					if(e2.get(i).equals("1")) {
					
						enabled.add(headerdata.get(i));
					}
					else {
						
						disabled.add(headerdata.get(i));
					}

			}
		}
		
		changed.add(disabled);
		changed.add(enabled);
		
		System.out.println("disabled");
		String[] disabledA = new String[disabled.size()];
		disabledA = disabled.toArray(disabledA);
		Arrays.stream(disabledA).forEach(num -> System.out.print(num+" "));
		System.out.println("");
		System.out.println("enabled");
		String[] enabledA = new String[enabled.size()];
		enabledA = enabled.toArray(enabledA);
		Arrays.stream(enabledA).forEach(num -> System.out.print(num+" "));
		System.out.println("");
		
		
		return changed ;
	}
	

	
	public static ArrayList<String> readHeader(String filename) throws IOException{
		ArrayList<String> headerdata = new ArrayList<String>();
	
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line =  bufferedReader.readLine() ;
			
			StringTokenizer st = new StringTokenizer(line,delimiter);
			
			while(st.hasMoreTokens()){
				headerdata.add(st.nextToken());
			}
			

			fileReader.close();
			
			
		
		
		return headerdata ;
	}
	
	
	public static HashMap<String,ArrayList<ArrayList<String>>> readEntries(String filename) throws IOException{
		HashMap<String,ArrayList<ArrayList<String>>> cases = new HashMap<String,ArrayList<ArrayList<String>>>();
		
		
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;
			int count = 0 ;
			while ((line = bufferedReader.readLine()) != null) {
			
				/// skip first entry
				if(count == 0 ) {
					count++;
				}
				
				else {
					
					ArrayList<String> entry = new ArrayList<String>();
					StringTokenizer st = new StringTokenizer(line,delimiter);
					
					while(st.hasMoreTokens()){
						 entry.add(st.nextToken());
					}
					
					
					String caseId = entry.get(0);
					
					if(cases.containsKey(caseId)){
						ArrayList<ArrayList<String>> caseevents = cases.get(caseId);
						caseevents.add(entry);
						cases.put(caseId, caseevents);
					}
					else {
						ArrayList<ArrayList<String>> events = new ArrayList<ArrayList<String>>();
						events.add(entry);
						cases.put(caseId, events);
					}
					

				}

				
			}
			fileReader.close();
	
			return cases ;
		
	}
	
}

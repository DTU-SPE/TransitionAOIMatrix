package TransitionAOIs.TransitionAOIs;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.events.Event;

import TransitionAOIs.TransitionAOIs.process;

public class processTests extends TestCase {
	

	public void testGetcolumnsSetsIndexes() throws IOException {
		
		String filename = "testfile/1.1test.tsv";
		String[] showColumns = new String[] {"AOI[window]Hit","AOI[title]Hit","AOI[graph]Hit","AOI[legend]Hit","AOI[Q1]Hit","AOI[Q2]Hit","AOI[Q3]Hit"};
		
		Integer startcolIndexTest = 2;
		Integer endcolIndexTest = 6;

		
		System.out.println("test testGetcolumnsSetsIndexes()");
  
		 ArrayList<String> header = process.readHeader(filename);
	
		ArrayList<Integer> selectedColumnsIndexes = process.getcolumnsSetsIndexes(showColumns ,header);
	
		
		Integer startindex = selectedColumnsIndexes.get(0);
		Integer endindex = selectedColumnsIndexes.get(selectedColumnsIndexes.size()-1);
		
		assertEquals(startindex, startcolIndexTest);
		assertEquals(endindex, endcolIndexTest);
		
	}

	public void testProcessfile() throws IOException {
		
		System.out.println("test testGenreateDataMatrixForParticipant()");
		
		String filename = "3.2.tsv";
		int leftoffset = 2;
		String participantNameTest = "P01";
		
		HashMap<String,ArrayList<ArrayList<String>>> dataentries = process.readEntries(filename); /// 1st end
		ArrayList<String> header = process.readHeader(filename);
		HashMap<String, HashMap<String, Integer>> transitionsmap = process.generateTransitionMap(header,dataentries);
			
		 	
			Iterator cases = transitionsmap.entrySet().iterator();
			
			
			while (cases.hasNext()) {
			    HashMap.Entry Case = (HashMap.Entry) cases.next();
			    String participant = (String)Case.getKey();
			    HashMap<String, Integer> value = (HashMap<String, Integer>)Case.getValue();
			   
			    if(participant.equals(participantNameTest)){
			    	// generate data matrix for test participant 
			    	Integer[][] dataMatrix = process.genreateDataMatrixForParticipant(value,header, leftoffset); // second end
			    	
			    	// check the transitions and their count in dataMatrix match with data entries
			    	
			    		/// get participant case
			    	ArrayList<ArrayList<String>> events = dataentries.get(participant);
			    	
			    	/// iterate over rows and cols in dataMatrix
			    	for(int i=0 ; i<header.size()-leftoffset; i++){
			    		for(int j=0 ; j<header.size()-leftoffset; j++){
			    			
			    			String starttransition = header.get(i+leftoffset);
			    			String endtransition = header.get(j+leftoffset);
			    			int countcell  =  dataMatrix[i][j];
			    			
			    			int startTindex = process.getAOIindex(starttransition, header);
			    			int endTindex = process.getAOIindex(endtransition, header);
			    			
//			    			System.out.println(starttransition);
//			    			System.out.println(endtransition);
//			    			System.out.println("data matrix: "+countcell);
			    			
			    			/// check if events have the same count for a specific transition
			    			int transitionscounter = 0 ;
			    			
			    			for(int k=0 ; k<events.size()-1 ; k++){
		
			    				ArrayList<String> e1 = events.get(k);
			    				ArrayList<String> e2 = events.get(k+1);
			    				
			    				if(e1.get(startTindex).equals("1") && e1.get(endTindex).equals("0") && e2.get(startTindex).equals("0") && e2.get(endTindex).equals("1")){
			    					transitionscounter++;
			    				}
			    				
			    			}
			    			
//			    			System.out.println("log file:"+transitionscounter);
			    			
			    			assertEquals(countcell,transitionscounter);
			    			
			    			
			    			
			    		}
			    	}
			    
			    	
			    	
			    	
			    	
			    	
			    }
			}
			
		
		
	}

	public void testGenreateDataMatrixForParticipant() throws IOException{
		
		String filename = "testfile/1.1test.tsv";
		int leftoffset = 2;
		String participantNameTest = "P01";
		
		String transitionstartTest = "AOI[Q2]Hit"; // row
		String transitionendTest = "AOI[Q3]Hit"; // col
		Integer countTest = 3;
		
		
		System.out.println("test testGenreateDataMatrixForParticipant()");
		
		HashMap<String,ArrayList<ArrayList<String>>> dataentries = process.readEntries(filename);
		 ArrayList<String> header = process.readHeader(filename);
		 
		 int transitionstartIndex = process.getAOIindex(transitionstartTest, header) - leftoffset;
		 int transitionendIndex = process.getAOIindex(transitionendTest, header) -leftoffset ;

		HashMap<String, HashMap<String, Integer>> transitionsmap = process.generateTransitionMap(header,dataentries);
		
		 	
		Iterator cases = transitionsmap.entrySet().iterator();
		
		
		while (cases.hasNext()) {
		    HashMap.Entry Case = (HashMap.Entry) cases.next();
		    String participant = (String)Case.getKey();
		    HashMap<String, Integer> value = (HashMap<String, Integer>)Case.getValue();
		   
		    if(participant.equals(participantNameTest)){
		    	// test case start
		    	   Integer[][] dataMatrix = process.genreateDataMatrixForParticipant(value,header, leftoffset);
		    	   
		    	   // print matrix
//		    	   for(int i=0 ; i<header.size()-leftoffset; i++){
//		    		   System.out.println("");
//		    		   for(int j=0 ; j<header.size()-leftoffset; j++){
//		    			   System.out.print(dataMatrix[i][j]+" ");
//		    		   }
//		    		   System.out.println("");
//		    	   }
		    	   
		    	   assertEquals(dataMatrix[transitionstartIndex][transitionendIndex],countTest);
		    	   
		    	// test case end   
		    }
		    
		}
	}
	
	public void testGetAOIindex() throws IOException {
		
		String filename = "testfile/1.1test.tsv";
		String AOInameTest = "AOI[graph]Hit";
		int AOIindexTest = 4 ;
		
		
		System.out.println("test testGetAOIindex()");
		
		 ArrayList<String> header = process.readHeader(filename);
		
		int AOIindex = process.getAOIindex(AOInameTest,header);
		assertEquals(AOIindex,AOIindexTest);
		
		
	}

	public void testGenerateTransitionMap() throws IOException {
		

		String filename = "testfile/1.1test.tsv";
		String testParticipantExist = "P01";
		
		String transitionnameTest = "AOI[Q1]Hit-AOI[Q2]Hit";
		Integer transitioncountTest = 5 ;
		
		System.out.println("test testGenerateTransitionMap()");
		
		 ArrayList<String> header = process.readHeader(filename);
		 
		HashMap<String, ArrayList<ArrayList<String>>> Cases = process.readEntries(filename);

		boolean transitionnamefound = false ;

		HashMap<String, HashMap<String, Integer>>  transitionMap = process.generateTransitionMap(header,Cases);
		
		Iterator transitionspercase = transitionMap.entrySet().iterator();
		
		while (transitionspercase.hasNext()) {
			HashMap.Entry Case = (HashMap.Entry) transitionspercase.next();
			String participant = (String) Case.getKey();
			 HashMap<String, Integer> transitions = ( HashMap<String, Integer>) Case.getValue();

			if (participant.equals(testParticipantExist)) {
				
				Iterator transitionsIt = transitions.entrySet().iterator();
				while(transitionsIt.hasNext()){
					HashMap.Entry Transition = (HashMap.Entry) transitionsIt.next();
					String transitioname = (String) Transition.getKey();
					 Integer transitioncount = ( Integer) Transition.getValue();
					 
					 if(transitioname.equals(transitionnameTest)){
						 transitionnamefound = true ;
						 assertEquals(transitioncount,transitioncountTest);
					 }
				
					 
				}
				
			}
			}
			
				if(!transitionnamefound){
					fail("Transition name not found");
				}
			
			
	}

	public void testFindTransitionsFromStatesChanges() throws IOException {

		System.out.println("test testFindTransitionsFromStatesChanges()");

		String filename = "testfile/1.1test.tsv";

		String event1_id = "17";
		String event2_id = "18";
		String testParticipantExist = "P01";
		int countenabled = 0;
		int countdisabled = 0;


		ArrayList<String> header = process.readHeader(filename);

		HashMap<String, ArrayList<ArrayList<String>>> Cases = process.readEntries(filename);

		Iterator cases = Cases.entrySet().iterator();

		while (cases.hasNext()) {
			HashMap.Entry Case = (HashMap.Entry) cases.next();
			String participant = (String) Case.getKey();
			ArrayList<ArrayList<String>> events = (ArrayList<ArrayList<String>>) Case.getValue();

			if (participant.equals(testParticipantExist)) {
				for (int i = 0; i < events.size() - 1; i++) {
					//// test case
					ArrayList<String> e1 = events.get(i);
					ArrayList<String> e2 = events.get(i + 1);

					if (e1.get(1).equals(event1_id) && e2.get(1).equals(event2_id)) {

						ArrayList<ArrayList<String>> foundTransition = process.findTransitionsFromStatesChanges(e1, e2,
								header);

						int disabled = foundTransition.get(0).size();
						int enabled = foundTransition.get(1).size();

						assertEquals(disabled, countdisabled);
						assertEquals(enabled, countenabled);

					}
					/// end of test case
				}
			}

		}

	}

	public void testReadHeader() throws IOException {

		String compvaltest = "ParticipantName ";
		String filename = "testfile/1.1test.tsv";
		ArrayList<String> header = process.readHeader(filename);
		//System.err.println(header.get(0));
		assertEquals(header.get(0).toString().length(), compvaltest.length());

	}

	public void testReadEntries() throws IOException {
		String filename = "testfile/1.1test.tsv";
		String testParticipantExist = "P01";

		System.out.println("test testReadEntries()");

		HashMap<String, ArrayList<ArrayList<String>>> Cases = process.readEntries(filename);

		Iterator cases = Cases.entrySet().iterator();

		while (cases.hasNext()) {
			HashMap.Entry Case = (HashMap.Entry) cases.next();
			String participant = (String) Case.getKey();
			ArrayList<ArrayList<String>> events = (ArrayList<ArrayList<String>>) Case.getValue();

			if (participant.equals(testParticipantExist)) {
				assertEquals(participant, testParticipantExist);

				for (ArrayList<String> event : events) {
					// System.out.println("");

					for (String col : event) {
						// System.out.print(col+" ");
					}

					// System.out.println("");
				}

			}

		}

	}

}

package TransitionAOIs.TransitionAOIs;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.events.Event;

import TransitionAOIs.TransitionAOIs.process;

public class processTests extends TestCase {
	

	public void testGetcolumnsSetsIndexes() throws IOException {
		
		String filename = "1.2.tsv";
		String[] showColumns = new String[] {"AOI[title]Hit","AOI[legend]Hit","AOI[Q1]Hit","AOI[Q2]Hit","AOI[Q3]Hit","AOI[Q4]Hit"};
	    String ParticipantColumn = "ParticipantName";
	    
		
		Integer startcolIndexTest = 1;
		Integer endcolIndexTest = 6;

		
		System.out.println("test testGetcolumnsSetsIndexes()");
  
		ArrayList<String> header = process.readHeader(filename,showColumns,ParticipantColumn,false);
	
		ArrayList<Integer> selectedColumnsIndexes = process.getcolumnsSetsIndexes(showColumns ,header);
	
		
		Integer startindex = selectedColumnsIndexes.get(0);
		Integer endindex = selectedColumnsIndexes.get(selectedColumnsIndexes.size()-1);
		
		assertEquals(startindex, startcolIndexTest);
		assertEquals(endindex, endcolIndexTest);
		
	}

	public void testProcessfile() throws IOException {
		
		System.out.println("test testGenreateDataMatrixForParticipant()");
		
		String filename = "1.2.tsv";
		String[] showColumns = new String[] {"AOI[title]Hit","AOI[legend]Hit","AOI[Q1]Hit","AOI[Q2]Hit","AOI[Q3]Hit","AOI[Q4]Hit"};
	    String ParticipantColumn = "ParticipantName";
	    
	
		int leftoffset = 1;
		String participantNameTest = "P01";
		
		
		ArrayList<String> headerindexes = process.readHeader(filename,showColumns,ParticipantColumn,true);
		HashMap<String,ArrayList<ArrayList<String>>> dataentries =  process.readEntries(filename,showColumns,ParticipantColumn,headerindexes); /// 1st end
		ArrayList<String> header = process.readHeader(filename,showColumns,ParticipantColumn,false);
		HashMap<String, HashMap<String, Integer>> transitionsmap = process.generateTransitionMap(header,dataentries,leftoffset);
			
		 	
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
			    			
			    			int startTindex = process.getColumnIindex(starttransition, header);
			    			int endTindex = process.getColumnIindex(endtransition, header);
			    			
//			    			System.out.println(starttransition);
//			    			System.out.println(endtransition);
//			    			System.out.println("data matrix: "+countcell);
			    			
			    			/// check if events have the same count for a specific transition
			    			int transitionscounter = 0 ;
			    			
			    			for(int k=0 ; k<events.size()-1 ; k++){
		
			    				int starteventindex = k ;
			
			    				while(starteventindex<events.size()-1 && process.areAllcells0(events.get(starteventindex),leftoffset)){
			    					starteventindex++;
			    				}
			    				
			    				if(starteventindex>=events.size()-1){
			    					// reached the end
			    					break ;
			    				}
			    				int endeventindex = starteventindex+1 ;
			    				
			    				while(endeventindex<events.size()-1 && process.areAllcells0(events.get(endeventindex),leftoffset)){
			    					endeventindex++;
			    				}
			    				
			    				k = starteventindex ;
			    				
			    				ArrayList<String> e1 = events.get(starteventindex);
			    				ArrayList<String> e2 = events.get(endeventindex);
			    				
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
		String filename = "1.2.tsv";
		String[] showColumns = new String[] {"AOI[title]Hit","AOI[legend]Hit","AOI[Q1]Hit","AOI[Q2]Hit","AOI[Q3]Hit","AOI[Q4]Hit"};
	    String ParticipantColumn = "ParticipantName";
	    
		
		int leftoffset = 1;
		String participantNameTest = "P01";
		
		String transitionstartTest = "AOI[Q2]Hit"; // row
		String transitionendTest = "AOI[Q3]Hit"; // col
		Integer countTest = 2;
		
		
		System.out.println("test testGenreateDataMatrixForParticipant()");
		
		ArrayList<String> header = process.readHeader(filename,showColumns,ParticipantColumn,false);
		
		System.out.println("test testGenreateDataMatrixForParticipant()");
		
		ArrayList<String> headerindexes = process.readHeader(filename,showColumns,ParticipantColumn,true);
		
		
		HashMap<String,ArrayList<ArrayList<String>>> dataentries = process.readEntries(filename,showColumns,ParticipantColumn,headerindexes);
		
		 
		 int transitionstartIndex = process.getColumnIindex(transitionstartTest, header) - leftoffset;
		 int transitionendIndex = process.getColumnIindex(transitionendTest, header) -leftoffset ;

		HashMap<String, HashMap<String, Integer>> transitionsmap = process.generateTransitionMap(header,dataentries,leftoffset);
		
		 	
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
	
	public void testgetColumnIndex() throws IOException {
		
		String filename = "1.2.tsv";
		String[] showColumns = new String[] {"AOI[title]Hit","AOI[legend]Hit","AOI[Q1]Hit","AOI[Q2]Hit","AOI[Q3]Hit","AOI[Q4]Hit"};
	    String ParticipantColumn = "ParticipantName";
	    
		String AOInameTest = "AOI[Q1]Hit";
		int AOIindexTest = 3 ;
		
		ArrayList<String> header = process.readHeader(filename,showColumns,ParticipantColumn,false);
		
		System.out.println("test testGetAOIindex()");
		
		
		
		int AOIindex = process.getColumnIindex(AOInameTest,header);
		assertEquals(AOIindex,AOIindexTest);
		
		
	}

	public void testGenerateTransitionMap() throws IOException {
		

		String filename = "1.2.tsv";
		String[] showColumns = new String[] {"AOI[title]Hit","AOI[legend]Hit","AOI[Q1]Hit","AOI[Q2]Hit","AOI[Q3]Hit","AOI[Q4]Hit"};
	    String ParticipantColumn = "ParticipantName";
	    
		
		String testParticipantExist = "P01";
		int leftoffset = 1 ;
		
		String transitionnameTest = "AOI[Q1]Hit-AOI[Q2]Hit";
		Integer transitioncountTest = 5 ;
		
		System.out.println("test testGenerateTransitionMap()");
		
		ArrayList<String> header = process.readHeader(filename,showColumns,ParticipantColumn,false);
		ArrayList<String> headerindexes = process.readHeader(filename,showColumns,ParticipantColumn,true);
		 
		HashMap<String, ArrayList<ArrayList<String>>> Cases = process.readEntries(filename,showColumns,ParticipantColumn,headerindexes);

		boolean transitionnamefound = false ;

		HashMap<String, HashMap<String, Integer>>  transitionMap = process.generateTransitionMap(header,Cases,leftoffset);
		
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

		String filename = "1.2.tsv";
		String[] showColumns = new String[] {"AOI[title]Hit","AOI[legend]Hit","AOI[Q1]Hit","AOI[Q2]Hit","AOI[Q3]Hit","AOI[Q4]Hit"};
	    String ParticipantColumn = "ParticipantName";
	    
		

		int event1_id = 21;
		String testParticipantExist = "P01";
		int countenabled = 1;
		int countdisabled = 1;
		int leftoffset = 1;


		ArrayList<String> header = process.readHeader(filename,showColumns,ParticipantColumn,false);
		ArrayList<String> headerindexes = process.readHeader(filename,showColumns,ParticipantColumn,true);

		HashMap<String, ArrayList<ArrayList<String>>> Cases = process.readEntries(filename,showColumns,ParticipantColumn,headerindexes);

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

					if (i==event1_id-1) {

//					    System.out.println("e1");
//					    e1.stream().forEach(System.out::print);
//					    System.out.println("");
//					    
//
//					    System.out.println("e2");
//					    e1.stream().forEach(System.out::print);
//					    System.out.println("");
					    
						ArrayList<ArrayList<String>> foundTransition = process.findTransitionsFromStatesChanges(e1, e2,
								header,leftoffset);

						int disabled = foundTransition.get(0).size();
						int enabled = foundTransition.get(1).size();
						
						//System.err.println(disabled);
					//	System.err.println(enabled);
						
						
//						System.out.println("disabled");
//						String[] disabledA = new String[disabled.size()];
//						disabledA = disabled.toArray(disabledA);
//						Arrays.stream(disabledA).forEach(num -> System.out.print(num+" "));
//						System.out.println("");
//						System.out.println("enabled");
//						String[] enabledA = new String[enabled.size()];
//						enabledA = enabled.toArray(enabledA);
//						Arrays.stream(enabledA).forEach(num -> System.out.print(num+" "));
//						System.out.println("");
						

						assertEquals(disabled, countdisabled);
						assertEquals(enabled, countenabled);

					}
					/// end of test case
				}
			}

		}

		
		
	}

	public void testReadHeader() throws IOException {

		String compvaltest = "ParticipantName";
		String filename = "1.2.tsv";
		String[] showColumns = new String[] {"AOI[title]Hit","AOI[legend]Hit","AOI[Q1]Hit","AOI[Q2]Hit","AOI[Q3]Hit","AOI[Q4]Hit"};
	    String ParticipantColumn = "ParticipantName";
	    
		ArrayList<String> header = process.readHeader(filename,showColumns,ParticipantColumn,false);
			
		assertEquals(header.get(0).toString().length(), compvaltest.length());

	}

	public void testReadEntries() throws IOException {
		String filename = "1.2.tsv";
		String[] showColumns = new String[] {"AOI[title]Hit","AOI[legend]Hit","AOI[Q1]Hit","AOI[Q2]Hit","AOI[Q3]Hit","AOI[Q4]Hit"};
		 String ParticipantColumn = "ParticipantName";
		String testParticipantExist = "P01";
		ArrayList<String> headerindexes = process.readHeader(filename,showColumns,ParticipantColumn,true);


		
		System.out.println("test testReadEntries()");

		HashMap<String, ArrayList<ArrayList<String>>> Cases = process.readEntries(filename,showColumns,ParticipantColumn,headerindexes);

		Iterator cases = Cases.entrySet().iterator();

		while (cases.hasNext()) {
			HashMap.Entry Case = (HashMap.Entry) cases.next();
			String participant = (String) Case.getKey();
			ArrayList<ArrayList<String>> events = (ArrayList<ArrayList<String>>) Case.getValue();

			if (participant.equals(testParticipantExist)) {
				assertEquals(participant, testParticipantExist);
				
				for (ArrayList<String> event : events) {
					
					 System.out.println("");

					for (String col : event) {
						 System.out.print(col+" ");
					}

					 System.out.println("");
				}

			}

		}

	}

}

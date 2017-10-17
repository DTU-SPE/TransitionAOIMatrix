package TransitionAOIs.TransitionAOIs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class workbook {
	
	private String filename ;
	private  XSSFWorkbook workbook ;
	
	
	public workbook(String filename){
		this.filename = filename ;
		
		 //Create blank workbook
	     workbook = new XSSFWorkbook(); 
		
	}
	
	public void createSpreadSheet(String sheetname,Map < Integer, Object[] > empinfo) throws IOException{
		  //Create a blank sheet
	      XSSFSheet spreadsheet = workbook.createSheet(sheetname);
	     

	      //Create row object
	      XSSFRow row;

	      //Iterate over data and write to sheet
	      Set < Integer > keyid = empinfo.keySet();
	      int rowid = 0;

	      for (Integer key : keyid) {
	         row = spreadsheet.createRow(rowid++);
	         Object [] objectArr = empinfo.get(key);
	         int cellid = 0;

	         for (Object obj : objectArr) {
	            Cell cell = row.createCell(cellid++);
	            cell.setCellValue(String.valueOf(obj));
	         }
	      }

	      //Write the workbook in file system
	      FileOutputStream out = new FileOutputStream(new File(filename));
	      workbook.write(out);
	    
	      out.close();
	     
	}
	
	

}

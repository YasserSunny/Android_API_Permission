/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package validation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.*;
import java.util.HashMap;
/**
 *
 * @author yasser
 */
public class TestSet {
    String testSetPath;
    HashMap<String,String> commitHistory;
    
    public TestSet(String testSet)
    {
        this.testSetPath = testSet;
        this.commitHistory = ReadCommitHistory(this.testSetPath);
    }
    
    private HashMap<String,String> ReadCommitHistory(String testSet)
    {
        HashMap<String,String> transactions = new HashMap<String,String>();
        try
        {
            BufferedReader readTestSet = new BufferedReader(new FileReader(testSet));
            String line;
            String[] lineParts;
            String[] lineParts2;
            
            while((line = readTestSet.readLine()) !=  null)
            {
                
                lineParts = line.split("\\t");
                //System.out.println(lineParts[0]);
                //lineParts2 = lineParts[1].split(",");
                //if(lineParts2.length < 10)
                if(lineParts.length > 1)
                    transactions.put(lineParts[0],lineParts[1]);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return transactions;
    }
}

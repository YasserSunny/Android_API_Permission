/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package validation;

import java.util.HashMap;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author yasser
 */
public class AndroGuardRules {
    
    String ruleFilePath;
    HashMap<String,Set<String>> ApiToPermissionRules;
    
    public AndroGuardRules(String filePath)
    {
        this.ruleFilePath = filePath;
        this.ApiToPermissionRules = new HashMap<String,Set<String>>();
    }
    
    public void LoadRules()
    {
        BufferedReader breader;
        String line;
        String[] lineParts;
        String[] lineParts2;
        String currApis="";
        String currPer="";
        Set<String> eachApiPermission;
        
        try
        {
            eachApiPermission = new HashSet<String>();
            breader = new BufferedReader(new FileReader(this.ruleFilePath));
            while((line=breader.readLine()) != null)
            {
                lineParts = line.split(":");
                lineParts2 = lineParts[0].split(";");
                currApis = lineParts2[0].trim().replace("\"", "").replace("$","");
                currApis = currApis.split("/")[currApis.split("/").length-1];
                currApis = "A_"+currApis;
                currPer = lineParts[1].trim().replace("\"", "").replace(",","");
                currPer = "P_"+currPer;
                
                eachApiPermission =  ApiToPermissionRules.get(currApis);
                if(eachApiPermission == null)
                {
                    eachApiPermission = new HashSet<String>();
                    eachApiPermission.add(currPer);
                    ApiToPermissionRules.put(currApis, eachApiPermission);
                }
                else
                {
                    eachApiPermission.add(currPer);
                    ApiToPermissionRules.put(currApis, eachApiPermission);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
}

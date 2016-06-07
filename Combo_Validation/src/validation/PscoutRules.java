/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package validation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author yasser
 */
public class PscoutRules {
    String ruleFilePath;
    HashMap<String,Set<String>> ApiToPermissionRules;
    
    public PscoutRules(String filePath)
    {
        this.ruleFilePath = filePath;
        this.ApiToPermissionRules = new HashMap<String,Set<String>>();
    }
    
    public void LoadRules()
    {

        String lineApiToPerm;
        String[] lineApiToPermParts; 
        BufferedReader apiToPermReader;
        HashMap<String,Set<String>> ApiToPermissionRules;
        Set<String> eachApiPermission;
        
        try
        {
            apiToPermReader = new BufferedReader(new FileReader(this.ruleFilePath));
            ApiToPermissionRules = new HashMap<String,Set<String>>();
            eachApiPermission = new HashSet<String>();
            
            while((lineApiToPerm = apiToPermReader.readLine()) != null)
            {
                //eachApiPermission.clear();
                lineApiToPermParts = lineApiToPerm.split("=>");
                eachApiPermission = this.ApiToPermissionRules.get("A_"+lineApiToPermParts[0].split("\\$")[0].split("\\.")[lineApiToPermParts[0].split("\\$")[0].split("\\.").length-1]);
                
                if(eachApiPermission == null)
                {
                     eachApiPermission = new HashSet<String>();
                     eachApiPermission.add("P_"+lineApiToPermParts[1].split("\\.")[lineApiToPermParts[1].split("\\.").length - 1]);
                     this.ApiToPermissionRules.put("A_"+lineApiToPermParts[0].split("\\$")[0].split("\\.")[lineApiToPermParts[0].split("\\$")[0].split("\\.").length-1], eachApiPermission);
                }
                else
                {
                     eachApiPermission.add("P_"+lineApiToPermParts[1].split("\\.")[lineApiToPermParts[1].split("\\.").length - 1]);
                     this.ApiToPermissionRules.put("A_"+lineApiToPermParts[0].split("\\$")[0].split("\\.")[lineApiToPermParts[0].split("\\$")[0].split("\\.").length-1], eachApiPermission);
                     
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}

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
public class AssocRules {
    String assocRuleFilePath;
    String FileWithIdsPath;
    HashMap<Integer,String> assocRules;
    HashMap<Integer,Float> rulesWithConfidence;
    
    public AssocRules(String assocRule, String fileIds)
    {
        this.FileWithIdsPath = fileIds;
        this.assocRuleFilePath = assocRule;
        this.rulesWithConfidence = new HashMap<Integer,Float>();
        this.assocRules = ReadAssocRule2(this.assocRuleFilePath,this.FileWithIdsPath);
    }
    
    private HashMap<Integer,String> ReadAssocRule(String assocRuleFilePath,String fielWithIdPath)
    {
        String pattern = "==>";
        String pattern2 = "<-";
        Pattern pat = Pattern.compile(pattern);
        Matcher m;
        String line;
        String[] lineParts;
        String[] lineParts2;
        int count = 0;
        int HasMapId = 0;
        HashMap<Integer,String> FileWithId = ReadFilIds(fielWithIdPath);
        HashMap<Integer,String> GroupedFiles = new  HashMap<Integer,String>();
        try
        {
            BufferedReader readAssocFile = new BufferedReader(new FileReader(assocRuleFilePath));
            
            while((line = readAssocFile.readLine())!=null)
            {
                
                m = pat.matcher(line);
                while(m.find())
                {
                    
                    lineParts = line.split(pattern);
                    lineParts2 = lineParts[1].split(" ");
                   
                    if(!lineParts[0].trim().equals("condition") && lineParts2.length<=4)
                    {
                        //System.out.println(lineParts[0]+" "+lineParts2[1]+" "+lineParts2[2].replace("(", "").replace(",", ""));
                        //System.out.println(line);
                        if(lineParts[0].split(" ").length < 2)
                        {
                            if(Float.parseFloat(lineParts2[2].replace("(", "").replace(",", "")) >= 0.50)
                            {
                                HasMapId++;
                                //System.out.println(HasMapId+" "+line);
                                //System.out.println(lineParts2[2].replace("(", "").replace(",", ""));
//                                System.out.println(lineParts[0].trim()+" "
//                                                   + FileWithId.get(Integer.parseInt(lineParts[0].trim()))+","
//                                                    +lineParts2[1].trim()+","+lineParts2[2]+" "
//                                                    + FileWithId.get(Integer.parseInt(lineParts2[1].trim())));
                                //System.out.println(FileWithId.get(Integer.parseInt(lineParts[0].trim()))+","+FileWithId.get(Integer.parseInt(lineParts2[1].trim())));
                                GroupedFiles.put(HasMapId, FileWithId.get(Integer.parseInt(lineParts[0].trim()))+","+FileWithId.get(Integer.parseInt(lineParts2[1].trim())));
                                this.rulesWithConfidence.put(HasMapId,Float.parseFloat(lineParts2[2].replace("(", "").replace(",", "")));
                            }
                        }
                        count++;
                    }
                    
                    
                }
            }
            
           //System.out.println(GroupedFiles.size());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return GroupedFiles;
    }
    
    private HashMap<Integer,String> ReadFilIds(String filepath)
    {
        HashMap<Integer,String> fileIds = new HashMap<Integer,String>();
        String line;
        String[] lineParts;
        BufferedReader readIdFile;
        String item;
        String itemLastPart="";
        String[] itemParts;
        String[] itemParts2;
        
        try
        {
            readIdFile = new BufferedReader(new FileReader(filepath));
            
            while((line = readIdFile.readLine()) != null)
            {
                lineParts = line.split("\\t");
                item=lineParts[0].trim();
                itemParts = item.split("_");
                if(itemParts[0].equals("A"))
                {
                    //itemParts2 = itemParts[1].split("/");
                    //itemLastPart = itemParts[0]+"_"+itemParts2[itemParts2.length-1];
                
                    //fileIds.put(Integer.parseInt(lineParts[1].trim()),itemLastPart);
                    fileIds.put(Integer.parseInt(lineParts[1].trim()),lineParts[0].trim()); // for Unfiltered Training set
                }
                else
                {
                    fileIds.put(Integer.parseInt(lineParts[1].trim()),lineParts[0].trim());
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return fileIds;
    }
    
    private HashMap<Integer,String> ReadAssocRule2(String assocRuleFilePath,String fielWithIdPath)
    {
        String pattern = "==>";
        String pattern2 = "<-";
        String pattern3 = "=>";
        Pattern pat = Pattern.compile(pattern3);
        Matcher m;
        String line;
        String[] lineParts;
        String[] lineParts2;
        String[] lineParts3;
        String ruleCurr;
        String[] ruleParts;
        String[] ruleParts2;
        String[] getStat;
        int count = 0;
        int HasMapId = 0;
        HashMap<Integer,String> FileWithId = ReadFilIds(fielWithIdPath);
        HashMap<Integer,String> GroupedFiles = new  HashMap<Integer,String>();
        try
        {
            BufferedReader readAssocFile = new BufferedReader(new FileReader(assocRuleFilePath));
            
            while((line = readAssocFile.readLine())!=null)
            {
                
                m = pat.matcher(line);
                while(m.find())
                {
                    
                    HasMapId++;
                    lineParts = line.split("=>");
                    getStat= lineParts[1].split("\\s+");
                    
                    if(Float.parseFloat(getStat[2]) > 0.06)
                    {
                        lineParts2 = lineParts[0].split("\\{");
                        lineParts3 = lineParts[1].split("\\{");
                        ruleCurr = lineParts2[1].replace("}","").trim()+"=>"+lineParts3[1].replace("}","");
                        ruleParts = ruleCurr.split(" ");
                        ruleParts2 = ruleParts[0].split("=>");
                        if(!ruleParts2[0].equals(""))
                        {
                            //System.out.println(getNames(ruleParts2[0],FileWithId)+"=>"+getNames(ruleParts2[1],FileWithId)+" "+getStat[2]);
                            GroupedFiles.put(HasMapId,getNames(ruleParts2[0],FileWithId)+","+getNames(ruleParts2[1],FileWithId));
                            this.rulesWithConfidence.put(HasMapId,Float.parseFloat(getStat[2]));
                        }
                    }
                }
            }
            
           //System.out.println(GroupedFiles.size());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return GroupedFiles;
    }
    
    private String getNames(String list,HashMap<Integer,String> FileWithId)
    {
        String NameList="";
        String holdList = list.trim();
        String curr;
        String[] StringItem = holdList.split(",");
        for(String item:StringItem)
        {
            curr = FileWithId.get(Integer.parseInt(item));
            if(NameList.length() < 1)
            {
                NameList=curr;
            }
            else
            {
                NameList=NameList+","+curr;
            }
        }
        return NameList;
    }
    
}

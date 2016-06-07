/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package validation;

import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;


/**
 *
 * @author yasser
 */
public class Analytics {
    public static void RecallPrecisionCalc_Method1(AssocRules assocRules,TestSet testSet,String resultFilePath)
    {
        BufferedWriter writeResult;
        String eachTran;
        String[] tranParts;
        String associationRules;
        String[] ruleParts;
        Set<String> transactionElements;
        Set<String> assocRuleElements;
        Set<String> intersectionSetTemp;
        HashMap<String,Float> Recall = new HashMap<String,Float>();
        HashMap<String,Float> Precision = new HashMap<String,Float>();
        int count=0;
        
        for(String key:testSet.commitHistory.keySet())
        {
            if(count < -1)
            {
                break;
            }
            else
            {
                eachTran = testSet.commitHistory.get(key);
                //System.out.println(eachTran);
                tranParts = eachTran.split(",");
                transactionElements = new HashSet<String>(Arrays.asList(tranParts));
                assocRuleElements = new  HashSet<String>();
                for(String eachFile:transactionElements)
                {
                    for(Integer iter:assocRules.assocRules.keySet())
                    {
                        ruleParts=assocRules.assocRules.get(iter).split(",");
                        for(String eachPart:ruleParts)
                        {
                            if(eachPart.contains(eachFile))
                            {
                                assocRuleElements.add(ruleParts[0]);
                                assocRuleElements.add(ruleParts[1]);
                            }
                        }
                        
                    }
                }
                intersectionSetTemp = new HashSet<String>();
                intersectionSetTemp.addAll(transactionElements);
                intersectionSetTemp.retainAll(assocRuleElements);
                //System.out.println("******************************************");
                //System.out.println("Size of Tranx: "+transactionElements.size()+" "+transactionElements.toString());
                //System.out.println("Size of FoundAssocRule: "+assocRuleElements.size()+" "+assocRuleElements.toString());
                //System.out.println("Size of common: "+intersectionSetTemp.size()+" "+ intersectionSetTemp.toString());
                float recall;
                float precision;
                
                if(transactionElements.size() < 1)
                    recall=0;
                else recall = ((float) intersectionSetTemp.size()/(float) transactionElements.size());
                
                if(assocRuleElements.size() < 1)
                    precision=0;
                else precision = ((float) intersectionSetTemp.size()/(float) assocRuleElements.size());
                
                //System.out.println("Recall: "+ recall + "Precision: "+precision);
                Recall.put(key, recall);
                Precision.put(key, precision);
            }
            count++;
        }
        
        float totalRecall=0;
        float totalPrecision=0;
        
         try
         {
            writeResult = new BufferedWriter(new FileWriter(resultFilePath));
            for(String eachItem:Recall.keySet())
            {
                totalRecall += Recall.get(eachItem);
                totalPrecision += Precision.get(eachItem);
                
                writeResult.append(eachItem+"\t"+Recall.get(eachItem)+"\t"+Precision.get(eachItem));
                writeResult.newLine();
            }
            writeResult.append("Avg Recall: "+ totalRecall/Recall.size()+" Avg Precision: "+totalPrecision/Precision.size());
            writeResult.close();
         }
         catch(Exception e)
         {
             e.printStackTrace();
         }
        
        System.out.println("Avg Recall: "+ totalRecall/Recall.size()+" Avg Precision: "+totalPrecision/Precision.size());
    }
    
    public static void RecallPrecisionCalc_Method2(AssocRules assocRules,TestSet testSet,String resultFilePath,String violatedRulesFile)
    {
        String eachTransaction;
        String fnTran="";
        String[] eachTransactionParts;
        Set<String> predictedTransaction = new HashSet(); 
        Set<String> realTransaction = new HashSet();
        Set<String> intersectionTransaction = new HashSet();
        Set<String> predictedTransactionAll = new HashSet();
        Set<String> intersectionTransactionAll = new HashSet();
        Set<String> FNList = new HashSet();
        int count=0;
        float eachRecall;
        float eachPrecision;
        float RecallSumm=0;
        float PrecisionSum=0;
        float avgRecallPerTran;
        float avgPrecisionPerTran;
        float MaxRecall = 0;
        float MaxPricision = 0;
        float MinRecall = 1;
        float MinPrecision = 1;
        HashMap<String,Float> Recall = new HashMap<String,Float>();
        HashMap<String,Float> Precision = new HashMap<String,Float>();
        HashMap<String,Float> RecallMax = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMax = new HashMap<String,Float>();
        HashMap<String,Float> RecallMin = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMin = new HashMap<String,Float>();
        HashMap<String,String> ViolatedRules = new HashMap<String,String>();
        HashMap<String,String> ViolatedRulesFN = new HashMap<String,String>();
        String RulesOfViolation;
        String RulesOfViolationFN;
        
        for(String key:testSet.commitHistory.keySet())
        {
            if(count < -1)
            {
                break;
            }
            else
            {
                realTransaction.clear();
                eachTransaction = testSet.commitHistory.get(key);
                RulesOfViolation = findViolatedRules(eachTransaction,assocRules.assocRules,assocRules.rulesWithConfidence);
                //RulesOfViolationFN = findViolatedRulesFN(eachTransaction,assocRules.assocRules,assocRules.rulesWithConfidence);
                ViolatedRules.put(key+" : "+eachTransaction, RulesOfViolation);
                //ViolatedRulesFN.put(key+" : "+eachTransaction, RulesOfViolationFN);
                eachTransactionParts = testSet.commitHistory.get(key).split(",");
                Collections.addAll(realTransaction, eachTransactionParts);
                RecallSumm   = 0;
                PrecisionSum = 0;
                MaxRecall = 0;
                MaxPricision = 0;
                MinRecall = 1;
                MinPrecision = 1;
                predictedTransactionAll.clear();
                intersectionTransactionAll.clear();
                FNList.clear();
                for(String eachFileInTran:eachTransactionParts)
                {
                    intersectionTransaction.clear();
                    predictedTransaction.clear();
                    predictedTransaction = findAllRules(eachFileInTran,assocRules);
                    intersectionTransaction.addAll(predictedTransaction);
                    intersectionTransaction.retainAll(realTransaction);
                    predictedTransactionAll.addAll(predictedTransaction);
                    
                    eachRecall = (float) intersectionTransaction.size()/(float) realTransaction.size();
                    if(predictedTransaction.size() > 0)
                        eachPrecision = (float) intersectionTransaction.size()/(float) predictedTransaction.size();
                    else
                        eachPrecision=0;
                    
                    RecallSumm += eachRecall;
                    PrecisionSum += eachPrecision;
                    
                    if(MaxRecall < eachRecall)
                        MaxRecall = eachRecall;
                    if(MaxPricision < eachPrecision)
                        MaxPricision = eachPrecision;
                    if(eachRecall < MinRecall)
                        MinRecall = eachRecall;
                    if(eachPrecision < MinPrecision)
                        MinPrecision = eachPrecision;
                    //System.out.println("Transaction no: "+key+"Recall: "+eachRecall+"Precision: "+eachPrecision);
                    //System.out.println("Current trasaction: "+eachTransaction);
                    //System.out.println("Number of Real Transaction:"+realTransaction.size()+"Number of Predicted Transaction:"+predictedTransaction.size()+" Intersection Set size: "+intersectionTransaction.size());
                }
                intersectionTransactionAll.addAll(predictedTransactionAll);
                intersectionTransactionAll.retainAll(realTransaction);
                
                FNList.addAll(realTransaction);
                FNList.removeAll(predictedTransactionAll);
                
                System.out.println("Original: "+realTransaction);
                System.out.println("Predicted: "+predictedTransactionAll);
                System.out.println("Missed: "+FNList);
                
                fnTran= "";
                for(String eachStr:FNList)
                {
                    if(fnTran.length() < 1)
                    {
                        fnTran =  eachStr;
                    }
                    else fnTran =  fnTran+","+eachStr;
                }
                //System.out.println(realTransaction.size()+" "+predictedTransactionAll.size()+" "+FNList.size()+"" +fnTran);
                //System.out.println("Before call: "+eachTransaction);
                //System.out.println("Before call: "+fnTran);
                RulesOfViolationFN = findViolatedRulesFN(fnTran,assocRules.assocRules,assocRules.rulesWithConfidence);
                ViolatedRulesFN.put(key+" : "+eachTransaction, RulesOfViolationFN);
                
                avgRecallPerTran = RecallSumm/(float) eachTransactionParts.length;
                avgPrecisionPerTran = PrecisionSum/(float) eachTransactionParts.length;
                
//                if(eachTransactionParts.length<2)
//                {
//                    System.out.println("Current trasaction: "+eachTransaction);
//                    System.out.println(predictedTransaction.toString());
//                }    
                    
                //System.out.println("Number of Predicted Transaction:"+predictedTransaction.size());
                
                Recall.put(key, avgRecallPerTran);
                Precision.put(key, avgPrecisionPerTran);
                RecallMax.put(key, MaxRecall);
                PrecisionMax.put(key, MaxPricision);
                RecallMin.put(key, MinRecall);
                PrecisionMin.put(key, MinPrecision);
                
            }
            count++;
        }
        
        //System.out.println("Recall size:" + Recall.size());
        //System.out.println("Precision size:" + Precision.size());
        RecallAndPrecisionDataWriting(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,resultFilePath);
        
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(violatedRulesFile));
            BufferedWriter bwFN = new BufferedWriter(new FileWriter(violatedRulesFile+"FN"));
            for(String trans:ViolatedRules.keySet())
            {
                if(ViolatedRules.get(trans).toString().length()>0)
                {
                    bw.append(trans+" ---- "+ViolatedRules.get(trans));
                    bw.newLine();
                }
                if(ViolatedRulesFN.get(trans).toString().length()>0)
                {
                    bwFN.append(trans+" ---- "+ViolatedRulesFN.get(trans));
                    bwFN.newLine();
                }
            }
            bw.close();
            bwFN.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void RecallPrecisionCalc_Method_A_P(AssocRules assocRules,TestSet testSet,String resultFilePath,String violatedRulesFile, String numOfPerm)
    {
        String eachTransaction;
        String[] eachTransactionParts;
        Set<String> predictedTransaction = new HashSet(); 
        Set<String> predictedTransactionAll = new HashSet();
        Set<String> intersectionTransactionAll = new HashSet();
        Set<String> realTransaction = new HashSet();
        Set<String> realTransaction_Permission = new HashSet();
        Set<String> realTransaction_API = new HashSet();
        Set<String> intersectionTransaction = new HashSet();
        int count=0;
        float eachRecall;
        float eachPrecision;
        float RecallSumm=0;
        float PrecisionSum=0;
        float avgRecallPerTran;
        float avgPrecisionPerTran;
        float overallRecallPerTran;
        float overallPrecisionPerTran;
        float MaxRecall = 0;
        float MaxPricision = 0;
        float MinRecall = 1;
        float MinPrecision = 1;
        HashMap<String,Float> Recall = new HashMap<String,Float>();
        HashMap<String,Float> Precision = new HashMap<String,Float>();
        HashMap<String,Float> RecallMax = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMax = new HashMap<String,Float>();
        HashMap<String,Float> RecallMin = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMin = new HashMap<String,Float>();
        HashMap<String,Float> RecallOverall = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionOverall = new HashMap<String,Float>();
        HashMap<String,String> ViolatedRules = new HashMap<String,String>();
        String RulesOfViolation;
        //String numberOfPermissions = numOfPerm;
        BufferedWriter PermWriter;
        
        try
        {
            PermWriter = new BufferedWriter(new FileWriter(numOfPerm));
            for(String key:testSet.commitHistory.keySet())
            {
                if(count < -1)
                {
                    break;
                }
                else
                {
                    realTransaction.clear();
                    realTransaction_API.clear();
                    realTransaction_Permission.clear();
                    eachTransaction = testSet.commitHistory.get(key);
                    //RulesOfViolation = findViolatedRules(eachTransaction,assocRules.assocRules,assocRules.rulesWithConfidence);
                    //ViolatedRules.put(key+" : "+eachTransaction, RulesOfViolation);
                    eachTransactionParts = testSet.commitHistory.get(key).split(",");

                    for(String eachPart:eachTransactionParts)
                    {
                        String[] PreFixes= eachPart.split("_");
                        if(PreFixes[0].equals("P"))
                            realTransaction_Permission.add(eachPart);
                        else
                            realTransaction_API.add(eachPart);
                    }
                    System.out.println("Number of APIS: "+realTransaction_API.size());
                    Collections.addAll(realTransaction, eachTransactionParts);
                    RecallSumm   = 0;
                    PrecisionSum = 0;
                    MaxRecall = 0;
                    MaxPricision = 0;
                    MinRecall = 1;
                    MinPrecision = 1;
                    predictedTransactionAll.clear();
                    intersectionTransactionAll.clear();
                    int count2=0;
                    for(String eachFileInTran:realTransaction_API)
                    {
                        count2++;
                        intersectionTransaction.clear();
                        predictedTransaction.clear();
                        predictedTransaction = findAllRules3(eachFileInTran,assocRules);
                        intersectionTransaction.addAll(predictedTransaction);
                        intersectionTransaction.retainAll(realTransaction_Permission);
                        predictedTransactionAll.addAll(predictedTransaction);

                        eachRecall = (float) intersectionTransaction.size()/(float) realTransaction_Permission.size();
                        if(predictedTransaction.size() > 0)
                            eachPrecision = (float) intersectionTransaction.size()/(float) predictedTransaction.size();
                        else
                            eachPrecision=0;

                        RecallSumm += eachRecall;
                        PrecisionSum += eachPrecision;

                        if(MaxRecall < eachRecall)
                            MaxRecall = eachRecall;
                        if(MaxPricision < eachPrecision)
                            MaxPricision = eachPrecision;
                        if(eachRecall < MinRecall)
                            MinRecall = eachRecall;
                        if(eachPrecision < MinPrecision)
                            MinPrecision = eachPrecision;
                        System.out.println(count2+" Transaction no: "+key+"Recall: "+eachRecall+"Precision: "+eachPrecision);
                        System.out.println(count2+" Current trasaction: "+eachTransaction);
                        System.out.println(count2+" Number of Real Transaction:"+realTransaction.size()+"Number of Predicted Transaction:"+predictedTransaction.size()+" Intersection Set size: "+intersectionTransaction.size());
                    }
                    intersectionTransactionAll.addAll(predictedTransactionAll);
                    intersectionTransactionAll.retainAll(realTransaction);
                    //System.out.println(predictedTransactionAll.size()+" "+intersectionTransactionAll.size());
                    avgRecallPerTran = RecallSumm/(float) realTransaction_API.size();
                    avgPrecisionPerTran = PrecisionSum/(float) realTransaction_API.size();

                    PermWriter.append(realTransaction_Permission.size()+"\t"+predictedTransactionAll.size()+"\t"+intersectionTransactionAll.size());
                    PermWriter.newLine();

                    overallRecallPerTran = intersectionTransactionAll.size()/ (float) realTransaction_Permission.size();
                    if(predictedTransactionAll.size() > 0)
                        overallPrecisionPerTran = (float) intersectionTransactionAll.size()/(float) predictedTransactionAll.size();
                    else
                       overallPrecisionPerTran=0;

    //                if(eachTransactionParts.length<2)
    //                {
    //                    System.out.println("Current trasaction: "+eachTransaction);
    //                    System.out.println(predictedTransaction.toString());
    //                }    

                    //System.out.println("Number of Predicted Transaction:"+predictedTransaction.size());

                    Recall.put(key, avgRecallPerTran);
                    Precision.put(key, avgPrecisionPerTran);
                    RecallMax.put(key, MaxRecall);
                    PrecisionMax.put(key, MaxPricision);
                    RecallMin.put(key, MinRecall);
                    PrecisionMin.put(key, MinPrecision);
                    RecallOverall.put(key,overallRecallPerTran);
                    PrecisionOverall.put(key,overallPrecisionPerTran);

                }
                count++;
            }
            for(String perm:predictedTransactionAll)
            {
                System.out.println(perm);
            }
            PermWriter.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        //System.out.println("Recall size:" + Recall.size());
        //System.out.println("Precision size:" + Precision.size());
        //RecallAndPrecisionDataWriting(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,resultFilePath);
        RecallAndPrecisionDataWriting2(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,RecallOverall,PrecisionOverall,resultFilePath);
        
//        try
//        {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(violatedRulesFile));
//            for(String trans:ViolatedRules.keySet())
//            {
//                if(ViolatedRules.get(trans).toString().length()>0)
//                {
//                    bw.append(trans+" ---- "+ViolatedRules.get(trans));
//                    bw.newLine();
//                }
//            }
//            bw.close();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }
    
    public static void RecallPrecisionCalc_Method_P_P(AssocRules assocRules,TestSet testSet,String resultFilePath,String violatedRulesFile)
    {
        String eachTransaction;
        String[] eachTransactionParts;
        Set<String> predictedTransaction = new HashSet(); 
        Set<String> realTransaction = new HashSet();
        Set<String> realTransaction_Permission = new HashSet();
        Set<String> realTransaction_API = new HashSet();
        Set<String> intersectionTransaction = new HashSet();
        int count=0;
        float eachRecall;
        float eachPrecision;
        float RecallSumm=0;
        float PrecisionSum=0;
        float avgRecallPerTran;
        float avgPrecisionPerTran;
        float MaxRecall = 0;
        float MaxPricision = 0;
        float MinRecall = 1;
        float MinPrecision = 1;
        HashMap<String,Float> Recall = new HashMap<String,Float>();
        HashMap<String,Float> Precision = new HashMap<String,Float>();
        HashMap<String,Float> RecallMax = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMax = new HashMap<String,Float>();
        HashMap<String,Float> RecallMin = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMin = new HashMap<String,Float>();
        HashMap<String,String> ViolatedRules = new HashMap<String,String>();
        String RulesOfViolation;
        
        for(String key:testSet.commitHistory.keySet())
        {
            if(count < -1)
            {
                break;
            }
            else
            {
                realTransaction.clear();
                realTransaction_API.clear();
                realTransaction_Permission.clear();
                eachTransaction = testSet.commitHistory.get(key);
                //RulesOfViolation = findViolatedRules(eachTransaction,assocRules.assocRules,assocRules.rulesWithConfidence);
                //ViolatedRules.put(key+" : "+eachTransaction, RulesOfViolation);
                eachTransactionParts = testSet.commitHistory.get(key).split(",");
                
                for(String eachPart:eachTransactionParts)
                {
                    String[] PreFixes= eachPart.split("_");
                    if(PreFixes[0].equals("P"))
                        realTransaction_Permission.add(eachPart);
                    else
                        realTransaction_API.add(eachPart);
                }
                Collections.addAll(realTransaction, eachTransactionParts);
                RecallSumm   = 0;
                PrecisionSum = 0;
                MaxRecall = 0;
                MaxPricision = 0;
                MinRecall = 1;
                MinPrecision = 1;
                for(String eachFileInTran:realTransaction_Permission)
                {
                    intersectionTransaction.clear();
                    predictedTransaction.clear();
                    predictedTransaction = findAllRules3(eachFileInTran,assocRules);
                    intersectionTransaction.addAll(predictedTransaction);
                    intersectionTransaction.retainAll(realTransaction_Permission);
                    
                    eachRecall = (float) intersectionTransaction.size()/(float) realTransaction_Permission.size();
                    if(predictedTransaction.size() > 0)
                        eachPrecision = (float) intersectionTransaction.size()/(float) predictedTransaction.size();
                    else
                        eachPrecision=0;
                    
                    RecallSumm += eachRecall;
                    PrecisionSum += eachPrecision;
                    
                    if(MaxRecall < eachRecall)
                        MaxRecall = eachRecall;
                    if(MaxPricision < eachPrecision)
                        MaxPricision = eachPrecision;
                    if(eachRecall < MinRecall)
                        MinRecall = eachRecall;
                    if(eachPrecision < MinPrecision)
                        MinPrecision = eachPrecision;
                    //System.out.println("Transaction no: "+key+"Recall: "+eachRecall+"Precision: "+eachPrecision);
                    //System.out.println("Current trasaction: "+eachTransaction);
                    //System.out.println("Number of Real Transaction:"+realTransaction.size()+"Number of Predicted Transaction:"+predictedTransaction.size()+" Intersection Set size: "+intersectionTransaction.size());
                }
                
                avgRecallPerTran = RecallSumm/(float) realTransaction_Permission.size();
                avgPrecisionPerTran = PrecisionSum/(float) realTransaction_Permission.size();
                
//                if(eachTransactionParts.length<2)
//                {
//                    System.out.println("Current trasaction: "+eachTransaction);
//                    System.out.println(predictedTransaction.toString());
//                }    
                    
                //System.out.println("Number of Predicted Transaction:"+predictedTransaction.size());
                
                Recall.put(key, avgRecallPerTran);
                Precision.put(key, avgPrecisionPerTran);
                RecallMax.put(key, MaxRecall);
                PrecisionMax.put(key, MaxPricision);
                RecallMin.put(key, MinRecall);
                PrecisionMin.put(key, MinPrecision);
                
            }
            count++;
        }
        
        //System.out.println("Recall size:" + Recall.size());
        //System.out.println("Precision size:" + Precision.size());
        RecallAndPrecisionDataWriting(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,resultFilePath);
        
//        try
//        {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(violatedRulesFile));
//            for(String trans:ViolatedRules.keySet())
//            {
//                if(ViolatedRules.get(trans).toString().length()>0)
//                {
//                    bw.append(trans+" ---- "+ViolatedRules.get(trans));
//                    bw.newLine();
//                }
//            }
//            bw.close();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }
    
    public static void RecallPrecisionCalc_Method_P_A(AssocRules assocRules,TestSet testSet,String resultFilePath,String violatedRulesFile)
    {
        String eachTransaction;
        String[] eachTransactionParts;
        Set<String> predictedTransaction = new HashSet(); 
        Set<String> realTransaction = new HashSet();
        Set<String> realTransaction_Permission = new HashSet();
        Set<String> realTransaction_API = new HashSet();
        Set<String> intersectionTransaction = new HashSet();
        Set<String> predictedTransactionAll = new HashSet();
        Set<String> intersectionTransactionAll = new HashSet();
        int count=0;
        float eachRecall;
        float eachPrecision;
        float RecallSumm=0;
        float PrecisionSum=0;
        float avgRecallPerTran;
        float avgPrecisionPerTran;
        float MaxRecall = 0;
        float MaxPricision = 0;
        float MinRecall = 1;
        float MinPrecision = 1;
        float overallRecallPerTran;
        float overallPrecisionPerTran;
        HashMap<String,Float> Recall = new HashMap<String,Float>();
        HashMap<String,Float> Precision = new HashMap<String,Float>();
        HashMap<String,Float> RecallMax = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMax = new HashMap<String,Float>();
        HashMap<String,Float> RecallMin = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMin = new HashMap<String,Float>();
        HashMap<String,String> ViolatedRules = new HashMap<String,String>();
        HashMap<String,Float> RecallOverall = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionOverall = new HashMap<String,Float>();
        String RulesOfViolation;
        
        for(String key:testSet.commitHistory.keySet())
        {
            if(count < -1)
            {
                break;
            }
            else
            {
                realTransaction.clear();
                realTransaction_API.clear();
                realTransaction_Permission.clear();
                eachTransaction = testSet.commitHistory.get(key);
                //RulesOfViolation = findViolatedRules(eachTransaction,assocRules.assocRules,assocRules.rulesWithConfidence);
                //ViolatedRules.put(key+" : "+eachTransaction, RulesOfViolation);
                eachTransactionParts = testSet.commitHistory.get(key).split(",");
                
                for(String eachPart:eachTransactionParts)
                {
                    String[] PreFixes= eachPart.split("_");
                    if(PreFixes[0].equals("P"))
                        realTransaction_Permission.add(eachPart);
                    else
                        realTransaction_API.add(eachPart);
                }
                Collections.addAll(realTransaction, eachTransactionParts);
                RecallSumm   = 0;
                PrecisionSum = 0;
                MaxRecall = 0;
                MaxPricision = 0;
                MinRecall = 1;
                MinPrecision = 1;
                predictedTransactionAll.clear();
                intersectionTransactionAll.clear();
                for(String eachFileInTran:realTransaction_Permission)
                {
                    intersectionTransaction.clear();
                    predictedTransaction.clear();
                    predictedTransaction = findAllRules4(eachFileInTran,assocRules);
                    intersectionTransaction.addAll(predictedTransaction);
                    intersectionTransaction.retainAll(realTransaction_API);
                    predictedTransactionAll.addAll(predictedTransaction);
                    
                    eachRecall = (float) intersectionTransaction.size()/(float) realTransaction_API.size();
                    if(predictedTransaction.size() > 0)
                        eachPrecision = (float) intersectionTransaction.size()/(float) predictedTransaction.size();
                    else
                        eachPrecision=0;
                    
                    RecallSumm += eachRecall;
                    PrecisionSum += eachPrecision;
                    
                    if(MaxRecall < eachRecall)
                        MaxRecall = eachRecall;
                    if(MaxPricision < eachPrecision)
                        MaxPricision = eachPrecision;
                    if(eachRecall < MinRecall)
                        MinRecall = eachRecall;
                    if(eachPrecision < MinPrecision)
                        MinPrecision = eachPrecision;
                    //System.out.println("Transaction no: "+key+"Recall: "+eachRecall+"Precision: "+eachPrecision);
                    //System.out.println("Current trasaction: "+eachTransaction);
                    //System.out.println("Number of Real Transaction:"+realTransaction.size()+"Number of Predicted Transaction:"+predictedTransaction.size()+" Intersection Set size: "+intersectionTransaction.size());
                }
                intersectionTransactionAll.addAll(predictedTransactionAll);
                intersectionTransactionAll.retainAll(realTransaction);
                
                avgRecallPerTran = RecallSumm/(float) realTransaction_Permission.size();
                avgPrecisionPerTran = PrecisionSum/(float) realTransaction_Permission.size();
                
                overallRecallPerTran = intersectionTransactionAll.size()/ (float) realTransaction_API.size();
                if(predictedTransactionAll.size() > 0)
                    overallPrecisionPerTran = (float) intersectionTransactionAll.size()/(float) predictedTransactionAll.size();
                else
                   overallPrecisionPerTran=0;
                
//                if(eachTransactionParts.length<2)
//                {
//                    System.out.println("Current trasaction: "+eachTransaction);
//                    System.out.println(predictedTransaction.toString());
//                }    
                    
                //System.out.println("Number of Predicted Transaction:"+predictedTransaction.size());
                
                Recall.put(key, avgRecallPerTran);
                Precision.put(key, avgPrecisionPerTran);
                RecallMax.put(key, MaxRecall);
                PrecisionMax.put(key, MaxPricision);
                RecallMin.put(key, MinRecall);
                PrecisionMin.put(key, MinPrecision);
                RecallOverall.put(key,overallRecallPerTran);
                PrecisionOverall.put(key,overallPrecisionPerTran);
                
            }
            count++;
        }
        
        //System.out.println("Recall size:" + Recall.size());
        //System.out.println("Precision size:" + Precision.size());
        //RecallAndPrecisionDataWriting(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,resultFilePath);
        RecallAndPrecisionDataWriting2(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,RecallOverall,PrecisionOverall,resultFilePath);
        
//        try
//        {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(violatedRulesFile));
//            for(String trans:ViolatedRules.keySet())
//            {
//                if(ViolatedRules.get(trans).toString().length()>0)
//                {
//                    bw.append(trans+" ---- "+ViolatedRules.get(trans));
//                    bw.newLine();
//                }
//            }
//            bw.close();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }
    
    public static void RecallPrecisionCalc_Method_A_A(AssocRules assocRules,TestSet testSet,String resultFilePath,String violatedRulesFile)
    {
        String eachTransaction;
        String[] eachTransactionParts;
        Set<String> predictedTransaction = new HashSet(); 
        Set<String> realTransaction = new HashSet();
        Set<String> realTransaction_Permission = new HashSet();
        Set<String> realTransaction_API = new HashSet();
        Set<String> intersectionTransaction = new HashSet();
        int count=0;
        float eachRecall;
        float eachPrecision;
        float RecallSumm=0;
        float PrecisionSum=0;
        float avgRecallPerTran;
        float avgPrecisionPerTran;
        float MaxRecall = 0;
        float MaxPricision = 0;
        float MinRecall = 1;
        float MinPrecision = 1;
        HashMap<String,Float> Recall = new HashMap<String,Float>();
        HashMap<String,Float> Precision = new HashMap<String,Float>();
        HashMap<String,Float> RecallMax = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMax = new HashMap<String,Float>();
        HashMap<String,Float> RecallMin = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMin = new HashMap<String,Float>();
        HashMap<String,String> ViolatedRules = new HashMap<String,String>();
        String RulesOfViolation;
        
        for(String key:testSet.commitHistory.keySet())
        {
            if(count < -1)
            {
                break;
            }
            else
            {
                realTransaction.clear();
                realTransaction_API.clear();
                realTransaction_Permission.clear();
                eachTransaction = testSet.commitHistory.get(key);
                //RulesOfViolation = findViolatedRules(eachTransaction,assocRules.assocRules,assocRules.rulesWithConfidence);
                //ViolatedRules.put(key+" : "+eachTransaction, RulesOfViolation);
                eachTransactionParts = testSet.commitHistory.get(key).split(",");
                
                for(String eachPart:eachTransactionParts)
                {
                    String[] PreFixes= eachPart.split("_");
                    if(PreFixes[0].equals("P"))
                        realTransaction_Permission.add(eachPart);
                    else
                        realTransaction_API.add(eachPart);
                }
                Collections.addAll(realTransaction, eachTransactionParts);
                RecallSumm   = 0;
                PrecisionSum = 0;
                MaxRecall = 0;
                MaxPricision = 0;
                MinRecall = 1;
                MinPrecision = 1;
                for(String eachFileInTran:realTransaction_API)
                {
                    intersectionTransaction.clear();
                    predictedTransaction.clear();
                    predictedTransaction = findAllRules4(eachFileInTran,assocRules);
                    intersectionTransaction.addAll(predictedTransaction);
                    intersectionTransaction.retainAll(realTransaction_API);
                    
                    eachRecall = (float) intersectionTransaction.size()/(float) realTransaction_API.size();
                    if(predictedTransaction.size() > 0)
                        eachPrecision = (float) intersectionTransaction.size()/(float) predictedTransaction.size();
                    else
                        eachPrecision=0;
                    
                    RecallSumm += eachRecall;
                    PrecisionSum += eachPrecision;
                    
                    if(MaxRecall < eachRecall)
                        MaxRecall = eachRecall;
                    if(MaxPricision < eachPrecision)
                        MaxPricision = eachPrecision;
                    if(eachRecall < MinRecall)
                        MinRecall = eachRecall;
                    if(eachPrecision < MinPrecision)
                        MinPrecision = eachPrecision;
                    //System.out.println("Transaction no: "+key+"Recall: "+eachRecall+"Precision: "+eachPrecision);
                    //System.out.println("Current trasaction: "+eachTransaction);
                    //System.out.println("Number of Real Transaction:"+realTransaction.size()+"Number of Predicted Transaction:"+predictedTransaction.size()+" Intersection Set size: "+intersectionTransaction.size());
                }
                
                avgRecallPerTran = RecallSumm/(float) realTransaction_API.size();
                avgPrecisionPerTran = PrecisionSum/(float) realTransaction_API.size();
                
//                if(eachTransactionParts.length<2)
//                {
//                    System.out.println("Current trasaction: "+eachTransaction);
//                    System.out.println(predictedTransaction.toString());
//                }    
                    
                //System.out.println("Number of Predicted Transaction:"+predictedTransaction.size());
                
                Recall.put(key, avgRecallPerTran);
                Precision.put(key, avgPrecisionPerTran);
                RecallMax.put(key, MaxRecall);
                PrecisionMax.put(key, MaxPricision);
                RecallMin.put(key, MinRecall);
                PrecisionMin.put(key, MinPrecision);
                
            }
            count++;
        }
        
        //System.out.println("Recall size:" + Recall.size());
        //System.out.println("Precision size:" + Precision.size());
        RecallAndPrecisionDataWriting(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,resultFilePath);
        
//        try
//        {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(violatedRulesFile));
//            for(String trans:ViolatedRules.keySet())
//            {
//                if(ViolatedRules.get(trans).toString().length()>0)
//                {
//                    bw.append(trans+" ---- "+ViolatedRules.get(trans));
//                    bw.newLine();
//                }
//            }
//            bw.close();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }
    
    public static Set<String> findAllRules(String eachFileInTran,AssocRules ar)
    {
        Set<String> relatedRules = new HashSet();
        Set<String> ruleFromRightSide = new HashSet();
        ArrayList<String> fileFromRules = new ArrayList<String>();
        String[] currentRules;
        for(Integer key:ar.assocRules.keySet())
        {
            currentRules = ar.assocRules.get(key).split(",");
            if(eachFileInTran.equals(currentRules[0]))
            {
                fileFromRules.add(currentRules[1]);
                ruleFromRightSide = findAllRulesL2(currentRules[1],ar);
            }
            relatedRules = new HashSet(fileFromRules);
            relatedRules.addAll(ruleFromRightSide);
        }
        
        
        return relatedRules;
    }
    
    public static Set<String> findAllRulesL2(String fileRule, AssocRules allrules)
    {
        Set<String> level2Rules;
        ArrayList<String> rules = new ArrayList<String>();
        String[] currentRules;
        
        for(Integer key:allrules.assocRules.keySet())
        {
            currentRules = allrules.assocRules.get(key).split(",");
            if(fileRule.equals(currentRules[0]))
            {
                rules.add(currentRules[1]);
            }
        }
        
        level2Rules =new HashSet(rules);
        return level2Rules;
    }
    
    public static Set<String> findAllRulesL2A(String fileRule, AssocRules allrules,String RuleType)
    {
        Set<String> level2Rules;
        ArrayList<String> rules = new ArrayList<String>();
        String[] currentRules;
        
        for(Integer key:allrules.assocRules.keySet())
        {
            currentRules = allrules.assocRules.get(key).split(",");
            if(fileRule.equals(currentRules[0]))
            {
                String[] currentRulesParts = currentRules[1].split("_");
                if(currentRulesParts[0].equals(RuleType))
                    rules.add(currentRules[1]);
            }
        }
        
        level2Rules =new HashSet(rules);
        return level2Rules;
    }
    
    public static Set<String> findAllRules3(String eachFileInTran,AssocRules ar)
    {
        System.out.println("Incoming Api: "+eachFileInTran+" Rule Size: "+ar.assocRules.size());
        Set<String> relatedRules = new HashSet();
        ArrayList<String> fileFromRules = new ArrayList<String>();
        Set<String> ruleFromRightSide = new HashSet();
        String[] currentRules;
        for(Integer key:ar.assocRules.keySet())
        {
            currentRules = ar.assocRules.get(key).split(",");
            if(eachFileInTran.equals(currentRules[0]) /*&& ar.rulesWithConfidence.get(key)> 0.75*/)
            {
                String[] currentRulesParts = currentRules[1].split("_");
                if(currentRulesParts[0].equals("P"))
                    fileFromRules.add(currentRules[1]);
                    ruleFromRightSide = findAllRulesL2A(currentRules[1],ar,"P");
            }
            
        }
        relatedRules = new HashSet(fileFromRules);
        relatedRules.addAll(ruleFromRightSide);
        return relatedRules;
    }
    
    public static Set<String> findAllRules4(String eachFileInTran,AssocRules ar)
    {
        Set<String> relatedRules = new HashSet();
        ArrayList<String> fileFromRules = new ArrayList<String>();
        Set<String> ruleFromRightSide = new HashSet();
        String[] currentRules;
        for(Integer key:ar.assocRules.keySet())
        {
            currentRules = ar.assocRules.get(key).split(",");
            if(eachFileInTran.equals(currentRules[0]) /*&& ar.rulesWithConfidence.get(key)> 0.75*/)
            {
                String[] currentRulesParts = currentRules[1].split("_");
                if(currentRulesParts[0].equals("A"))
                    fileFromRules.add(currentRules[1]);
                    ruleFromRightSide = findAllRulesL2A(currentRules[1],ar,"A");
            }
            
        }
        relatedRules = new HashSet(fileFromRules);
        relatedRules.addAll(ruleFromRightSide);
        return relatedRules;
    }
    
    public static String findViolatedRules(String transaction,HashMap<Integer,String> arules,HashMap<Integer,Float> arulesWithCon)
    {
        String missingRules="";
        String[] tranParts = transaction.split(",");
        String[] currentRules;
        Set<String> tranPartString = new HashSet<String>(Arrays.asList(tranParts));
        for(String rules:tranParts)
        {
            
            for(Integer aruleId:arules.keySet())
            {
                currentRules = arules.get(aruleId).split(",");
                if(rules.equals(currentRules[0]) && arulesWithCon.get(aruleId) > 0.9)
                {
                    if(!tranPartString.contains(currentRules[1]))
                    {
                        if(missingRules.length() < 1)
                            missingRules = currentRules[0]+"=>"+currentRules[1];
                        else
                            missingRules = missingRules+","+currentRules[0]+"=>"+currentRules[1];
                    }
                }
            }
        }
        System.out.println("Missed rules FP: "+missingRules);
        return missingRules;
    }
    
    public static String findViolatedRulesFN(String transaction,HashMap<Integer,String> arules,HashMap<Integer,Float> arulesWithCon)
    {
        String missingRules="";
        String[] tranParts = transaction.split(",");
        String[] currentRules;
        Set<String> tranPartString = new HashSet<String>(Arrays.asList(tranParts));
        //System.out.println("Real Tran: "+transaction);
        for(String rules:tranParts)
        {
            
            for(Integer aruleId:arules.keySet())
            {
                currentRules = arules.get(aruleId).split(",");
                if(rules.equals(currentRules[1]) && arulesWithCon.get(aruleId) > 0.9)
                {
                    if(!tranPartString.contains(currentRules[0]))
                    {
                        if(missingRules.length() < 1)
                            missingRules = currentRules[0]+"=>"+currentRules[1];
                        else
                            missingRules = missingRules+","+currentRules[0]+"=>"+currentRules[1];
                    }
                }
            }
        }
        System.out.println("Missed rules FN: "+missingRules);
        return missingRules;
    }
    
    public static void RecallAndPrecisionDataWriting(HashMap<String,Float> recall, HashMap<String,Float> precision,HashMap<String,Float> recallMax, HashMap<String,Float> precisionMax,HashMap<String,Float> recallMin, HashMap<String,Float> precisionMin,String resultFilePath)
    {
        float totalRecall=0;
        float totalPrecision=0;
        float totalRecallNonEmpty = 0;
        float totalPrecisionNonEmpty = 0;
        float totalRecallMax=0;
        float totalPrecisionMax=0;
        float totalRecallNonEmptyMax = 0;
        float totalPrecisionNonEmptyMax = 0;
        float totalRecallMin=0;
        float totalPrecisionMin=0;
        float totalRecallNonEmptyMin = 0;
        float totalPrecisionNonEmptyMin = 0;
        int nonEmptyRecallCount = 0;
        int nonEmptyPrecisionCount = 0;
        BufferedWriter writeResult;
        
         try
         {
            writeResult = new BufferedWriter(new FileWriter(resultFilePath));
            writeResult.append("Avg Recall\tAvg Precision\tMax Recall\tMax Precision\tMin Recall\tMin Precision");
            writeResult.newLine();
            for(String eachItem:recall.keySet())
            {
                //System.out.println("Recall: "+recall.get(eachItem).toString());
                totalRecall += recall.get(eachItem);
                totalPrecision += precision.get(eachItem);
                totalRecallMax += recallMax.get(eachItem);
                totalPrecisionMax += precisionMax.get(eachItem);
                totalRecallMin += recallMin.get(eachItem);
                totalPrecisionMin += precisionMin.get(eachItem);
                if(recall.get(eachItem) != 0)
                {
                    totalRecallNonEmpty += recall.get(eachItem);
                    nonEmptyRecallCount++;
                }
                
                if(precision.get(eachItem) != 0)
                {
                    totalPrecisionNonEmpty += precision.get(eachItem);
                    nonEmptyPrecisionCount++;
                }
                
                if(recallMax.get(eachItem) != 0)
                {
                    totalRecallNonEmptyMax += recallMax.get(eachItem);
                }
                
                if(precisionMax.get(eachItem) != 0)
                {
                    totalPrecisionNonEmptyMax += precisionMax.get(eachItem);
                }
                
                if(recallMin.get(eachItem) != 0)
                {
                    totalRecallNonEmptyMin += recallMin.get(eachItem);
                }
                
                if(precisionMin.get(eachItem) != 0)
                {
                    totalPrecisionNonEmptyMin += precisionMin.get(eachItem);
                }

                writeResult.append(eachItem+"\t"+recall.get(eachItem)+"\t"+precision.get(eachItem)+"\t"+recallMax.get(eachItem)+"\t"+precisionMax.get(eachItem)+"\t"+recallMin.get(eachItem)+"\t"+precisionMin.get(eachItem));
                writeResult.newLine();
            }
            
            System.out.println("Avg Recall: "+ totalRecall/recall.size()+" Avg Precision: "+totalPrecision/precision.size()
                                +" Avg Recall with Max: "+totalRecallMax/recallMax.size()+" Avg Precision with Max: "+totalPrecisionMax/precisionMax.size()
                                    + " Avg Recall with Min: "+ totalRecallMin/recallMin.size()+" Avg Precision with Min: "+ totalPrecisionMin/precisionMin.size());
            
            System.out.println("Avg Recall non empty: "+ totalRecallNonEmpty/nonEmptyRecallCount+" Avg Precision non empty: "+totalPrecisionNonEmpty/nonEmptyPrecisionCount
                                   +"Avg Recall non empty max: "+totalRecallNonEmptyMax/nonEmptyRecallCount +" Avg Precision non empty max: "+totalPrecisionNonEmptyMax/nonEmptyPrecisionCount
                                        +"Avg Recall non empty min: "+ totalRecallNonEmptyMin/nonEmptyRecallCount +" Avg Precision non empty min: "+ totalPrecisionNonEmptyMin/nonEmptyPrecisionCount);
            
           System.out.println("AvgRecall nonempty count: "+ nonEmptyRecallCount +" AvgPrecision nonempty count: "+ nonEmptyPrecisionCount);
//                                   +"AvgRecall nonempty max count: "+ nonEmptyRecallMaxCount +" AvgPrecision nonempty max count: "+ nonEmptyPrecisionMaxCount
//                                        +"AvgRecall nonempty min count: "+ nonEmptyRecallMinCount +" AvgPrecision nonempty min count: "+ nonEmptyPrecisionMinCount);
            writeResult.append("Avg Recall: "+ totalRecall/recall.size()+" Avg Precision: "+totalPrecision/precision.size()
                                +" Avg Recall with Max: "+totalRecallMax/recallMax.size()+" Avg Precision with Max: "+totalPrecisionMax/precisionMax.size()
                                    + " Avg Recall with Min: "+ totalRecallMin/recallMin.size()+" Avg Precision with Min: "+ totalPrecisionMin/precisionMin.size());
            writeResult.newLine();
            writeResult.append("Avg Recall non empty: "+ totalRecallNonEmpty/nonEmptyRecallCount+" Avg Precision non empty: "+totalPrecisionNonEmpty/nonEmptyPrecisionCount
                                   +"Avg Recall non empty max: "+totalRecallNonEmptyMax/nonEmptyRecallCount +" Avg Precision non empty max: "+totalPrecisionNonEmptyMax/nonEmptyPrecisionCount
                                        +"Avg Recall non empty min: "+ totalRecallNonEmptyMin/nonEmptyRecallCount +" Avg Precision non empty min: "+ totalPrecisionNonEmptyMin/nonEmptyPrecisionCount);
            writeResult.newLine();
            writeResult.append("AvgRecall nonempty count: "+ nonEmptyRecallCount +" AvgPrecision nonempty count: "+ nonEmptyPrecisionCount);
            writeResult.close();
         }
         catch(Exception e)
         {
             e.printStackTrace();
         }
        
        
    }
    
    public static void RecallAndPrecisionDataWriting2(HashMap<String,Float> recall, HashMap<String,Float> precision,HashMap<String,Float> recallMax, HashMap<String,Float> precisionMax,HashMap<String,Float> recallMin, HashMap<String,Float> precisionMin, HashMap<String,Float> recallOverall, HashMap<String,Float> precisionOverall,String resultFilePath)
    {
        float totalRecall=0;
        float totalPrecision=0;
        float totalRecallNonEmpty = 0;
        float totalPrecisionNonEmpty = 0;
        float totalRecallMax=0;
        float totalPrecisionMax=0;
        float totalRecallNonEmptyMax = 0;
        float totalPrecisionNonEmptyMax = 0;
        float totalRecallMin=0;
        float totalPrecisionMin=0;
        float totalRecallNonEmptyMin = 0;
        float totalPrecisionNonEmptyMin = 0;
        float totalRecallOverall=0;
        float totalPrecisionOverall=0;
        float totalRecallOverallNonEmpty = 0;
        float totalPrecisionOverallNonEmpty =0;
        int nonEmptyRecallCount = 0;
        int nonEmptyPrecisionCount = 0;
        int nonEmptyRecallCountOverall =0;
        int nonEmptyPrecisionCountOverall = 0;
        BufferedWriter writeResult;
        
         try
         {
            writeResult = new BufferedWriter(new FileWriter(resultFilePath));
            writeResult.append("Avg Recall\tAvg Precision\tMax Recall\tMax Precision\tMin Recall\tMin Precision");
            writeResult.newLine();
            for(String eachItem:recall.keySet())
            {
                //System.out.println("Recall: "+recallOverall.get(eachItem)+"Precision: "+precisionOverall.get(eachItem));
                totalRecall += recall.get(eachItem);
                totalPrecision += precision.get(eachItem);
                totalRecallMax += recallMax.get(eachItem);
                totalPrecisionMax += precisionMax.get(eachItem);
                totalRecallMin += recallMin.get(eachItem);
                totalPrecisionMin += precisionMin.get(eachItem);
                totalRecallOverall += recallOverall.get(eachItem);
                totalPrecisionOverall += precisionOverall.get(eachItem);
                if(recall.get(eachItem) != 0)
                {
                    totalRecallNonEmpty += recall.get(eachItem);
                    nonEmptyRecallCount++;
                }
                
                if(precision.get(eachItem) != 0)
                {
                    totalPrecisionNonEmpty += precision.get(eachItem);
                    nonEmptyPrecisionCount++;
                }
                
                if(recallMax.get(eachItem) != 0)
                {
                    totalRecallNonEmptyMax += recallMax.get(eachItem);
                }
                
                if(precisionMax.get(eachItem) != 0)
                {
                    totalPrecisionNonEmptyMax += precisionMax.get(eachItem);
                }
                
                if(recallMin.get(eachItem) != 0)
                {
                    totalRecallNonEmptyMin += recallMin.get(eachItem);
                }
                
                if(precisionMin.get(eachItem) != 0)
                {
                    totalPrecisionNonEmptyMin += precisionMin.get(eachItem);
                }
                if(recallOverall.get(eachItem)!= 0)
                {
                    totalRecallOverallNonEmpty += recallOverall.get(eachItem);
                    nonEmptyRecallCountOverall++;
                }
                if(precisionOverall.get(eachItem)!= 0)
                {
                    totalPrecisionOverallNonEmpty += precisionOverall.get(eachItem);
                    nonEmptyPrecisionCountOverall++;
                }

                writeResult.append(eachItem+"\t"+recall.get(eachItem)+"\t"+precision.get(eachItem)+"\t"+recallMax.get(eachItem)+"\t"+precisionMax.get(eachItem)+"\t"+recallMin.get(eachItem)+"\t"+precisionMin.get(eachItem)+"\t"+recallOverall.get(eachItem)+"\t"+precisionOverall.get(eachItem));
                writeResult.newLine();
            }
            
            System.out.println("Avg Recall: "+ totalRecall/recall.size()+" Avg Precision: "+totalPrecision/precision.size()
                                +" Avg Recall with Max: "+totalRecallMax/recallMax.size()+" Avg Precision with Max: "+totalPrecisionMax/precisionMax.size()
                                    + " Avg Recall with Min: "+ totalRecallMin/recallMin.size()+" Avg Precision with Min: "+ totalPrecisionMin/precisionMin.size()
                                        +"Avg Recall Overall: "+totalRecallOverall/recallOverall.size()+"Avg Precision Overall: "+totalPrecisionOverall/precisionOverall.size());
            
            System.out.println("Avg Recall non empty: "+ totalRecallNonEmpty/nonEmptyRecallCount+" Avg Precision non empty: "+totalPrecisionNonEmpty/nonEmptyPrecisionCount
                                   +"Avg Recall non empty max: "+totalRecallNonEmptyMax/nonEmptyRecallCount +" Avg Precision non empty max: "+totalPrecisionNonEmptyMax/nonEmptyPrecisionCount
                                        +"Avg Recall non empty min: "+ totalRecallNonEmptyMin/nonEmptyRecallCount +" Avg Precision non empty min: "+ totalPrecisionNonEmptyMin/nonEmptyPrecisionCount
                                            +"Avg Recall Overall non empty: "+ totalRecallOverallNonEmpty/nonEmptyRecallCountOverall+" Avg Precision Overall non empty: "+ totalPrecisionOverallNonEmpty/nonEmptyPrecisionCountOverall);
            
           System.out.println("AvgRecall nonempty count: "+ nonEmptyRecallCount +" AvgPrecision nonempty count: "+ nonEmptyPrecisionCount);
//                                   +"AvgRecall nonempty max count: "+ nonEmptyRecallMaxCount +" AvgPrecision nonempty max count: "+ nonEmptyPrecisionMaxCount
//                                        +"AvgRecall nonempty min count: "+ nonEmptyRecallMinCount +" AvgPrecision nonempty min count: "+ nonEmptyPrecisionMinCount);
            writeResult.append("Avg Recall: "+ totalRecall/recall.size()+" Avg Precision: "+totalPrecision/precision.size()
                                +" Avg Recall with Max: "+totalRecallMax/recallMax.size()+" Avg Precision with Max: "+totalPrecisionMax/precisionMax.size()
                                    + " Avg Recall with Min: "+ totalRecallMin/recallMin.size()+" Avg Precision with Min: "+ totalPrecisionMin/precisionMin.size()
                                        +"Avg Recall Overall: "+totalRecallOverall/recallOverall.size()+"Avg Precision Overall: "+totalPrecisionOverall/precisionOverall.size());
            writeResult.newLine();
            writeResult.append("Avg Recall non empty: "+ totalRecallNonEmpty/nonEmptyRecallCount+" Avg Precision non empty: "+totalPrecisionNonEmpty/nonEmptyPrecisionCount
                                   +"Avg Recall non empty max: "+totalRecallNonEmptyMax/nonEmptyRecallCount +" Avg Precision non empty max: "+totalPrecisionNonEmptyMax/nonEmptyPrecisionCount
                                        +"Avg Recall non empty min: "+ totalRecallNonEmptyMin/nonEmptyRecallCount +" Avg Precision non empty min: "+ totalPrecisionNonEmptyMin/nonEmptyPrecisionCount
                                            +"Avg Recall Overall non empty: "+ totalRecallOverallNonEmpty/nonEmptyRecallCountOverall+" Avg Precision Overall non empty: "+ totalPrecisionOverallNonEmpty/nonEmptyPrecisionCountOverall);
            writeResult.newLine();
            writeResult.append("AvgRecall nonempty count: "+ nonEmptyRecallCount +" AvgPrecision nonempty count: "+ nonEmptyPrecisionCount);
            writeResult.close();
         }
         catch(Exception e)
         {
             e.printStackTrace();
         }
        
        
    }
    
    public static void RecallPrecisionCalc_Method_A_P_withAndroGuard(AssocRules assocRules,TestSet testSet,String resultFilePath,String violatedRulesFile,AndroGuardRules agRules)
    {
        String eachTransaction;
        String[] eachTransactionParts;
        Set<String> predictedTransaction = new HashSet(); 
        Set<String> predictedTransactionAll = new HashSet();
        Set<String> intersectionTransactionAll = new HashSet();
        Set<String> realTransaction = new HashSet();
        Set<String> realTransaction_Permission = new HashSet();
        Set<String> realTransaction_API = new HashSet();
        Set<String> intersectionTransaction = new HashSet();
        int count=0;
        float eachRecall;
        float eachPrecision;
        float RecallSumm=0;
        float PrecisionSum=0;
        float avgRecallPerTran;
        float avgPrecisionPerTran;
        float overallRecallPerTran;
        float overallPrecisionPerTran;
        float MaxRecall = 0;
        float MaxPricision = 0;
        float MinRecall = 1;
        float MinPrecision = 1;
        HashMap<String,Float> Recall = new HashMap<String,Float>();
        HashMap<String,Float> Precision = new HashMap<String,Float>();
        HashMap<String,Float> RecallMax = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMax = new HashMap<String,Float>();
        HashMap<String,Float> RecallMin = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMin = new HashMap<String,Float>();
        HashMap<String,Float> RecallOverall = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionOverall = new HashMap<String,Float>();
        HashMap<String,String> ViolatedRules = new HashMap<String,String>();
        String RulesOfViolation;
        
        
        for(String key:testSet.commitHistory.keySet())
        {
            if(count < -1)
            {
                break;
            }
            else
            {
                realTransaction.clear();
                realTransaction_API.clear();
                realTransaction_Permission.clear();
                eachTransaction = testSet.commitHistory.get(key);
                //RulesOfViolation = findViolatedRules(eachTransaction,assocRules.assocRules,assocRules.rulesWithConfidence);
                //ViolatedRules.put(key+" : "+eachTransaction, RulesOfViolation);
                eachTransactionParts = testSet.commitHistory.get(key).split(",");
                
                for(String eachPart:eachTransactionParts)
                {
                    String[] PreFixes= eachPart.split("_");
                    if(PreFixes[0].equals("P"))
                        realTransaction_Permission.add(eachPart);
                    else
                        realTransaction_API.add(eachPart);
                }
                //System.out.println("Number of APIS: "+realTransaction_API.size());
                Collections.addAll(realTransaction, eachTransactionParts);
                RecallSumm   = 0;
                PrecisionSum = 0;
                MaxRecall = 0;
                MaxPricision = 0;
                MinRecall = 1;
                MinPrecision = 1;
                predictedTransactionAll.clear();
                intersectionTransactionAll.clear();
                int count2=0;
                for(String eachFileInTran:realTransaction_API)
                {
                    count2++;
                    intersectionTransaction.clear();
                    predictedTransaction.clear();
                    
                    predictedTransaction.addAll(findAndroGuardRules(eachFileInTran,agRules));  // if-else need to be added
                    predictedTransaction.addAll(findAllRules3(eachFileInTran,assocRules));
                    intersectionTransaction.addAll(predictedTransaction);
                    intersectionTransaction.retainAll(realTransaction_Permission);
                   // System.out.println(realTransaction_Permission);s
                    
//                    if(intersectionTransaction.size() < 1)
//                    {
//                        predictedTransaction = findAllRules3(eachFileInTran,assocRules);
//                        intersectionTransaction.addAll(predictedTransaction);
//                        intersectionTransaction.retainAll(realTransaction_Permission);
//                        //System.out.println("From Unfiltered");
//                    }
//                    else
//                    {
//                        //System.out.println("From AndroGuard");
//                    }
                    
                    predictedTransactionAll.addAll(predictedTransaction);
                    eachRecall = (float) intersectionTransaction.size()/(float) realTransaction_Permission.size();
                    if(predictedTransaction.size() > 0)
                        eachPrecision = (float) intersectionTransaction.size()/(float) predictedTransaction.size();
                    else
                        eachPrecision=0;
                    
                    RecallSumm += eachRecall;
                    PrecisionSum += eachPrecision;
                    
                    if(MaxRecall < eachRecall)
                        MaxRecall = eachRecall;
                    if(MaxPricision < eachPrecision)
                        MaxPricision = eachPrecision;
                    if(eachRecall < MinRecall)
                        MinRecall = eachRecall;
                    if(eachPrecision < MinPrecision)
                        MinPrecision = eachPrecision;
                    System.out.println(count2+" Transaction no: "+key+"Recall: "+eachRecall+"Precision: "+eachPrecision);
                    System.out.println(count2+" Current trasaction: "+eachTransaction);
                    System.out.println(count2+" Number of Real Transaction:"+realTransaction.size()+"Number of Predicted Transaction:"+predictedTransaction.size()+" Intersection Set size: "+intersectionTransaction.size());
                }
                intersectionTransactionAll.addAll(predictedTransactionAll);
                intersectionTransactionAll.retainAll(realTransaction);
                //System.out.println(predictedTransactionAll.size()+" "+intersectionTransactionAll.size());
                avgRecallPerTran = RecallSumm/(float) realTransaction_API.size();
                avgPrecisionPerTran = PrecisionSum/(float) realTransaction_API.size();
                
                overallRecallPerTran = intersectionTransactionAll.size()/ (float) realTransaction_Permission.size();
                if(predictedTransactionAll.size() > 0)
                    overallPrecisionPerTran = (float) intersectionTransactionAll.size()/(float) predictedTransactionAll.size();
                else
                   overallPrecisionPerTran=0;
                
//                if(eachTransactionParts.length<2)
//                {
//                    System.out.println("Current trasaction: "+eachTransaction);
//                    System.out.println(predictedTransaction.toString());
//                }    
                    
                //System.out.println("Number of Predicted Transaction:"+predictedTransaction.size());
                
                Recall.put(key, avgRecallPerTran);
                Precision.put(key, avgPrecisionPerTran);
                RecallMax.put(key, MaxRecall);
                PrecisionMax.put(key, MaxPricision);
                RecallMin.put(key, MinRecall);
                PrecisionMin.put(key, MinPrecision);
                RecallOverall.put(key,overallRecallPerTran);
                PrecisionOverall.put(key,overallPrecisionPerTran);
                
            }
            count++;
        }
        
        //System.out.println("Recall size:" + Recall.size());
        //System.out.println("Precision size:" + Precision.size());
        //RecallAndPrecisionDataWriting(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,resultFilePath);
        RecallAndPrecisionDataWriting2(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,RecallOverall,PrecisionOverall,resultFilePath);
        
//        try
//        {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(violatedRulesFile));
//            for(String trans:ViolatedRules.keySet())
//            {
//                if(ViolatedRules.get(trans).toString().length()>0)
//                {
//                    bw.append(trans+" ---- "+ViolatedRules.get(trans));
//                    bw.newLine();
//                }
//            }
//            bw.close();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }
    
    public static Set<String> findAndroGuardRules(String givenAPI, AndroGuardRules agRules)
    {
        System.out.println("Looking in AndroGuard: "+givenAPI);
        String currApi = givenAPI;
        System.out.println("Lets See: "+currApi);
        Set<String> foundPermission = new HashSet<String>();
        foundPermission = agRules.ApiToPermissionRules.get(currApi);
        if(foundPermission != null)
        {
            System.out.println("Permission Size: "+foundPermission.size());
            return foundPermission;
        }
        else
        {
            System.out.println("Permission Size 0");
            foundPermission = new HashSet<String>();
            return foundPermission;
        }
    }
    
    public static Set<String> findPscoutRules(String givenAPI, PscoutRules pRules)
    {
        System.out.println("Looking in Pscout: "+givenAPI);
        String currApi = givenAPI;
        System.out.println("Lets See: "+currApi);
        Set<String> foundPermission = new HashSet<String>();
        foundPermission = pRules.ApiToPermissionRules.get(currApi);
        if(foundPermission != null)
        {
            System.out.println("Permission Size: "+foundPermission.size());
            return foundPermission;
        }
        else
        {
            System.out.println("Permission Size 0");
            foundPermission = new HashSet<String>();
            return foundPermission;
        }
    }
    
    public static void RecallPrecisionCalc_Method_A_P_withAndroGuardandPscout(AssocRules assocRules,TestSet testSet,String resultFilePath,String violatedRulesFile,AndroGuardRules agRules, PscoutRules pRules)
    {
        String eachTransaction;
        String[] eachTransactionParts;
        Set<String> predictedTransaction = new HashSet(); 
        Set<String> predictedTransactionAll = new HashSet();
        Set<String> intersectionTransactionAll = new HashSet();
        Set<String> realTransaction = new HashSet();
        Set<String> realTransaction_Permission = new HashSet();
        Set<String> realTransaction_API = new HashSet();
        Set<String> intersectionTransaction = new HashSet();
        int count=0;
        float eachRecall;
        float eachPrecision;
        float RecallSumm=0;
        float PrecisionSum=0;
        float avgRecallPerTran;
        float avgPrecisionPerTran;
        float overallRecallPerTran;
        float overallPrecisionPerTran;
        float MaxRecall = 0;
        float MaxPricision = 0;
        float MinRecall = 1;
        float MinPrecision = 1;
        HashMap<String,Float> Recall = new HashMap<String,Float>();
        HashMap<String,Float> Precision = new HashMap<String,Float>();
        HashMap<String,Float> RecallMax = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMax = new HashMap<String,Float>();
        HashMap<String,Float> RecallMin = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionMin = new HashMap<String,Float>();
        HashMap<String,Float> RecallOverall = new HashMap<String,Float>();
        HashMap<String,Float> PrecisionOverall = new HashMap<String,Float>();
        HashMap<String,String> ViolatedRules = new HashMap<String,String>();
        String RulesOfViolation;
        String numberOfPermissions = "/home/yasser/Pscout/NumberOfRules";
        BufferedWriter PermWriter;
        
        try
        {
        
            PermWriter = new BufferedWriter(new FileWriter(numberOfPermissions));
            for(String key:testSet.commitHistory.keySet())
            {
                if(count < -1)
                {
                    break;
                }
                else
                {
                    realTransaction.clear();
                    realTransaction_API.clear();
                    realTransaction_Permission.clear();
                    eachTransaction = testSet.commitHistory.get(key);
                    //RulesOfViolation = findViolatedRules(eachTransaction,assocRules.assocRules,assocRules.rulesWithConfidence);
                    //ViolatedRules.put(key+" : "+eachTransaction, RulesOfViolation);
                    eachTransactionParts = testSet.commitHistory.get(key).split(",");

                    for(String eachPart:eachTransactionParts)
                    {
                        String[] PreFixes= eachPart.split("_");
                        if(PreFixes[0].equals("P"))
                            realTransaction_Permission.add(eachPart);
                        else
                            realTransaction_API.add(eachPart);
                    }
                    //System.out.println("Number of APIS: "+realTransaction_API.size());
                    Collections.addAll(realTransaction, eachTransactionParts);
                    RecallSumm   = 0;
                    PrecisionSum = 0;
                    MaxRecall = 0;
                    MaxPricision = 0;
                    MinRecall = 1;
                    MinPrecision = 1;
                    predictedTransactionAll.clear();
                    intersectionTransactionAll.clear();
                    int count2=0;
                    for(String eachFileInTran:realTransaction_API)
                    {
                        count2++;
                        intersectionTransaction.clear();
                        predictedTransaction.clear();

                       // predictedTransaction.addAll(findAndroGuardRules(eachFileInTran,agRules));  // if-else need to be added
                        //predictedTransaction.addAll(findPscoutRules(eachFileInTran,pRules));
                        predictedTransaction.addAll(findAllRules3(eachFileInTran,assocRules));
                        intersectionTransaction.addAll(predictedTransaction);
                        intersectionTransaction.retainAll(realTransaction_Permission);
                       // System.out.println(realTransaction_Permission);s

    //                    if(intersectionTransaction.size() < 1)
    //                    {
    //                        predictedTransaction = findAllRules3(eachFileInTran,assocRules);
    //                        intersectionTransaction.addAll(predictedTransaction);
    //                        intersectionTransaction.retainAll(realTransaction_Permission);
    //                        //System.out.println("From Unfiltered");
    //                    }
    //                    else
    //                    {
    //                        //System.out.println("From AndroGuard");
    //                    }

                        predictedTransactionAll.addAll(predictedTransaction);
                        eachRecall = (float) intersectionTransaction.size()/(float) realTransaction_Permission.size();
                        if(predictedTransaction.size() > 0)
                            eachPrecision = (float) intersectionTransaction.size()/(float) predictedTransaction.size();
                        else
                            eachPrecision=0;

                        RecallSumm += eachRecall;
                        PrecisionSum += eachPrecision;

                        if(MaxRecall < eachRecall)
                            MaxRecall = eachRecall;
                        if(MaxPricision < eachPrecision)
                            MaxPricision = eachPrecision;
                        if(eachRecall < MinRecall)
                            MinRecall = eachRecall;
                        if(eachPrecision < MinPrecision)
                            MinPrecision = eachPrecision;
                        System.out.println(count2+" Transaction no: "+key+"Recall: "+eachRecall+"Precision: "+eachPrecision);
                        System.out.println(count2+" Current trasaction: "+eachTransaction);
                        System.out.println(count2+" Number of Real Transaction:"+realTransaction.size()+"Number of Predicted Transaction:"+predictedTransaction.size()+" Intersection Set size: "+intersectionTransaction.size());
                    }
                    
                    
                    intersectionTransactionAll.addAll(predictedTransactionAll);
                    intersectionTransactionAll.retainAll(realTransaction);
                    PermWriter.append(realTransaction_Permission.size()+"\t"+predictedTransactionAll.size()+"\t"+intersectionTransactionAll.size());
                    PermWriter.newLine();
                    //System.out.println(predictedTransactionAll.size()+" "+intersectionTransactionAll.size());
                    avgRecallPerTran = RecallSumm/(float) realTransaction_API.size();
                    avgPrecisionPerTran = PrecisionSum/(float) realTransaction_API.size();

                    overallRecallPerTran = intersectionTransactionAll.size()/ (float) realTransaction_Permission.size();
                    if(predictedTransactionAll.size() > 0)
                        overallPrecisionPerTran = (float) intersectionTransactionAll.size()/(float) predictedTransactionAll.size();
                    else
                       overallPrecisionPerTran=0;

    //                if(eachTransactionParts.length<2)
    //                {
    //                    System.out.println("Current trasaction: "+eachTransaction);
    //                    System.out.println(predictedTransaction.toString());
    //                }    

                    //System.out.println("Number of Predicted Transaction:"+predictedTransaction.size());

                    Recall.put(key, avgRecallPerTran);
                    Precision.put(key, avgPrecisionPerTran);
                    RecallMax.put(key, MaxRecall);
                    PrecisionMax.put(key, MaxPricision);
                    RecallMin.put(key, MinRecall);
                    PrecisionMin.put(key, MinPrecision);
                    RecallOverall.put(key,overallRecallPerTran);
                    PrecisionOverall.put(key,overallPrecisionPerTran);

                }
                count++;
            }
            PermWriter.close();
            //System.out.println("Recall size:" + Recall.size());
            //System.out.println("Precision size:" + Precision.size());
            //RecallAndPrecisionDataWriting(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,resultFilePath);
            RecallAndPrecisionDataWriting2(Recall,Precision,RecallMax,PrecisionMax,RecallMin,PrecisionMin,RecallOverall,PrecisionOverall,resultFilePath);

    //        try
    //        {
    //            BufferedWriter bw = new BufferedWriter(new FileWriter(violatedRulesFile));
    //            for(String trans:ViolatedRules.keySet())
    //            {
    //                if(ViolatedRules.get(trans).toString().length()>0)
    //                {
    //                    bw.append(trans+" ---- "+ViolatedRules.get(trans));
    //                    bw.newLine();
    //                }
    //            }
    //            bw.close();
    //        }
    //        catch(Exception e)
    //        {
    //            e.printStackTrace();
    //        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

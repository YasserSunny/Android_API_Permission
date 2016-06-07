/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package validation;

/**
 *
 * @author yasser
 */
public class Validation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int loop;
        String assocRules;
        String FileIds;
        String TestSet;
        String resultFile;
        String violatedRules;
        
        for(loop = 1; loop<=1;loop++)
        {
            /*assocRules = "/media/Uncharted/ApMiner_GooglePlay/outputPlay1";
            FileIds = "/home/yasser/AndroidNewAnalysis/ItemList/FileList1";
            TestSet = "/home/yasser/AndroidNewAnalysis/TestSet/TestSet4";
            resultFile = "/home/yasser/AndroidNewAnalysis/Result/Result4";
            violatedRules = "/home/yasser/AndroidNewAnalysis/ViolatedRules/VioRules1sssss";
            String androGuardFile = "/home/yasser/Pscout/androguardApiToPerm";
            String PscoutFile = "/home/yasser/Pscout/ApiToPerm";*/
            
            assocRules = "E:\\Data\\Validation_FilterPrime\\filterPrimeRules";
            FileIds = "E:\\Data\\Validation_FilterPrime\\ItemList_FilterPrime";
            TestSet = "E:\\Data\\TestSet\\API_PER_SEQ\\testingSet9";
            resultFile = "E:\\Data\\Validation_FilterPrime\\Result2_cross_Google_Fdroid";
            violatedRules = "E:\\Data\\Validation_FilterPrime\\VioRules1";
            String androGuardFile = "E:\\Data\\Validation_FilterPrime\\androguardApiToPerm";
            String PscoutFile = "E:\\Data\\Validation_FilterPrime\\ApiToPerm";
            String numOfPerm="E:\\Data\\Validation_FilterPrime\\NumOfPerm";
        
            AndroGuardRules androGRules = new AndroGuardRules(androGuardFile);
            PscoutRules pscoutRules = new PscoutRules(PscoutFile);
            androGRules.LoadRules();
            pscoutRules.LoadRules();
            AssocRules ar = new AssocRules(assocRules,FileIds);
            TestSet testSet = new TestSet(TestSet);

            System.out.println("Current File: "+assocRules);
            System.out.println("Number of rules : "+ar.assocRules.size()+"\nNumber of Transaction in Test Set: "+testSet.commitHistory.size());
            
//            for(String currApi:pscoutRules.ApiToPermissionRules.keySet())
//            {
//                System.out.println(currApi+"=>"+pscoutRules.ApiToPermissionRules.get(currApi));
//            }
            //Analytics.RecallPrecisionCalc_Method2(ar, testSet, resultFile, violatedRules);
//            for(Integer Id:ar.assocRules.keySet())
//            {
//                System.out.println(Id+" "+ar.assocRules.get(Id));
//            }
            
           //Analytics.RecallPrecisionCalc_Method_A_P_withAndroGuard(ar, testSet, resultFile, violatedRules,androGRules);
            //Analytics.RecallPrecisionCalc_Method_P_A(ar, testSet, resultFile, violatedRules);
           // Analytics.RecallPrecisionCalc_Method_A_P_withAndroGuardandPscout(ar, testSet, resultFile, violatedRules,androGRules,pscoutRules);
            Analytics.RecallPrecisionCalc_Method_A_P(ar, testSet, resultFile, violatedRules,numOfPerm);
//        for(Integer loopIter:ar.assocRules.keySet())
//            System.out.println(loopIter+" ==> "+ar.assocRules.get(loopIter));
        
//        for(Integer Rules:ar.rulesWithConfidence.keySet())
//            System.out.println(Rules+" ==> "+ar.rulesWithConfidence.get(Rules));
        }
    }
}

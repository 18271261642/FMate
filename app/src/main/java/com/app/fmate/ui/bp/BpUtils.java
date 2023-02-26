package com.app.fmate.ui.bp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin
 * Date 2022/6/10
 */
public class BpUtils {

    public static List<Integer> get48SizeList(){
        List<Integer> lt = new ArrayList<>();
        for(int i = 0;i<48;i++){
            lt.add(0);
        }
        return lt;
    }


    public static List<String> get6SizeList(int startIndex, int endIndex, List<String> sourceList, List<String> originallyList){
        List<String> tmpList = new ArrayList<>();
        tmpList.addAll(sourceList);
        List<String> tmpOriList = originallyList;


        for(int k = endIndex+1;(k<tmpList.size() );k++){
            String v = tmpList.get(k);
            if(tmpOriList.size() < 7){
                tmpOriList.add(v);
            }

        }

        if(tmpOriList.size()<6){
            for(int i = startIndex-1;(i>=0 && i >i-tmpList.size());i--){
                if(tmpOriList.size()<7){
                    tmpOriList.add(0,tmpList.get(i));
                }

            }
        }

        return tmpOriList;
    }


    public static List<Integer> get6IntSizeList(int validLength,int startIndex, int endIndex, List<Integer> sourceList, List<Integer> originallyList){

        for(int k = endIndex+1;(k<sourceList.size() );k++){
            System.out.println("-----排序的="+sourceList.get(k)+" "+sourceList.size());
            if(validLength < 7){
                originallyList.add(sourceList.get(k));
                validLength++;
            }

        }

        if(validLength<6){
            for(int i = startIndex-1;(i>=0 && i >i-originallyList.size());i--){
                if(validLength<7){
                    originallyList.add(0,sourceList.get(i));
                    validLength++;
                }

            }
        }

        return originallyList;
    }
}

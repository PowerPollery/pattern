import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.jumpmind.symmetric.csv.CsvReader;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 情感分析数据集初始化类
 */
public class DataSetIniter {
    private String dataPath;
    private String trainingPath;
    private String testingPath;
    private String stopPath;
    private HashSet<String> stopWords;
    private int linesMax = 10000;
    private int commentIndex = 3;
    private int ratingIndex = 6;
//    private int commentIndex = 7;
//    private int ratingIndex = 2;
    private int commentLengthMin = 50;
    private int commentLengthMax = 10000;
    private String charSet = "gbk";
//    private String charSet = "utf-8";
    private int increment = 1; //增量
    private int initPercent = 10; //所有数据需要初始化数据所占的百分比 (前百分之~)
    private int traningPercent = 70; //初始化数据中训练集所占的百分比 (前百分之~)，剩余为测试集

    public DataSetIniter(String dataPath, String trainingPath, String testingPath, String stopPath){
        this.dataPath = dataPath;
        this.trainingPath = trainingPath;
        this.testingPath = testingPath;
        this.stopPath = stopPath;
    }

    public void init(){
        System.out.println("Initing...");
        try {
            FileWriter trainingWriter = new FileWriter(new File(trainingPath));
            FileWriter testingWriter = new FileWriter(new File(testingPath));

            LineNumberReader LinesReader = new LineNumberReader(new FileReader(new File(dataPath)));
            LinesReader.skip(Long.MAX_VALUE);
            int linesTotal = LinesReader.getLineNumber();
            LinesReader.close();

            linesMax = (int)(1.0*initPercent/100 * linesTotal);

            int traningMax = (int)(1.0*traningPercent/100 * linesMax);

            int lines = 0;
            int traningLines = 0;
            int testingLines = 0;
            int cover = 0;
            //生成CsvReader对象，以，为分隔符，GBK编码方式
            CsvReader reader = new CsvReader(dataPath,',', Charset.forName(charSet));
            reader.readHeaders(); // 跳过表头
            //逐条读取记录，直至读完
            for(int i = 0; reader.readRecord() && i < linesMax; i++){
                cover = i;
                if(i % increment != 0)
                    continue;
                String[] strs = reader.getValues();
                String comment = strs[commentIndex];
                String ratingStr = strs[ratingIndex];
                if(ratingStr == null || comment == null || comment.length() < commentLengthMin || comment.length() > commentLengthMax)
                    continue;

                String clazz = "g";
//                int rating = Integer.valueOf(ratingStr);
                int rating = (int) Math.round(Double.valueOf(ratingStr));
//                if(rating != 1 && rating != 4 && rating != 5)
//                    continue;
                if(rating < 2){
                    clazz = "b";
                }else if(rating > 4){
                    clazz = "g";
                }else
                    continue;

//                if(ratingStr.equals("1"))
//                    clazz = "b";
//                else if (ratingStr.equals("4") || ratingStr.equals("5"))
//                    clazz = "g";
//                else if (ratingStr.equals("2") || ratingStr.equals("3"))
//                    clazz = "b";


                comment = convert(filter(HanLP.segment(comment)));
//                comment = convert(HanLP.extractKeyword(comment, comment.length()));

                if(comment.equals(""))
                    continue;


                if(i <= traningMax) {
                    trainingWriter.write(clazz + " " + comment + "\n");
                    traningLines++;
                }else{
                    testingWriter.write(clazz + " " + comment + "\n");
                    testingLines++;
                }

                lines++;
            }
            trainingWriter.close();
            testingWriter.close();
            System.out.println("lines: "+lines);
            System.out.println("traningLines: "+traningLines);
            System.out.println("testingLines: "+testingLines);
            System.out.println("cover: "+cover);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String[] toArray(Set<String> set){
        String[] arr = new String[set.size()];
        int i = 0;
        for(String s : set){
            arr[i] = s;
            i++;
        }
        return arr;
    }

    //去除重复和停用词
    public Set<String> filter(List<Term> list){
        Set<String> filtered = new HashSet<String>();
        for(Term t:list){
            if(hasNotChineseCharacter(t.word) || stopWords.contains(t.word) || t.word.equals("\n"))
                continue;
            filtered.add(t.word);
        }
        return filtered;
    }

    public void loadStopWords() {
        try {
            stopWords = new HashSet<String>();
            if(stopPath == null){
                return;
            }
            CsvReader reader = new CsvReader(stopPath,',', Charset.forName(charSet));
            reader.readHeaders(); // 跳过表头
            //逐条读取记录，直至读完
            while(reader.readRecord()){
                String str = reader.getValues()[0];
                stopWords.add(str);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String convert(Collection<String> collection){
        String str = "";
        int size = collection.size();
        int i=0;
        for(String s:collection){
            str += s;
            if(i != size-1)
                str += " ";
            i++;
        }
        return str;
    }

    /**
     * 判断一个字符是否非中文
     * */
    public static boolean isNotChineseCharacter(char c) {
        return !(c >= 0x4E00 && c <= 0x9FA5);// 根据字节码判断
    }

    /**
     * 判断一个字符串是否包含非中文
     * */
    public static boolean hasNotChineseCharacter(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (isNotChineseCharacter(c)) return true;// 有一个非中文字符就返回
        }
        return false;
    }

    public void setLinesMax(int linesMax) {
        this.linesMax = linesMax;
    }

    public void setCommentLengthMin(int commentLengthMin) {
        this.commentLengthMin = commentLengthMin;
    }

    public void setCommentLengthMax(int commentLengthMax) {
        this.commentLengthMax = commentLengthMax;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public void setInitPercent(int initPercent) {
        this.initPercent = initPercent;
    }

    public void setTraningPercent(int traningPercent) {
        this.traningPercent = traningPercent;
    }
}

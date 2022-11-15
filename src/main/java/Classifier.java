import org.jumpmind.symmetric.csv.CsvReader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 朴素贝叶斯分类器
 */
public class Classifier {
    private String modelPath;
    private Map<String, Clazz> clazzMap = new HashMap();

    public Classifier(String modelPath){
        this.modelPath = modelPath;
    }

    public void loadModel(){
        System.out.println("Loading model...");
        try {
            CsvReader reader = new CsvReader(modelPath,':', Charset.forName("utf8"));
            while(reader.readRecord()){
                String[] arr = reader.getValues();
                try {
                    if(arr.length == 2){
                        clazzMap.put(arr[0], new Clazz(Integer.valueOf(arr[1])));
                    }else if (arr.length == 3){
                        clazzMap.get(arr[0]).put(arr[1], Integer.valueOf(arr[2]));
                    }
                } catch (NumberFormatException e){
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String predict(String[] instance){
        double maxVxi = Double.NEGATIVE_INFINITY;
        String maxLabel=null;
        int total = 0;
        double Vxi;
        for(Map.Entry<String, Clazz>e:clazzMap.entrySet()) {
            total += e.getValue().count;
        }
        for(Map.Entry<String, Clazz>e:clazzMap.entrySet()) {
            Clazz clazz = e.getValue();
            //计算先验概率
            double prior = 1.0 * clazz.count / total;
            Vxi = prior;
            for (String s:instance){
                //计算后验概率
                Integer num = clazz.get(s);
                num = num == null ? 1 : num + 1; //拉普拉斯平滑
                double prob = 1.0 * num / clazz.count;
                Vxi *= prob;
            }
            if(Vxi > maxVxi){
                maxVxi = Vxi;
                maxLabel = e.getKey();
            }
        }
        return maxLabel;
    }
}

import org.jumpmind.symmetric.csv.CsvReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成训练模型
 */
public class ModelTrainer {
    private String inPath;
    private String outPath;
    private Map<String, Clazz> clazzMap = new HashMap();

    public ModelTrainer(String inPath, String outPath){
        this.inPath = inPath;
        this.outPath = outPath;
    }

    public void train(){
        System.out.println("Training...");
        try {
            CsvReader reader = new CsvReader(inPath,' ', Charset.forName("utf8"));
            while(reader.readRecord()){
                String[] arr = reader.getValues();
                if(clazzMap.containsKey(arr[0])){
                    clazzMap.get(arr[0]).count++;
                }else{
                    clazzMap.put(arr[0], new Clazz());
                }
                Clazz c = clazzMap.get(arr[0]);
                for (int i = 1; i < arr.length; i++){
                    String col = arr[i];
                    if(c.containsKey(col)){
                        c.put(col, c.get(col)+1);
                    }else{
                        c.put(col, 1);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            output();
        }
    }

    private void output(){
        try {
            FileWriter writer = new FileWriter(new File(outPath));
            for(Map.Entry<String, Clazz>e:clazzMap.entrySet()){
                writer.write(e.getKey()+":"+e.getValue().count+"\n");
                for (Map.Entry<String, Integer> en:e.getValue().entrySet()){
                    writer.write(e.getKey()+":"+en.getKey()+":"+en.getValue()+"\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Clazz> getClazzMap(){
        return clazzMap;
    }
}

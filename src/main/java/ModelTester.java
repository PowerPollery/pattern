import org.jumpmind.symmetric.csv.CsvReader;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 测试模型
 */
public class ModelTester {
    private String testingPath;
    private Classifier classifier;
//    private int correct = 0;
//    private int wrong = 0;
    //二分类可采用更加精确的统计 https://zhuanlan.zhihu.com/p/369936908?ivk_sa=1024320u
    private int TP = 0; //正确的正例
    private int FN = 0; //错误的反例
    private int FP = 0; //错误的正例
    private int TN = 0; //正确的反例

    public ModelTester(String testingPath, Classifier classifier){
        this.testingPath = testingPath;
        this.classifier = classifier;
    }

    public double test(){
        System.out.println("Test begin -----");
        try {
            CsvReader reader = new CsvReader(testingPath,' ', Charset.forName("utf8"));
            while (reader.readRecord()){
                String[] arr = reader.getValues();
                String clazz = arr[0];
                String[] words = new String[arr.length-1];
                for(int i = 1; i < arr.length; i++){
                    words[i-1] = arr[i];
                }
                String predict = classifier.predict(words);
                if(clazz.equals("g") && predict.equals("g")){
                    TP++;
                }else if(clazz.equals("g") && predict.equals("b")){
                    FN++;
                }else if(clazz.equals("b") && predict.equals("g")){
                    FP++;
                }else if(clazz.equals("b") && predict.equals("b")){
                    TN++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("correct: "+(TP+TN));
            System.out.println("wrong: "+(FP+FN));
            System.out.println("Accuracy: "+(1.0*(TP+TN)/(TP+TN+FP+FN)));
            System.out.println("Precision: "+(1.0*(TP)/(TP+FP)));
            System.out.println("Recall: "+(1.0*(TP)/(TP+FN)));

            return (1.0*(TP+TN)/(TP+TN+FP+FN));
        }

    }
}

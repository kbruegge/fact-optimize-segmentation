import com.google.common.collect.Lists;
import hexmap.CameraPixel;
import hexmap.FactCameraPixel;
import hexmap.FactPixelMapping;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by kai on 11.02.16.
 */
public class CameraCoordinatesTest {


    /**
     * writes a file called 'matrix.txt' which can be plotted by matplotlib like this:
     * >>>  a = np.loadtxt("matrix.txt")
     * >>>  plt.imshow(a, interpolation='nearest', aspect='auto')
     * >>>  plt.show()
     */
    @Test
    public void imageTest(){
        FactPixelMapping m  = FactPixelMapping.getInstance();
        double step = 0.5;
        File output =  new File("matrix.txt");
        try {
            FileWriter w = new FileWriter(output);
            //BufferedWriter writer = new BufferedWriter(new Writer)
            //walk through rows
            for(double y = -210; y < 210 ; y += step ){
                for  (double x = -210; x < 210; x += step){
                    FactCameraPixel p = m.getPixelBelowCoordinatesInMM(x, y);
                    if (p != null){
                        int id = p.id;
                        w.write(String.valueOf(id));
                        w.write(" ");
                    } else {
                        w.write("nan");
                        w.write(" ");
                    }
                }
                w.write("\n");
            }
            w.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void neighbourTest(){
        FactPixelMapping m  = FactPixelMapping.getInstance();
        double step = 0.5;
        File output =  new File("neighbours.txt");


        CameraPixel first = m.getPixelFromId(393);
        ArrayList<FactCameraPixel> neighbors = m.getNeighboursForPixel(first);

        try {
            FileWriter w = new FileWriter(output);
            //BufferedWriter writer = new BufferedWriter(new Writer)
            //walk through rows
            for(double y = -210; y < 210 ; y += step ){
                for  (double x = -210; x < 210; x += step){
                    FactCameraPixel p = m.getPixelBelowCoordinatesInMM(x, y);
                    if (p != null){
                        int id = p.id;
                        if(neighbors.contains(p)){
                            w.write("2000");
                        } else {
                            w.write(String.valueOf(id));
                        }
                        w.write(" ");
                    } else {
                        w.write("nan");
                        w.write(" ");
                    }
                }
                w.write("\n");
            }
            w.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void showerTest(){
        FactPixelMapping m  = FactPixelMapping.getInstance();
        double step = 0.5;
        File output =  new File("shower.txt");


        ArrayList<Integer> showerIds = Lists.newArrayList(1366, 1289, 1362, 1280, 1330, 1316, 1321, 1360, 1294, 1327, 1361, 1295, 1307, 1292, 1278, 1315, 1365, 1363, 1317, 1341, 1322, 1320, 1314, 1364, 1328, 1319, 1329, 1359, 1331);

        try {
            FileWriter w = new FileWriter(output);
            //BufferedWriter writer = new BufferedWriter(new Writer)
            //walk through rows
            for(double y = -210; y < 210 ; y += step ){
                for  (double x = -210; x < 210; x += step){
                    FactCameraPixel p = m.getPixelBelowCoordinatesInMM(x, y);
                    if (p != null){
                        int id = p.id;
                        if(showerIds.contains(id)){
                            w.write("2000");
                        } else {
                            w.write(String.valueOf(id));
                        }
                        w.write(" ");
                    } else {
                        w.write("nan");
                        w.write(" ");
                    }
                }
                w.write("\n");
            }
            w.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import hexmap.FactCameraPixel;
import hexmap.FactPixelMapping;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by kai on 11.02.16.
 */
public class CameraCoordinatesTest {


    @Test
    public void bla(){
        FactPixelMapping m  = FactPixelMapping.getInstance();
        FactCameraPixel pixel = m.getPixelBelowCoordinatesInMM(-4.0, 49.5);
        System.out.println(pixel);
        assertThat(pixel, is(not(nullValue())));

        pixel = m.getPixelBelowCoordinatesInMM(-8.265, 47.5);
        System.out.println(pixel);
    }

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
}

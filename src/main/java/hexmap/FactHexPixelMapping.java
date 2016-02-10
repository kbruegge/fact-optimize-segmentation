/**
 * 
 */
package hexmap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.Data;

import java.net.URL;

/**
 * This class provides a mapping between different Pixel ids and geometric information specifically for the FACT camera
 *
 * @author Kai
 * 
 */
public class FactHexPixelMapping extends PixelMapping<CameraPixel> {


    static Logger log = LoggerFactory.getLogger(FactHexPixelMapping.class);


    private static FactHexPixelMapping mapping;

    public static FactHexPixelMapping getInstance() {
        if (mapping ==  null){
            String pixelMap = "/hexmap/fact-pixel-map.csv";
            URL mapUrl = FactHexPixelMapping.class.getResource(pixelMap);
            if(mapUrl == null){
                String msg = "Could not load pixel mapping from URL: " + pixelMap + ". Does the file exist?";
                log.error(msg);
                throw new InstantiationError(msg);
            } else {
                mapping = new FactHexPixelMapping(mapUrl);
            }
        }
        return mapping;
    }

    private FactHexPixelMapping(URL mappingURL) {
        if(mappingURL.getFile().isEmpty()){
            throw new RuntimeException("Could not find pixel mapping file");
        }
        load(mappingURL);
    }



    /**
     * Takes a data item containing a row from the mapping file.
     *
     * @return a pixel with the info from the item
     */
    protected CameraPixel getPixelFromCSVItem(Data item){
        int id = (int)(item.get("id"));

        //convert them to millimeter by multiplying with the pixel diameter
        double posX = Double.parseDouble(item.get("pos_X").toString())*9.5;
        double posY = Double.parseDouble(item.get("pos_Y").toString())*9.5;
        return null;

//        int cubeX = col;
//        int cubeZ = row - (col - Math.abs(col % 2))/2;
//
//        int axialQ = cubeX;
//        int axialR = cubeZ;
//
//        //this is how you get the chid from all other numbers which are present in the fact pixelmapping file.
//        int chid = (hardID % 10) + 9 * ((hardID / 10) % 10) + 36 * ((hardID / 100) % 10) + 360 * (hardID / 1000);
//        FactCameraPixel p = new FactCameraPixel(chid, softID, hardID, axialQ , axialR, posX, posY);
//        return p;
    }



    @Override
    public int getNumberOfPixel() {
        return 1440;
    }
}
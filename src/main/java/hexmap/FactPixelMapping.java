/**
 * 
 */
package hexmap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.Data;

import java.io.IOException;
import java.net.URL;

/**
 * This class provides a mapping between different Pixel ids and geometric information specifically for the FACT camera
 *
 * @author Kai
 * 
 */
public class FactPixelMapping extends AbstractPixelMapping<FactCameraPixel> {


    static Logger log = LoggerFactory.getLogger(FactPixelMapping.class);


    private static FactPixelMapping mapping;

    public static FactPixelMapping getInstance() {
        if (mapping ==  null){
            String pixelMap = "/fact_pixel_map.csv";
            URL mapUrl = FactPixelMapping.class.getResource(pixelMap);
            if(mapUrl == null){
                String msg = "Could not load pixel mapping from URL: " + pixelMap + ". Does the file exist?";
                log.error(msg);
                throw new InstantiationError(msg);
            } else {
                mapping = new FactPixelMapping(mapUrl);
            }
        }
        return mapping;
    }

    private FactPixelMapping(URL mappingURL) {
        if(mappingURL.getFile().isEmpty()){
            throw new RuntimeException("Could not find pixel mapping file");
        }
        try {
            load(mappingURL);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not instatiate pixelmap");
        }
    }



    /**
     * Takes a data item containing a row from the mapping file.
     *
     * @return a pixel with the info from the item
     */
    protected FactCameraPixel getPixelFromCSVItem(Data item){
        int id = (int)(item.get("CHID"));
        int softID = (int)(item.get("softID"));
        int hardID = (int)(item.get("hardID"));

        //convert them to millimeter by multiplying with the pixel diameter
        double posX = Double.parseDouble(item.get("pos_X").toString())*9.5;
        double posY = Double.parseDouble(item.get("pos_Y").toString())*9.5;

        int [] ax = getAxialCoordinatesFromRealWorldCoordinatesInMM(posX, posY);

        int axialQ = ax[0];
        int axialR = ax[1];
//
        //this is how you get the chid from all other numbers which are present in the fact pixelmapping file.
        return new FactCameraPixel(id, softID, hardID, axialQ , axialR, posX, posY);
    }



    @Override
    public int getNumberOfPixel() {
        return 1440;
    }
}
import com.google.common.collect.Lists;
import hexmap.CameraPixel;
import hexmap.FactCameraPixel;
import hexmap.FactPixelMapping;
import stream.Data;
import stream.Processor;
import stream.annotations.Parameter;

import java.util.ArrayList;
import java.util.HashSet;


public class TwoLevelCleaning implements Processor{

    @Parameter(required = true, description = "The first threshold to select pixels")
    public double firstThreshold;

    @Parameter(required = true, description = "The second threshold to select neighbouring pixel ")
    public double secondThreshold;


    @Parameter(required = true, description = "Minimum number of pixel a shower can contain")
    public int minNumberOfPixel;

    FactPixelMapping pixelMap = FactPixelMapping.getInstance();

    @Override
    public Data process(Data item) {
        HashSet<CameraPixel> showerPixel = new HashSet<>();
        double[] photons = (double[]) item.get("estimatedPhotons");

        showerPixel = addCorePixel(showerPixel, photons, firstThreshold);
        showerPixel = addNeighboringPixels(showerPixel, photons, secondThreshold);
        showerPixel = removeSmallCluster(showerPixel, minNumberOfPixel);
        item.put("shower", showerPixel.stream().mapToInt(c -> c.id).toArray());
        return item;
    }


    /**
     * Add all pixel with a weight > corePixelThreshold to the showerpixel list.
     * @param showerPixel 'HashSet containing the so far identified shower pixels'
     * @param photons
     * @param corePixelThreshold
     * @return
     */
    public HashSet<CameraPixel> addCorePixel(HashSet<CameraPixel> showerPixel, double[] photons, double corePixelThreshold) {
        for(int pixel = 0; pixel < photons.length; pixel++)
        {
            if (photons[pixel] > corePixelThreshold){
                showerPixel.add(pixelMap.getPixelFromId(pixel));
            }
        }
        return showerPixel;
    }

    /**
     * add all neighboring pixels of the core pixels, with a weight > neighborPixelThreshold to the showerpixellist
     * @param showerPixel 'HashSet containing the so far identified shower pixels'
     * @param photons
     * @return
     */
    public HashSet<CameraPixel> addNeighboringPixels(HashSet<CameraPixel> showerPixel, double[] photons, double neighborPixelThreshold)
    {
        HashSet<CameraPixel> neighbouringPixel = new HashSet<>();
        for (CameraPixel pix : showerPixel){
            ArrayList<FactCameraPixel> currentNeighbors = pixelMap.getNeighboursForPixel(pix);
            currentNeighbors.stream().filter(nPix -> photons[nPix.id] > neighborPixelThreshold).forEach(neighbouringPixel::add);
        }
        showerPixel.addAll(neighbouringPixel);
        return showerPixel;
    }


    /**
     * Remove all clusters of pixels with less than minNumberOfPixel pixels in the cluster
     * @param showerPixel 'HashSet containing the so far identified shower pixels'
     * @param minNumberOfPixel
     * @return
     */
    public HashSet<CameraPixel> removeSmallCluster(HashSet<CameraPixel> showerPixel, int minNumberOfPixel)
    {

        ArrayList<CameraPixel> listOfShowerPixel = Lists.newArrayList(showerPixel);

        ArrayList<ArrayList<CameraPixel>> listOfLists = pixelMap.breadthFirstSearch(listOfShowerPixel);

        for (ArrayList<CameraPixel> l: listOfLists){
            if(l.size() <= minNumberOfPixel){
                showerPixel.removeAll(l);
            }
        }

        return showerPixel;
    }


}

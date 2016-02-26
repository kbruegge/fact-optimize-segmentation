/**
 *
 */
package hexmap;

import com.google.common.collect.Ordering;
import stream.Data;
import stream.io.CsvStream;
import stream.io.SourceURL;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static java.lang.Math.abs;

/**
 * This class provides a mapping between different Pixel ids and geometric information from the
 * camera layout.
 *
 * This class can get instantiated as a singleton with the getInstance() method.
 *
 * The geometric coordinates stored in the text file to build this default map are stored in the "odd -q" vertical layout.
 * Other PixelMappings can be stored
 * See http://www.redblobgames.com/grids/hexagons/ for details and pictures.
 *
 * @author kai
 */
public abstract class AbstractPixelMapping<T extends CameraPixel> {

    public enum Orientation {
        FLAT_TOP(0),
        POINTY_TOP(1);

        private final int orientation;

        Orientation(int i) {
            this.orientation = i;
        }
    }


    protected Orientation orientation = Orientation.FLAT_TOP;


    //offsets to neigbouring pixels in axial coordinates.
    private final int[][] neighbourOffsets = {
            {+1, 0}, {+1, -1}, {0, -1}, {-1, 0}, {-1, +1}, {0, +1}
    };

    // list containing all pixels in the camera
    private  List<T> cameraPixels;


    //This array contains the camera pixels in axial layout. I don't care about unused entries.
    private T[][] axialGrid;
    int qOffset = 0;
    int rOffset = 0;


    /**
     * Concrete PixelMapping implementations return the number of pixels they use.
     * @return the number of pixel in the camera.
     */
    public abstract int getNumberOfPixel();

    /**
     * For each row in the .csv which specifies the camera geometry, this method gets called.
     * @param item
     * @return A concrete implmentation of a CameraPixel
     */
    protected abstract T getPixelFromCSVItem(Data item);


    public ArrayList<T> getNeighboursForPixelId(int pixelId) {
        return getNeighboursForPixel(getPixelFromId(pixelId));
    }


    public T getPixelFromId(int id){
        return cameraPixels.get(id);
    }


    /**
     * This expects a file containing information on all of the pixel
     * @param mapping url to the mapping file
     */
    protected void load(URL mapping) throws IOException {
        //use the csv stream to read stuff from the csv file
        CsvStream stream = null;
        try {
            stream = new CsvStream(new SourceURL(mapping), ",");
            stream.init();
        } catch (Exception e){
            e.printStackTrace();
            throw new IOException("Could not load pixel positions from file " + mapping);
        }


        ArrayList<T> pixels = new ArrayList<>(getNumberOfPixel());

        int maxQ = Integer.MIN_VALUE;
        int minQ = Integer.MAX_VALUE;

        int maxR = Integer.MIN_VALUE;
        int minR = Integer.MAX_VALUE;

        for (int i = 0; i < getNumberOfPixel(); i++) {
            Data item = null;
            try {
                item = stream.readNext();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Could not parse pixel positions from file " + mapping);
            }
            T p = getPixelFromCSVItem(item);

            maxQ = Math.max(maxQ, p.axialQ);
            minQ = Math.min(minQ, p.axialQ);

            maxR = Math.max(maxR, p.axialR);
            minR = Math.min(minR, p.axialR);
            pixels.add(p);
        }

        cameraPixels = Ordering.natural().onResultOf(o -> ((CameraPixel) o).id).sortedCopy(pixels);

        qOffset = abs(minQ);
        rOffset = abs(minR);

        //noinspection unchecked
        axialGrid = (T[][]) new CameraPixel[abs(minQ) + maxQ + 1][abs(minR) + maxR + 1];

        for (T pixel : this.cameraPixels){
            axialGrid[pixel.axialQ + abs(minQ)][pixel.axialR + abs(minR)] = pixel;
        }
    }

    private T getPixelFromAxialCoordinates(int q, int r) {
        if(q + qOffset < 0 || r + rOffset < 0 ||(q + qOffset) >= axialGrid.length || (r + rOffset) >= axialGrid[0].length) {
            return null;
        }
        return axialGrid[q + qOffset][r + rOffset];
    }

    /**
     * Get pixels which are adjacent to the given pixel.
     * @param p the pixel to find neighbours to
     * @return a list of adjacent pixels
     */
    public ArrayList<T> getNeighboursForPixel(CameraPixel p) {
        ArrayList<T> l = new ArrayList<>();
        //check if x coordinate is even or not
        //int parity = (p.offsetCoordinateX & 1);
        //get the neighbour in each direction and store them in the list
        for (int direction = 0; direction <= 5; direction++) {
            int[] d = neighbourOffsets[direction];
            T np = getPixelFromAxialCoordinates(p.axialQ + d[0], p.axialR + d[1]);
            if (np != null){
                l.add(np);
            }
        }
        return l;
    }

    /**
     * All pixels which are in the camera
     * @return The list containing all pixel objects.
     */
    public List<T> getAllPixel(){
        return cameraPixels;
    }

    protected int[] getAxialCoordinatesFromRealWorldCoordinatesInMM(double xCoordinate, double yCoordinate){
//        xCoordinate /= 9.5;
//        yCoordinate /=- 9.5;
        yCoordinate += 0.5;
        //distance from center to corner
        double axial_r = 0;
        double axial_q = 0;

        double factor = 1/ (Math.sqrt(3));
        double size  = 9.5 * factor;

        if (orientation == Orientation.FLAT_TOP) {
            axial_q = 2.0 / 3.0 * xCoordinate / size;
            axial_r = (-xCoordinate/3.0  + factor*yCoordinate) / size;
        } else if (orientation == Orientation.POINTY_TOP) {
            axial_q = (factor * xCoordinate - yCoordinate/3.0) / size;
            axial_r = 2.0 / 3.0 * yCoordinate / size;
        }


        double cube_x = axial_q;
        double cube_z = axial_r;
        double cube_y = -cube_x-cube_z;


        //now round maybe violating the constraint
        int rx = (int) Math.round(cube_x);
        int rz = (int) Math.round(cube_z);
        int ry = (int) Math.round(cube_y);

        //artificially fix the constraint.
        double x_diff = abs(rx -cube_x);
        double z_diff = abs(rz -cube_z);
        double y_diff = abs(ry -cube_y);

        if(x_diff > y_diff && x_diff > z_diff){
            rx = -ry-rz;
        } else if(y_diff > z_diff){
            ry = -rx-rz;
        } else {
            rz = -rx-ry;
        }

        //convert the cube coordinate back to axial coordiantes
        int q = rx;
        int r = rz;
        return new int[]{q, r};
    }


    /**
     * Get the FactCameraPixel sitting below the coordinates passed to the method.
     * The center of the coordinate system in the camera is the center of the camera.
     *
     * @param xCoordinate the real world x coordinate in mm
     * @param yCoordinate the real world y coordinate in mm
     * @return The pixel below the point or NULL if the pixel does not exist.
     */
    public T getPixelBelowCoordinatesInMM(double xCoordinate, double yCoordinate){
        //get some pixel near the point provided
        //in pixel units

        int[] ax = getAxialCoordinatesFromRealWorldCoordinatesInMM(xCoordinate, yCoordinate);

        return getPixelFromAxialCoordinates(ax[0], ax[1]);
    }



    /**
     * Finds all unconnected sets of pixel in the showerPixel List and returns a
     * list of lists. Each list containing one separate set. Does a BFs search.
     * See the wikipedia article on BFS. This version is not as memory efficient
     * as it could be.
     *
     * @param showerPixel
     *            the list to search in
     * @return A list of lists.
     */
    public ArrayList<ArrayList<CameraPixel>> breadthFirstSearch(Collection<CameraPixel> showerPixel) {
        ArrayList<ArrayList<CameraPixel>> listOfIslands = new ArrayList<>();
        HashSet<CameraPixel> marked = new HashSet<>();

        for (CameraPixel pix : showerPixel) {
            if (!marked.contains(pix)) {
                // start BFS
                marked.add(pix);
                ArrayList<CameraPixel> q = new ArrayList<>();
                q.add(pix);
                for (int index = 0; index < q.size() && !q.isEmpty(); index++) {
                    // add neighbours to q
                    ArrayList<T> neighbors = getNeighboursForPixel(q.get(index));
                    for (T i : neighbors) {
                        if (showerPixel.contains(i)
                                && !marked.contains(i)) {
                            q.add(i);
                            marked.add(i);
                        }
                    }
                }
                listOfIslands.add(q);
            }
        }
        return listOfIslands;
    }
}
